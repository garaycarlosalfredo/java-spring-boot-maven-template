package com.example.template.items.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Generated(event = EventType.INSERT)
    @Column(insertable = false, updatable = false, nullable = false)
    private Long sku;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    protected Item() {}

    public Item(String name, String description, BigDecimal price) {
        validateAndAssign(name, description, price);
    }

    public void update(String name, String description, BigDecimal price) {
        validateAndAssign(name, description, price);
    }

    private void validateAndAssign(String name, String description, BigDecimal price) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name is required");
        name = name.strip();
        if (name.length() > 100) throw new IllegalArgumentException("Name must have at most 100 characters");
        if (description != null && description.length() > 1000) throw new IllegalArgumentException("Description must have at most 1000 characters");
        if (price == null) throw new IllegalArgumentException("Price is required");
        if (price.signum() < 0) throw new IllegalArgumentException("Price must be nonnegative");
        if (price.precision() - price.scale() > 10 || price.scale() > 2) throw new IllegalArgumentException("Price must have at most 10 integer digits and 2 decimal places");
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Long id() { return id; }
    public Long sku() { return sku; }
    public String name() { return name; }
    public String description() { return description; }
    public BigDecimal price() { return price; }
}
