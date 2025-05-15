package com.example.crud_mysql.controller;

import com.example.crud_mysql.model.Item;
import com.example.crud_mysql.service.ItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/search")
    public Page<Item> searchItems(Pageable pageable){

        return itemService.searchItem(pageable);
    }

    @GetMapping("/findAll")
    public List<Item> findAllItems() {
        List<Item> items = itemService.getAllItems();
        return items;
    }

    @PostMapping("/create")
    public Item createItem(@RequestBody Item item) {

        return itemService.save(item);
    }

    @PutMapping("/{id}/edit")
    public Item updateItem(@PathVariable Long id, @RequestBody Item updatedItem) {

        return itemService.updateItem(id, updatedItem);
    }

    @PutMapping("/{id}/soft-delete")
    public Item softDeleteItem(@PathVariable Long id) {

        return itemService.softDelete(id);
    }
}
