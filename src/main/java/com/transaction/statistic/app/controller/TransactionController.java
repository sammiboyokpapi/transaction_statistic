package com.transaction.statistic.app.controller;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeParseException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.transaction.statistic.app.model.Statistics;
import com.transaction.statistic.app.model.Transaction;
import com.transaction.statistic.app.service.TransactionService;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * REST controller for handling transaction-related requests.
 * Provides endpoints to add transactions, retrieve statistics, and delete transactions.
 * 
 * Author: [Okpapi Samuel]
 */
@RestController
@RequiredArgsConstructor
public class TransactionController {

    // Service for handling business logic related to transactions
    private final TransactionService transactionService;

    /**
     * Handles POST requests to add a new transaction.
     * Validates the input data and decides the appropriate HTTP response status.
     *
     * @param transactionRequest The request body containing transaction details.
     * @return ResponseEntity indicating the result of the operation.
     */
    @PostMapping("/transactions")
    public ResponseEntity<Void> addTransaction(@RequestBody TransactionRequest transactionRequest) {
        try {
            // Parse and validate amount and timestamp from request
            BigDecimal amount = new BigDecimal(transactionRequest.getAmount());
            Instant timestamp = Instant.parse(transactionRequest.getTimestamp());
            
            // Create a transaction object
            Transaction transaction = new Transaction(amount, timestamp);
            
            // Add the transaction and check if it was accepted
            boolean isAccepted = transactionService.addTransaction(transaction);
            if (isAccepted) {
                // Return 201 Created if the transaction was successfully added
                return ResponseEntity.status(HttpStatus.CREATED).build();
            } else {
                // Return 204 No Content if the transaction is older than 30 seconds
                if (timestamp.isBefore(Instant.now().minusSeconds(30))) {
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
                } else {
                    // Return 422 Unprocessable Entity if the transaction is not accepted
                    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
                }
            }
        } catch (NumberFormatException | DateTimeParseException e) {
            // Return 422 Unprocessable Entity if the input data is invalid
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
    }

    /**
     * Handles GET requests to retrieve statistics based on transactions in the last 30 seconds.
     *
     * @return ResponseEntity containing the statistics response.
     */
    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> getStatistics() {
        // Retrieve current statistics from the service
        Statistics statistics = transactionService.getStatistics();
        
        // Create response object with statistics data
        StatisticsResponse response = new StatisticsResponse(
                statistics.getSum(),
                statistics.getAvg(),
                statistics.getMax(),
                statistics.getMin(),
                statistics.getCount()
        );
        
        // Return 200 OK with statistics data
        return ResponseEntity.ok(response);
    }

    /**
     * Handles DELETE requests to remove all existing transactions.
     *
     * @return ResponseEntity indicating the result of the operation.
     */
    @DeleteMapping("/transactions")
    public ResponseEntity<Void> deleteTransactions() {
        // Clear all transactions and reset statistics
        transactionService.deleteTransactions();
        
        // Return 204 No Content indicating successful deletion
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Global exception handler for invalid JSON input.
     * Returns a 400 Bad Request status when JSON parsing fails.
     *
     * @param e The exception thrown due to invalid JSON.
     * @return ResponseEntity indicating a bad request.
     */
    @ExceptionHandler
    public ResponseEntity<Void> handleInvalidJson(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    /**
     * Data class for holding transaction request details.
     */
    @Data
    public static class TransactionRequest {
        private String amount;
        private String timestamp;
    }

    /**
     * Data class for holding statistics response details.
     */
    @Data
    @RequiredArgsConstructor
    public static class StatisticsResponse {
        private final BigDecimal sum;
        private final BigDecimal avg;
        private final BigDecimal max;
        private final BigDecimal min;
        private final long count;
    }
}
