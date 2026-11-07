package com.example.template.items.service;

import java.math.BigDecimal;
import java.util.List;

import com.example.template.exception.ResourceNotFoundException;
import com.example.template.items.entity.Item;
import com.example.template.items.repository.ItemRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemService {
    private final ItemRepository repository;

    public ItemService(ItemRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Item create(String name, String description, BigDecimal price) {
        return repository.saveAndFlush(new Item(name, description, price));
    }

    @Transactional
    public Item update(Long id, String name, String description, BigDecimal price) {
        Item item = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item", id));
        item.update(name, description, price);
        return repository.saveAndFlush(item);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Item", id);
        }
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Item> list() {
        return repository.findAll(Sort.by(Sort.Order.asc("price"), Sort.Order.asc("id")));
    }

    @Transactional(readOnly = true)
    public Item get(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Item", id));
    }
}
