package com.transaction.statistic.app.service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.transaction.statistic.app.model.Statistics;
import com.transaction.statistic.app.model.Transaction;

/**
 * Service class for managing transactions and calculating statistics.
 * This class is responsible for:
 * - Adding transactions
 * - Providing statistics on transactions
 * - Deleting transactions
 * - Purging old transactions periodically
 * 
 * Author: [Okpapi Samuel]
 */
@Service
public class TransactionService {

    // ConcurrentHashMap to store transactions with their timestamp as the key
    private final Map<Instant, Transaction> transactions = new ConcurrentHashMap<>();
    
    // AtomicReference to store and manage the current statistics
    private final AtomicReference<Statistics> statistics = new AtomicReference<>(new Statistics());

    /**
     * Adds a new transaction to the service.
     * Validates the transaction timestamp and updates the statistics.
     * @param transaction the transaction to be added
     * @return true if the transaction was added successfully, false otherwise
     */
    public boolean addTransaction(Transaction transaction) {
        Instant now = Instant.now();

        // Validate if the transaction timestamp is in the future
        if (transaction.getTimestamp().isAfter(now)) {
            return false; // Timestamp is in the future
        }

        // Validate if the transaction timestamp is older than 30 seconds
        if (transaction.getTimestamp().isBefore(now.minusSeconds(30))) {
            return false; // Timestamp is older than 30 seconds
        }

        // Add transaction to the ConcurrentHashMap
        transactions.put(transaction.getTimestamp(), transaction);

        // Update statistics with the new transaction amount
        Statistics currentStats = statistics.get();
        currentStats.update(transaction.getAmount());
        statistics.set(currentStats);

        return true;
    }

    /**
     * Retrieves the current statistics.
     * @return the current statistics
     */
    public Statistics getStatistics() {
        return statistics.get();
    }

    /**
     * Deletes all transactions and resets statistics.
     * This method clears the map of transactions and resets the statistics to initial values.
     */
    public void deleteTransactions() {
        transactions.clear();
        statistics.get().reset();
    }

    /**
     * Scheduled task to periodically purge old transactions.
     * Runs every second to remove transactions older than 30 seconds and updates statistics.
     */
    @Scheduled(fixedRate = 1000)
    private void purgeOldTransactions() {
        Instant now = Instant.now();
        
        // Remove transactions older than 30 seconds
        transactions.keySet().removeIf(timestamp -> timestamp.isBefore(now.minusSeconds(30)));
        
        // Update statistics after purging old transactions
        updateStatistics();
    }

    /**
     * Updates the statistics based on the current transactions.
     * Creates a new Statistics object, recalculates the aggregate statistics, and updates the AtomicReference.
     */
    private void updateStatistics() {
        Statistics newStats = new Statistics();
        
        // Recalculate statistics based on current transactions
        transactions.values().forEach(transaction -> newStats.update(transaction.getAmount()));
        
        // Update the statistics reference with the new statistics
        statistics.set(newStats);
    }
}
