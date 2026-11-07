package com.example.template.items.entity;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ItemTests {

    @Test
    void updateReplacesFieldsAndStripsName() {
        var item = new Item("Old name", "Old description", BigDecimal.ONE);
        item.update("  New name  ", null, new BigDecimal("2.50"));
        assertEquals("New name", item.name());
        assertNull(item.description());
        assertEquals(new BigDecimal("2.50"), item.price());
    }

    @Test
    void updateRejectsInvalidValuesAndKeepsPreviousOnes() {
        var item = new Item("Notebook", "Example", BigDecimal.ONE);
        assertThrows(IllegalArgumentException.class, () -> item.update("  ", "Example", BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () -> item.update("Notebook", null, new BigDecimal("-0.01")));
        assertEquals("Notebook", item.name());
        assertEquals("Example", item.description());
        assertEquals(BigDecimal.ONE, item.price());
    }
}
