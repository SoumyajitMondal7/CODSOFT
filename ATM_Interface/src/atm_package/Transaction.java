package atm_package;

import java.time.LocalDateTime;

public class Transaction {
    private LocalDateTime timestamp;
    private String type;
    private double amount;

    public Transaction(String type, double amount) {
        this.timestamp = LocalDateTime.now();
        this.type = type;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return timestamp + " - " + type + ": " + amount;
    }
}
