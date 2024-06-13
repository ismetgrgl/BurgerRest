package org.burgerapp.controller;

import lombok.RequiredArgsConstructor;
import org.burgerapp.dto.itemdto.request.ItemSaveRequestDto;
import org.burgerapp.entity.Items;
import org.burgerapp.service.ItemsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemsController {
    private final ItemsService itemsService;

    @PostMapping("/createItems")
    public ResponseEntity<Items> createOption(@RequestBody ItemSaveRequestDto dto) {
        Items createdItems = itemsService.createItems(dto);
        return ResponseEntity.ok(createdItems);
    }

    @GetMapping("/allItems")
    public ResponseEntity<List<Items>> getAllItems() {
        List<Items> items = itemsService.getAllItems();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Items>> getItemsByType(@PathVariable String type) {
        List<Items> items = itemsService.getItemsByType(type);
        return ResponseEntity.ok(items);
    }
}
