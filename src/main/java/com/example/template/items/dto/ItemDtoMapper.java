package com.example.template.items.dto;

import com.example.template.items.entity.Item;

public final class ItemDtoMapper {
    private ItemDtoMapper() {}

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.id(), item.sku(), item.name(), item.description(), item.price());
    }
}
