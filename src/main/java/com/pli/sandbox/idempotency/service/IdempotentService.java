package com.pli.sandbox.idempotency.service;

import com.pli.sandbox.idempotency.domain.BankTransaction;
import com.pli.sandbox.idempotency.repository.BankTransactionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IdempotentService {

    private final BankTransactionRepository bankTransactionRepository;

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
}
