package info.sitnikov.finance.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public final class Amount {
    private final String id;
    private String categoryId;
    private String description;
    private double amount;
    private LocalDateTime date;

    public Amount(String categoryId, String description, double amount, LocalDateTime date) {
        this.id = UUID.randomUUID().toString();
        this.categoryId = categoryId;
        this.description = description;
        this.amount = amount;
        this.date = date;
    }
}
