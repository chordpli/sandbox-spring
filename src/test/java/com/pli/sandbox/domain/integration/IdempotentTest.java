package com.pli.sandbox.domain.integration;

import com.pli.sandbox.common.test.IntegrationTest;
import com.pli.sandbox.idempotency.domain.BankTransaction;
import com.pli.sandbox.idempotency.service.IdempotentService;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

@DisplayName("데이터 적재 멱등성 보장 전략별 성능 테스트")
public class IdempotentTest extends IntegrationTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private IdempotentService idempotentService;

    private static final int NUM_RUNS = 3;
    private static final int BATCH_SIZE = 1000;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM bank_transaction");
    }

    @Test
    @DisplayName("시나리오 1-1: Application-level SELECT 후 INSERT (인덱스 없음)")
    void testScenario1_1() throws Exception {
        List<Long> durations = new ArrayList<>();
        for (int i = 0; i < NUM_RUNS; i++) {
            jdbcTemplate.execute("DELETE FROM bank_transaction");
            long startTime = System.nanoTime();
            runScenario1Logic();
            long endTime = System.nanoTime();
            durations.add(endTime - startTime);
            verifyRecordCount("Scenario 1-1");
        }
        logPerformance("Scenario 1-1", durations);
    }

    private void runScenario1Logic() throws Exception {
        // Initial data load
        List<BankTransaction> initialData = readCsv("idempotent/dataset_a.csv");
        idempotentService.saveAll(initialData);

        // Test data load
        List<BankTransaction> testData = readCsv("idempotent/dataset_b.csv");

        for (BankTransaction transaction : testData) {
            if (!idempotentService.exists(transaction)) {
                idempotentService.saveAll(List.of(transaction));
            }
        }
    }

    @Test
    @DisplayName("시나리오 1-2: Application-level SELECT 후 INSERT (인덱스 있음)")
    void testScenario1_2() throws Exception {
        List<Long> durations = new ArrayList<>();
        for (int i = 0; i < NUM_RUNS; i++) {
            jdbcTemplate.execute("DELETE FROM bank_transaction");
            jdbcTemplate.execute(
                    "CREATE INDEX IF NOT EXISTS idx_bank_transaction_lookup ON bank_transaction (transaction_time, account_number, transaction_type, amount)");
            long startTime = System.nanoTime();
            runScenario1Logic();
            long endTime = System.nanoTime();
            durations.add(endTime - startTime);
            verifyRecordCount("Scenario 1-2");
            jdbcTemplate.execute("DROP INDEX IF EXISTS idx_bank_transaction_lookup");
        }
        logPerformance("Scenario 1-2", durations);
    }

    @Test
    @DisplayName("시나리오 2-1: ON CONFLICT DO NOTHING 활용 (복합 유니크 키, 인덱스 적용)")
    void testScenario2_1() throws Exception {
        jdbcTemplate.execute("ALTER TABLE bank_transaction DROP CONSTRAINT IF EXISTS uk_transaction_composite");
        jdbcTemplate.execute(
                "ALTER TABLE bank_transaction ADD CONSTRAINT uk_transaction_composite UNIQUE (transaction_time, account_number, transaction_type, amount)");

        List<Long> durations = new ArrayList<>();
        List<BankTransaction> initialData = readCsv("idempotent/dataset_a.csv");
        List<BankTransaction> testData = readCsv("idempotent/dataset_b.csv");

        for (int i = 0; i < NUM_RUNS; i++) {
            jdbcTemplate.execute("DELETE FROM bank_transaction");
            idempotentService.saveAll(initialData);

            long startTime = System.nanoTime();
            String sql =
                    "INSERT INTO bank_transaction (transaction_time, account_number, transaction_type, amount, balance, counterparty_name, memo, hash_value) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (transaction_time, account_number, transaction_type, amount) DO NOTHING";
            idempotentService.saveAllWithConflictResolution(testData, sql);
            long endTime = System.nanoTime();
            durations.add(endTime - startTime);
            verifyRecordCount("Scenario 2-1");
        }
        logPerformance("Scenario 2-1", durations);
        jdbcTemplate.execute("ALTER TABLE bank_transaction DROP CONSTRAINT IF EXISTS uk_transaction_composite");
    }

    @Test
    @DisplayName("시나리오 3-1: ON CONFLICT DO NOTHING 활용 (Hashed Unique Index)")
    void testScenario3_1() throws Exception {
        jdbcTemplate.execute("DROP INDEX IF EXISTS uk_transaction_hash");
        jdbcTemplate.execute("CREATE UNIQUE INDEX uk_transaction_hash ON bank_transaction (hash_value)");

        List<Long> durations = new ArrayList<>();
        List<BankTransaction> initialData = readCsv("idempotent/dataset_a.csv");
        List<BankTransaction> testData = readCsv("idempotent/dataset_b.csv");

        for (int i = 0; i < NUM_RUNS; i++) {
            jdbcTemplate.execute("DELETE FROM bank_transaction");
            idempotentService.saveAll(initialData);

            long startTime = System.nanoTime();
            String sql =
                    "INSERT INTO bank_transaction (transaction_time, account_number, transaction_type, amount, balance, counterparty_name, memo, hash_value) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (hash_value) DO NOTHING";
            idempotentService.saveAllWithConflictResolution(testData, sql);
            long endTime = System.nanoTime();
            durations.add(endTime - startTime);
            verifyRecordCount("Scenario 3-1");
        }
        logPerformance("Scenario 3-1", durations);
        jdbcTemplate.execute("DROP INDEX IF EXISTS uk_transaction_hash");
    }

    @Test
    @DisplayName("시나리오 4-1: Staging Table을 이용한 MERGE")
    void testScenario4_1() throws Exception {
        jdbcTemplate.execute("DROP TABLE IF EXISTS bank_transaction_staging");
        jdbcTemplate.execute(
                """
                CREATE TABLE bank_transaction_staging (
                    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                    transaction_time TIMESTAMP(6),
                    account_number VARCHAR(30),
                    transaction_type VARCHAR(10),
                    amount DECIMAL(18,2),
                    balance DECIMAL(18,2),
                    counterparty_name VARCHAR(100),
                    memo VARCHAR(255),
                    hash_value VARCHAR(64)
                );
                """);
        List<Long> durations = new ArrayList<>();
        List<BankTransaction> initialData = readCsv("idempotent/dataset_a.csv");
        List<BankTransaction> testData = readCsv("idempotent/dataset_b.csv");

        for (int i = 0; i < NUM_RUNS; i++) {
            jdbcTemplate.execute("DELETE FROM bank_transaction");
            jdbcTemplate.execute("DELETE FROM bank_transaction_staging");
            idempotentService.saveAll(initialData);

            long startTime = System.nanoTime();
            idempotentService.saveAllToStagingAndMerge(testData);
            long endTime = System.nanoTime();
            durations.add(endTime - startTime);
            verifyRecordCount("Scenario 4-1");
        }
        logPerformance("Scenario 4-1", durations);
    }

    private List<BankTransaction> readCsv(String filePath) throws Exception {
        List<BankTransaction> rows = new ArrayList<>();
        ClassPathResource resource = new ClassPathResource(filePath);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                BankTransaction transaction = BankTransaction.builder()
                        .transactionTime(LocalDateTime.parse(values[0], formatter))
                        .accountNumber(values[1])
                        .transactionType(values[2])
                        .amount(new BigDecimal(values[3]))
                        .balance(new BigDecimal(values[4]))
                        .counterpartyName(values[5])
                        .memo(values[6])
                        .hashValue(generateHash(values[0], values[1], values[2], values[3]))
                        .build();
                rows.add(transaction);
            }
        }
        return rows;
    }

    private String generateHash(String... values) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        StringJoiner joiner = new StringJoiner("|");
        for (String value : values) {
            joiner.add(value);
        }
        byte[] hash = digest.digest(joiner.toString().getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private void logPerformance(String scenario, List<Long> durationsNano) throws java.io.IOException {
        long totalDurationNano =
                durationsNano.stream().mapToLong(Long::longValue).sum();
        long averageDurationNano = totalDurationNano / durationsNano.size();
        long averageDurationMillis = TimeUnit.NANOSECONDS.toMillis(averageDurationNano);
        // 100,000건의 테스트 데이터(dataset_b)를 처리하는 RPS를 계산합니다.
        double rps = (double) 100000 / (averageDurationMillis / 1000.0);
        //        1027881
        java.io.File reportsDir = new java.io.File("idempotent_results/1차");
        if (!reportsDir.exists()) {
            reportsDir.mkdirs();
        }
        String fileName = scenario.replace(" ", "_") + "_result.txt";
        try (java.io.FileWriter writer = new java.io.FileWriter(new java.io.File(reportsDir, fileName))) {
            writer.write(String.format("[%s] Total runs: %d%n", scenario, NUM_RUNS));
            writer.write(String.format("[%s] Average execution time: %d ms%n", scenario, averageDurationMillis));
            writer.write(String.format("[%s] Average RPS: %.2f%n", scenario, rps));
            writer.write(String.format(
                    "[%s] All durations (ms): %s%n",
                    scenario,
                    durationsNano.stream()
                            .map(TimeUnit.NANOSECONDS::toMillis)
                            .map(String::valueOf)
                            .collect(java.util.stream.Collectors.joining(", "))));
        }
    }

    private void verifyRecordCount(String scenario) {
        long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM bank_transaction", Long.class);
        if (count != 110000) {
            throw new IllegalStateException(String.format(
                    "[%s] Record count verification failed. Expected 110000, but got %d", scenario, count));
        }
        System.out.printf("[%s] Record count verified: %d%n", scenario, count);
    }
}
