package org.burgerapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {
    @GetMapping("/auth")
    public ResponseEntity<String> getFallbackAuth() {
        //return ResponseEntity.internalServerError().body("Auth service şu an hizmet verememektedir.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Auth service şu an hizmet verememektedir");
    }
    @GetMapping("/userprofile")
    public ResponseEntity<String> getFallbackUserProfile() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User service şu an hizmet verememektedir");
    }
    @GetMapping("/items")
    public ResponseEntity<String> getFallbackItems() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Items service şu an hizmet verememektedir");
    }

        @GetMapping("/order")
        public ResponseEntity<String> getFallbackOrders() {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Orders service şu an hizmet verememektedir");
        }

}