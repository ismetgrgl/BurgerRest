package org.burgerapp.controller;

import lombok.RequiredArgsConstructor;
import org.burgerapp.dto.cartdto.AddToCartRequestDto;
import org.burgerapp.dto.cartdto.ViewCartRequestDto;
import org.burgerapp.dto.productdto.request.RemoveFromCartRequestDto;
import org.burgerapp.entity.ShoppingCart;
import org.burgerapp.exception.ProductServiceException;
import org.burgerapp.service.ShoppingCartService;
import org.springframework.http.HttpStatus;
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

    @PutMapping("/remove")
    public ResponseEntity<ShoppingCart> removeFromCart(@RequestBody RemoveFromCartRequestDto dto) {
        try {
            ShoppingCart updatedCart = shoppingCartService.removeFromCart(dto);
            return ResponseEntity.ok(updatedCart);
        } catch (ProductServiceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/view")
    public ResponseEntity<ShoppingCart> viewCart(@RequestParam String token) {
        ShoppingCart shoppingCart = shoppingCartService.viewCart(token);
        return ResponseEntity.ok(shoppingCart);
    }

    @PostMapping("/paytocart")
    public ResponseEntity<Void> checkout(@RequestParam Long cartId) {
        shoppingCartService.checkout(cartId);
        return ResponseEntity.ok().build();
    }

}
