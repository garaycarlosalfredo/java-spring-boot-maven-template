package com.example.template.items.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.example.template.exception.ResourceNotFoundException;
import com.example.template.items.entity.Item;
import com.example.template.items.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ItemServiceTests {
    private final ItemRepository repository = mock(ItemRepository.class);
    private final ItemService service = new ItemService(repository);

    @Test
    void createsNormalizedItemAndReturnsSavedEntity() {
        var saved = mock(Item.class);
        when(repository.saveAndFlush(any(Item.class))).thenReturn(saved);
        assertSame(saved, service.create("  Example  item  ", "  Description  ", new BigDecimal("12.50")));
        var captor = org.mockito.ArgumentCaptor.forClass(Item.class);
        verify(repository).saveAndFlush(captor.capture());
        assertEquals("Example  item", captor.getValue().name());
        assertEquals("  Description  ", captor.getValue().description());
        assertEquals(new BigDecimal("12.50"), captor.getValue().price());
    }

    @Test
    void updateReplacesFieldsAndReturnsSavedEntity() {
        var item = new Item("Old name", "Old description", new BigDecimal("1.00"));
        when(repository.findById(1L)).thenReturn(Optional.of(item));
        when(repository.saveAndFlush(item)).thenReturn(item);
        assertSame(item, service.update(1L, "  New name  ", null, new BigDecimal("2.50")));
        assertEquals("New name", item.name());
        assertNull(item.description());
        assertEquals(new BigDecimal("2.50"), item.price());
        verify(repository).saveAndFlush(item);
    }

    @Test
    void throwsWhenUpdatingMissingItemAndSavesNothing() {
        when(repository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.update(999L, "Notebook", null, BigDecimal.ONE));
        verify(repository).findById(999L);
        verify(repository, never()).saveAndFlush(any(Item.class));
    }

    @Test
    void deletesExistingItem() {
        when(repository.existsById(1L)).thenReturn(true);
        service.delete(1L);
        verify(repository).existsById(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    void throwsWhenDeletingMissingItemAndDeletesNothing() {
        when(repository.existsById(999L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> service.delete(999L));
        verify(repository).existsById(999L);
        verify(repository, never()).deleteById(999L);
    }

    @ParameterizedTest
    @MethodSource("invalidDetails")
    void rejectsInvalidUpdateValuesBeforeSaving(String name, String description, BigDecimal price) {
        var item = new Item("Notebook", "Example", BigDecimal.ONE);
        when(repository.findById(1L)).thenReturn(Optional.of(item));
        assertThrows(IllegalArgumentException.class, () -> service.update(1L, name, description, price));
        verify(repository, never()).saveAndFlush(any(Item.class));
        assertEquals("Notebook", item.name());
        assertEquals("Example", item.description());
        assertEquals(BigDecimal.ONE, item.price());
    }

    @ParameterizedTest
    @MethodSource("invalidDetails")
    void rejectsInvalidDetailsBeforeSaving(String name, String description, BigDecimal price) {
        assertThrows(IllegalArgumentException.class, () -> service.create(name, description, price));
        verifyNoInteractions(repository);
    }

    static Stream<Arguments> invalidDetails() {
        return Stream.of(
                Arguments.of(null, null, BigDecimal.ONE),
                Arguments.of("  ", null, BigDecimal.ONE),
                Arguments.of("a".repeat(101), null, BigDecimal.ONE),
                Arguments.of("Notebook", "b".repeat(1001), BigDecimal.ONE),
                Arguments.of("Notebook", null, null),
                Arguments.of("Notebook", null, new BigDecimal("-0.01")),
                Arguments.of("Notebook", null, new BigDecimal("1E+10")),
                Arguments.of("Notebook", null, new BigDecimal("1.000")));
    }

    @Test
    void listsItemsSortedByPriceThenId() {
        var sort = Sort.by(Sort.Order.asc("price"), Sort.Order.asc("id"));
        var items = List.of(mock(Item.class));
        when(repository.findAll(sort)).thenReturn(items);
        assertSame(items, service.list());
        verify(repository).findAll(sort);
    }

    @Test
    void getsItemById() {
        var item = mock(Item.class);
        when(repository.findById(1L)).thenReturn(Optional.of(item));
        assertSame(item, service.get(1L));
        verify(repository).findById(1L);
    }

    @Test
    void throwsWhenItemIsMissing() {
        when(repository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.get(999L));
        verify(repository).findById(999L);
    }
}
