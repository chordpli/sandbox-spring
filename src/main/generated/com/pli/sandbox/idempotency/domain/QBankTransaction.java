package com.pli.sandbox.idempotency.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBankTransaction is a Querydsl query type for BankTransaction
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBankTransaction extends EntityPathBase<BankTransaction> {

    private static final long serialVersionUID = -1409050326L;

    public static final QBankTransaction bankTransaction = new QBankTransaction("bankTransaction");

    public final StringPath accountNumber = createString("accountNumber");

    public final NumberPath<java.math.BigDecimal> amount = createNumber("amount", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> balance = createNumber("balance", java.math.BigDecimal.class);

    public final StringPath counterpartyName = createString("counterpartyName");

    public final StringPath hashValue = createString("hashValue");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath memo = createString("memo");

    public final DateTimePath<java.time.LocalDateTime> transactionTime = createDateTime("transactionTime", java.time.LocalDateTime.class);

    public final StringPath transactionType = createString("transactionType");

    public QBankTransaction(String variable) {
        super(BankTransaction.class, forVariable(variable));
    }

    public QBankTransaction(Path<? extends BankTransaction> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBankTransaction(PathMetadata metadata) {
        super(BankTransaction.class, metadata);
    }

}

