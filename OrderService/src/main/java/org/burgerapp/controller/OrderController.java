package org.burgerapp.controller;


import lombok.RequiredArgsConstructor;
import org.burgerapp.entity.Order;
import org.burgerapp.exception.ErrorType;
import org.burgerapp.exception.OrderServiceException;
import org.burgerapp.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable Long orderId) {
        Order order = orderService.findById(orderId);
        return ResponseEntity.ok(order);
    }
}
