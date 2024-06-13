package org.burgerapp.dto.categorydto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CategorySaveRequestDto {
    private String token;
    private String name;
    private String description;
}
