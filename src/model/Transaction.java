package model;

import java.time.LocalDateTime;

public class Transaction{
    private final int fromId;
    private final int toId;
    private final double amount;
    private final boolean success;
    private final LocalDateTime timestamp;

    public Transaction(int fromId, int toId, double amount, boolean success) {
        this.fromId = fromId;
        this.toId = toId;
        this.amount = amount;
        this.success = success;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | %d -> %d | Amt: %.2f", 
            timestamp, success ? "SUCCESS" : "FAILED", fromId, toId, amount);
    }
}