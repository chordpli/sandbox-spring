package com.pli.sandbox.idempotency.repository;

import com.pli.sandbox.idempotency.domain.BankTransaction;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {
    boolean existsByTransactionTimeAndAccountNumberAndTransactionTypeAndAmount(
            LocalDateTime transactionTime, String accountNumber, String transactionType, BigDecimal amount);
}
