package com.pli.sandbox.domain.integration;

import com.pli.sandbox.idempotency.domain.BankTransaction;
import com.pli.sandbox.idempotency.service.IdempotentService;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
@DisplayName("데이터 적재 멱등성 보장 전략별 성능 테스트")
public class IdempotentTest {

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
        jdbcTemplate.execute("TRUNCATE TABLE bank_transaction");
        // Rely on Spring Boot's ddl-auto for schema management
    }

    @Test
    @DisplayName("시나리오 1-1: Application-level SELECT 후 INSERT (인덱스 없음)")
    void testScenario1_1() throws Exception {
        List<Long> durations = new ArrayList<>();
        for (int i = 0; i < NUM_RUNS; i++) {
            jdbcTemplate.execute("TRUNCATE TABLE bank_transaction");
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
            jdbcTemplate.execute("TRUNCATE TABLE bank_transaction");
            jdbcTemplate.execute(
                    "CREATE INDEX idx_bank_transaction_lookup ON bank_transaction (transaction_time, account_number, transaction_type, amount)");
            long startTime = System.nanoTime();
            runScenario1Logic();
            long endTime = System.nanoTime();
            durations.add(endTime - startTime);
            verifyRecordCount("Scenario 1-2");
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
        for (int i = 0; i < NUM_RUNS; i++) {
            jdbcTemplate.execute("TRUNCATE TABLE bank_transaction");
            // Initial data load
            List<BankTransaction> initialData = readCsv("idempotent/dataset_a.csv");
            idempotentService.saveAll(initialData);

            // Test data load
            List<BankTransaction> testData = readCsv("idempotent/dataset_b.csv");

            long startTime = System.nanoTime();
            String sql =
                    "INSERT INTO bank_transaction (transaction_time, account_number, transaction_type, amount, balance, counterparty_name, memo) VALUES (?, ?, ?, ?, ?, ?, ?) ON CONFLICT (transaction_time, account_number, transaction_type, amount) DO NOTHING";
            idempotentService.saveAllWithConflictResolution(testData, sql);
            long endTime = System.nanoTime();
            durations.add(endTime - startTime);
            verifyRecordCount("Scenario 2-1");
        }
        logPerformance("Scenario 2-1", durations);
    }

    @Test
    @DisplayName("시나리오 3-1: ON CONFLICT DO NOTHING 활용 (Hashed Unique Index)")
    void testScenario3_1() throws Exception {
        jdbcTemplate.execute("DROP INDEX IF EXISTS uk_transaction_hash");
        jdbcTemplate.execute("CREATE UNIQUE INDEX uk_transaction_hash ON bank_transaction (hash_value)");

        List<Long> durations = new ArrayList<>();
        for (int i = 0; i < NUM_RUNS; i++) {
            jdbcTemplate.execute("TRUNCATE TABLE bank_transaction");
            // Initial data load
            List<BankTransaction> initialData = readCsv("idempotent/dataset_a.csv");
            idempotentService.saveAll(initialData);

            // Test data load
            List<BankTransaction> testData = readCsv("idempotent/dataset_b.csv");

            long startTime = System.nanoTime();
            String sql =
                    "INSERT INTO bank_transaction (transaction_time, account_number, transaction_type, amount, balance, counterparty_name, memo, hash_value) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (hash_value) DO NOTHING";
            idempotentService.saveAllWithConflictResolution(testData, sql);
            long endTime = System.nanoTime();
            durations.add(endTime - startTime);
            verifyRecordCount("Scenario 3-1");
        }
        logPerformance("Scenario 3-1", durations);
    }

    @Test
    @DisplayName("시나리오 4-1: Staging Table을 이용한 MERGE")
    void testScenario4_1() throws Exception {
        jdbcTemplate.execute("DROP TABLE IF EXISTS bank_transaction_staging");
        jdbcTemplate.execute(
                "CREATE TABLE bank_transaction_staging (" + "id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,"
                        + "transaction_time TIMESTAMP,"
                        + "account_number VARCHAR(255),"
                        + "transaction_type VARCHAR(255),"
                        + "amount DECIMAL(19,2),"
                        + "balance DECIMAL(19,2),"
                        + "counterparty_name VARCHAR(255),"
                        + "memo VARCHAR(255),"
                        + "hash_value VARCHAR(255)"
                        + ")");
        // Initial data load
        List<BankTransaction> initialData = readCsv("idempotent/dataset_a.csv");
        idempotentService.saveAll(initialData);

        // Test data load
        List<BankTransaction> testData = readCsv("idempotent/dataset_b.csv");

        long startTime = System.nanoTime();
        // 1. Load to staging table
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            String sql =
                    "INSERT INTO bank_transaction_staging (transaction_time, account_number, transaction_type, amount, balance, counterparty_name, memo) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                for (int i = 0; i < testData.size(); i++) {
                    BankTransaction row = testData.get(i);
                    ps.setTimestamp(1, Timestamp.valueOf(row.getTransactionTime()));
                    ps.setString(2, row.getAccountNumber());
                    ps.setString(3, row.getTransactionType());
                    ps.setBigDecimal(4, row.getAmount());
                    ps.setBigDecimal(5, row.getBalance());
                    ps.setString(6, row.getCounterpartyName());
                    ps.setString(7, row.getMemo());
                    ps.addBatch();
                    if ((i + 1) % BATCH_SIZE == 0) {
                        ps.executeBatch();
                    }
                }
                ps.executeBatch();
            }
            connection.commit();
        }

        // 2. Merge
        jdbcTemplate.execute(
                "INSERT INTO bank_transaction (transaction_time, account_number, transaction_type, amount, balance, counterparty_name, memo) "
                        + "SELECT s.transaction_time, s.account_number, s.transaction_type, s.amount, s.balance, s.counterparty_name, s.memo "
                        + "FROM bank_transaction_staging s "
                        + "LEFT JOIN bank_transaction t ON "
                        + "s.transaction_time = t.transaction_time AND "
                        + "s.account_number = t.account_number AND "
                        + "s.transaction_type = t.transaction_type AND "
                        + "s.amount = t.amount "
                        + "WHERE t.id IS NULL");

        // 3. Truncate staging table
        jdbcTemplate.execute("TRUNCATE TABLE bank_transaction_staging");
        long endTime = System.nanoTime();
        logPerformance("Scenario 4-1", endTime - startTime);
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
        double rps = (double) (10000 * NUM_RUNS) / (averageDurationMillis / 1000.0);

        java.io.File reportsDir = new java.io.File("idempotent_results");
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
        if (count != 11000) {
            throw new IllegalStateException(String.format(
                    "[%s] Record count verification failed. Expected 11000, but got %d", scenario, count));
        }
        System.out.printf("[%s] Record count verified: %d%n", scenario, count);
    }
}
