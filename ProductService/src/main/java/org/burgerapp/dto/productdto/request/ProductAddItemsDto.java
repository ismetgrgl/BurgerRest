package org.burgerapp.dto.productdto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ProductAddItemsDto {
    String token;
    Long itemsId;
    Long productId;

}
