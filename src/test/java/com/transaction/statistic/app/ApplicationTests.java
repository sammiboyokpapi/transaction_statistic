package com.transaction.statistic.app;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.transaction.statistic.app.model.Transaction;
import com.transaction.statistic.app.service.TransactionService;

class ApplicationTests {

    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionService();
    }

    @Test
    void addTransaction() {
        Transaction transaction = new Transaction(BigDecimal.valueOf(100.00), Instant.now());
        boolean result = transactionService.addTransaction(transaction);
        assertTrue(result);
        assertEquals(1L, transactionService.getStatistics().getCount());
        assertEquals(BigDecimal.valueOf(100.00).setScale(2, BigDecimal.ROUND_HALF_UP), transactionService.getStatistics().getSum());
    }

    @Test
    void getStatistics() {
        Transaction transaction1 = new Transaction(BigDecimal.valueOf(100.00), Instant.now());
        Transaction transaction2 = new Transaction(BigDecimal.valueOf(200.00), Instant.now());
        transactionService.addTransaction(transaction1);
        transactionService.addTransaction(transaction2);

        assertEquals(2L, transactionService.getStatistics().getCount());
        assertEquals(BigDecimal.valueOf(300.00).setScale(2, BigDecimal.ROUND_HALF_UP), transactionService.getStatistics().getSum());
    }

    @Test
    void deleteTransactions() {
        Transaction transaction1 = new Transaction(BigDecimal.valueOf(100.00), Instant.now());
        Transaction transaction2 = new Transaction(BigDecimal.valueOf(200.00), Instant.now());
        transactionService.addTransaction(transaction1);
        transactionService.addTransaction(transaction2);
        transactionService.deleteTransactions();

        assertEquals(0L, transactionService.getStatistics().getCount());
        assertEquals(BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP), transactionService.getStatistics().getSum());
    }
}
