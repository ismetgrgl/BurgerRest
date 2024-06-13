package org.burgerapp.controller;

import lombok.RequiredArgsConstructor;

import org.burgerapp.dto.categorydto.request.CategorySaveRequestDto;
import org.burgerapp.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping(value = "/save")
    public ResponseEntity<String> save(@RequestBody CategorySaveRequestDto dto){
        categoryService.save(dto);
        return ResponseEntity.ok("Kategori başarıyla kaydedildi.");
    }
    @DeleteMapping(value = "/delete")
    public ResponseEntity<String> delete(@RequestParam Long id){
        categoryService.delete(id);
        return ResponseEntity.ok("Kategori başarıyla silindi.");
    }

}
