package com.example.template.items.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateItemRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must have at most 100 characters")
        String name,
        @Size(max = 1000, message = "Description must have at most 1000 characters")
        String description,
        @NotNull(message = "Price is required")
        @DecimalMin(value = "0", message = "Price must be nonnegative")
        @Digits(integer = 10, fraction = 2, message = "Price must have at most 10 integer digits and 2 decimal places")
        BigDecimal price) {

    public UpdateItemRequest {
        name = name == null ? null : name.strip();
    }
}
