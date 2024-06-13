package org.burgerapp.controller;

import lombok.RequiredArgsConstructor;
import org.burgerapp.dto.cartdto.AddToCartRequestDto;
import org.burgerapp.dto.cartdto.ViewCartRequestDto;
import org.burgerapp.entity.ShoppingCart;
import org.burgerapp.service.ShoppingCartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public ResponseEntity<ShoppingCart> addToCart(@RequestBody AddToCartRequestDto dto) {
        ShoppingCart shoppingCart = shoppingCartService.addToCart(dto);
        return ResponseEntity.ok(shoppingCart);
    }

    @GetMapping("/view")
    public ResponseEntity<ShoppingCart> viewCart(@RequestParam String token) {
        ShoppingCart shoppingCart = shoppingCartService.viewCart(token);
        return ResponseEntity.ok(shoppingCart);
    }

    @PostMapping("/checkout")
    public ResponseEntity<Void> checkout(@RequestParam Long cartId, @RequestParam Long userId) {
        shoppingCartService.checkout(cartId , userId );
        return ResponseEntity.ok().build();
    }
}
