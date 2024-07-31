package com.transaction.statistic.app.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import lombok.Data;

/**
 * Statistics class maintains and provides aggregate statistics for a set of transactions.
 * It keeps track of the sum, average, maximum, minimum, and count of transaction amounts.
 * 
 * Author: [Okpapi Samuel]
 */
@Data
public class Statistics {
    // Initial minimum value to be used when no transactions are present
    private static final BigDecimal INITIAL_MIN = BigDecimal.ZERO;
    
    // AtomicReference for sum of transaction amounts
    private final AtomicReference<BigDecimal> sum = new AtomicReference<>(BigDecimal.ZERO);
    
    // AtomicReference for average transaction amount
    private final AtomicReference<BigDecimal> avg = new AtomicReference<>(BigDecimal.ZERO);
    
    // AtomicReference for the maximum transaction amount
    private final AtomicReference<BigDecimal> max = new AtomicReference<>(BigDecimal.ZERO);
    
    // AtomicReference for the minimum transaction amount
    private final AtomicReference<BigDecimal> min = new AtomicReference<>(INITIAL_MIN);
    
    // AtomicLong for the count of transactions
    private final AtomicLong count = new AtomicLong(0);

    /**
     * Updates the statistics with a new transaction amount.
     * @param amount the amount of the new transaction
     */
    public synchronized void update(BigDecimal amount) {
        // Update sum with the new transaction amount
        sum.updateAndGet(current -> current.add(amount));
        
        // Increment the count of transactions
        count.incrementAndGet();
        
        // Update average transaction amount
        avg.set(sum.get().divide(BigDecimal.valueOf(count.get()), 2, RoundingMode.HALF_UP));
        
        // Update maximum transaction amount if necessary
        max.updateAndGet(current -> amount.max(current));
        
        // Update minimum transaction amount if necessary
        min.updateAndGet(current -> amount.min(current));
    }

    /**
     * Resets the statistics to their initial state.
     */
    public synchronized void reset() {
        // Reset sum to zero
        sum.set(BigDecimal.ZERO);
        
        // Reset average to zero
        avg.set(BigDecimal.ZERO);
        
        // Reset maximum to zero
        max.set(BigDecimal.ZERO);
        
        // Reset minimum to initial value (zero)
        min.set(INITIAL_MIN);
        
        // Reset count to zero
        count.set(0);
    }

    /**
     * Returns the sum of transaction amounts with two decimal places and rounding mode HALF_UP.
     * @return the sum of transaction amounts
     */
    public BigDecimal getSum() {
        return sum.get().setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Returns the average transaction amount with two decimal places and rounding mode HALF_UP.
     * @return the average transaction amount
     */
    public BigDecimal getAvg() {
        return avg.get().setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Returns the maximum transaction amount with two decimal places and rounding mode HALF_UP.
     * @return the maximum transaction amount
     */
    public BigDecimal getMax() {
        return max.get().setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Returns the minimum transaction amount with two decimal places and rounding mode HALF_UP.
     * If no transactions have been recorded, returns INITIAL_MIN (zero).
     * @return the minimum transaction amount
     */
    public BigDecimal getMin() {
        // Return INITIAL_MIN if count is 0, otherwise return current minimum
        return count.get() > 0 ? min.get().setScale(2, RoundingMode.HALF_UP) : INITIAL_MIN.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Returns the count of transactions.
     * @return the count of transactions
     */
    public long getCount() {
        return count.get();
    }
}
