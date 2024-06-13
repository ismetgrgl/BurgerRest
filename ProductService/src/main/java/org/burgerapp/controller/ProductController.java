package org.burgerapp.controller;

import lombok.RequiredArgsConstructor;
import org.burgerapp.dto.productdto.request.ProductAddItemsDto;
import org.burgerapp.dto.productdto.request.ProductSaveRequestDto;
import org.burgerapp.entity.Product;
import org.burgerapp.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping("/{productId}/items/{itemsId}")
    public ResponseEntity<Product> addItemsToProduct(@RequestBody ProductAddItemsDto dto) {
        Product updatedProduct = productService.addItemsToProduct(dto);
        return ResponseEntity.ok(updatedProduct);
    }

    @PostMapping("/createProduct")
    public ResponseEntity<Product> createProduct(@RequestBody ProductSaveRequestDto dto) {
        Product createdProduct = productService.createProduct(dto);
        return ResponseEntity.ok(createdProduct);
    }
}
