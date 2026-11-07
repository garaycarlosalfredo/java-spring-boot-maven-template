package com.example.template.items.controller;

import java.net.URI;
import java.util.List;

import com.example.template.items.dto.CreateItemRequest;
import com.example.template.items.dto.DeleteItemResponse;
import com.example.template.items.dto.ItemDto;
import com.example.template.items.dto.ItemDtoMapper;
import com.example.template.items.dto.UpdateItemRequest;
import com.example.template.items.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/items")
public class ItemController {
    private final ItemService service;

    public ItemController(ItemService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemDto> createItem(@Valid @RequestBody CreateItemRequest request) {
        var item = service.create(request.name(), request.description(), request.price());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(item.id()).toUri();
        return ResponseEntity.created(location).body(ItemDtoMapper.toItemDto(item));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ItemDto>> listItems() {
        var items = service.list();
        var itemsDto = items.stream()
                .map(ItemDtoMapper::toItemDto)
                .toList();
        return ResponseEntity.ok(itemsDto);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemDto> getItem(@PathVariable Long id) {
        return ResponseEntity.ok(ItemDtoMapper.toItemDto(service.get(id)));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemDto> updateItem(@PathVariable Long id, @Valid @RequestBody UpdateItemRequest request) {
        var item = service.update(id, request.name(), request.description(), request.price());
        return ResponseEntity.ok(ItemDtoMapper.toItemDto(item));
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DeleteItemResponse> deleteItem(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(new DeleteItemResponse(id));
    }
}
