package com.example.template.items.dto;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UpdateItemRequestTests {

    @Test
    void stripsNameWithoutChangingInternalSpacesOrDescription() {
        var request = new UpdateItemRequest("  Example  item  ", "  Description  ", BigDecimal.ONE);
        assertEquals("Example  item", request.name());
        assertEquals("  Description  ", request.description());
    }

    @Test
    void preservesMissingNameForValidation() {
        assertNull(new UpdateItemRequest(null, null, BigDecimal.ZERO).name());
    }
}
