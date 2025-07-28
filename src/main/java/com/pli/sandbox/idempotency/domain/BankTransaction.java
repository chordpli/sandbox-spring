package com.pli.sandbox.idempotency.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "bank_transaction")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BankTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime transactionTime;

    private String accountNumber;

    private String transactionType;

    private BigDecimal amount;

    private BigDecimal balance;

    private String counterpartyName;

    private String memo;

    private String hashValue;

    @Builder
    public BankTransaction(
            LocalDateTime transactionTime,
            String accountNumber,
            String transactionType,
            BigDecimal amount,
            BigDecimal balance,
            String counterpartyName,
            String memo,
            String hashValue) {
        this.transactionTime = transactionTime;
        this.accountNumber = accountNumber;
        this.transactionType = transactionType;
        this.amount = amount;
        this.balance = balance;
        this.counterpartyName = counterpartyName;
        this.memo = memo;
        this.hashValue = hashValue;
    }
}
