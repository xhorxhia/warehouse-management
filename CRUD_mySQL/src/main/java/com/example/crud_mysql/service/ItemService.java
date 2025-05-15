package com.example.crud_mysql.service;

import com.example.crud_mysql.model.Enum.LifeCycle;
import com.example.crud_mysql.model.Item;
import com.example.crud_mysql.repository.ItemRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final ObjectMapper objectMapper;

    public ItemService(ItemRepository itemRepository, ObjectMapper objectMapper) {
        this.itemRepository = itemRepository;
        this.objectMapper = objectMapper;
    }

    public Item save(Item item) {
        return itemRepository.save(item);
    }

    public Page<Item> searchItem(Pageable pageable) {
        return itemRepository.findAllByLifeCycle(pageable, LifeCycle.READY);
    }

    public Item updateItem(Long id, Item item) {
        return itemRepository.findById(id).map(itm -> {
            itm.setItemName(item.getItemName());
            itm.setUnitPrice(item.getUnitPrice());
            itm.setQuantity(item.getQuantity());
            return itemRepository.save(itm);
            }).orElseThrow(() -> new RuntimeException("Item not found with this id " + id));
    }

    public List<Item> getAllItems() {
        List<Item> items = itemRepository.findAll().stream()
                .map(items1 -> objectMapper.convertValue(items1, Item.class)).collect(Collectors.toList());;
        return items;
    }

    public Item softDelete(Long id) {
        return itemRepository.findById(id).map(item -> {
            item.setLifeCycle(LifeCycle.DELETED);
            return itemRepository.save(item);
        }).orElseThrow(() -> new RuntimeException("Item not found with this id " + id));
    }




}
