package org.burgerapp.dto.productdto.request;

import lombok.*;
import org.burgerapp.entity.Category;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Getter
@Setter
public class ProductSaveRequestDto {
    private String token;
    private String name;
    private String description;
    private Double price;
    private Set<Long> itemsIds;
    private Long categoryId;
}
