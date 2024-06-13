package org.burgerapp.dto.itemdto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ItemSaveRequestDto {
    private String token;
    private String name;
    private String type;
    private Double price;

}
