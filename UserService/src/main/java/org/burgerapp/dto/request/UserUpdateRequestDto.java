package org.burgerapp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserUpdateRequestDto {
    private String token;
    private String name;
    private String surname;
    private String phone;
    private String address;
    private Double balance;
}