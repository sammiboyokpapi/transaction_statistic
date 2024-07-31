package com.transaction.statistic.app.model;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents a transaction with a specific amount and timestamp.
 * This class is used to encapsulate the data for individual transactions.
 * 
 * Author: [Okpapi Samuel]
 */
@Data
@AllArgsConstructor
public class Transaction {
    
    /**
     * The amount of the transaction.
     * Stored as a BigDecimal to handle monetary values with precision.
     */
    private BigDecimal amount;

    /**
     * The timestamp of the transaction.
     * Stored as an Instant to represent the exact time in UTC when the transaction occurred.
     */
    private Instant timestamp;
}
