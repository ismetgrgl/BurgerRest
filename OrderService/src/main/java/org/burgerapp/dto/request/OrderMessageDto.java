package org.burgerapp.dto.request;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

public class OrderMessageDto {
    private Long cartId;
    private Long userId;
    private Double totalPrice;

    // Getters and Setters
}
