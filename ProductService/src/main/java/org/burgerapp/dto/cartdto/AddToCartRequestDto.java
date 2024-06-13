package org.burgerapp.dto.cartdto;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AddToCartRequestDto {
    private String token;
    private Long productId;
    private Set<Long> itemIds;
    private int quantity;
}
