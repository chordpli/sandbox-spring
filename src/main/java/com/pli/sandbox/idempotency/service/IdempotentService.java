package com.pli.sandbox.idempotency.service;

import com.pli.sandbox.idempotency.domain.BankTransaction;
import com.pli.sandbox.idempotency.repository.BankTransactionRepository;
import java.sql.Timestamp;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IdempotentService {

    private final BankTransactionRepository bankTransactionRepository;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(List<BankTransaction> transactions) {
        bankTransactionRepository.saveAll(transactions);
    }

    @Transactional(readOnly = true)
    public boolean exists(BankTransaction transaction) {
        return bankTransactionRepository.existsByTransactionTimeAndAccountNumberAndTransactionTypeAndAmount(
                transaction.getTransactionTime(),
                transaction.getAccountNumber(),
                transaction.getTransactionType(),
                transaction.getAmount());
    }

    @Transactional
    public void saveAllWithConflictResolution(List<BankTransaction> transactions, String conflictSql) {
        jdbcTemplate.batchUpdate(conflictSql, transactions, transactions.size(), (ps, transaction) -> {
            ps.setTimestamp(1, Timestamp.valueOf(transaction.getTransactionTime()));
            ps.setString(2, transaction.getAccountNumber());
            ps.setString(3, transaction.getTransactionType());
            ps.setBigDecimal(4, transaction.getAmount());
            ps.setBigDecimal(5, transaction.getBalance());
            ps.setString(6, transaction.getCounterpartyName());
            ps.setString(7, transaction.getMemo());
            if (conflictSql.contains("hash_value")) {
                ps.setString(8, transaction.getHashValue());
            }
        });
    }

    @Transactional
    public void saveAllToStagingAndMerge(List<BankTransaction> transactions) {
        // 1. Load to staging table
        String insertStagingSql =
                "INSERT INTO bank_transaction_staging (transaction_time, account_number, transaction_type, amount, balance, counterparty_name, memo) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(insertStagingSql, transactions, transactions.size(), (ps, transaction) -> {
            ps.setTimestamp(1, Timestamp.valueOf(transaction.getTransactionTime()));
            ps.setString(2, transaction.getAccountNumber());
            ps.setString(3, transaction.getTransactionType());
            ps.setBigDecimal(4, transaction.getAmount());
            ps.setBigDecimal(5, transaction.getBalance());
            ps.setString(6, transaction.getCounterpartyName());
            ps.setString(7, transaction.getMemo());
        });

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
    }
}
