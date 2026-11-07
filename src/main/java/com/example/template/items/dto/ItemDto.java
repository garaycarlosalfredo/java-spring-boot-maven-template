package com.example.template.items.dto;

import java.math.BigDecimal;

public record ItemDto(Long id, Long sku, String name, String description, BigDecimal price) {}
