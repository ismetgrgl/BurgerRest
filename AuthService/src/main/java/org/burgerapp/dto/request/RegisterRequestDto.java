package org.burgerapp.dto.request;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RegisterRequestDto {
    @NotBlank(message = "username boş geçilemez.")
    @Size(min = 3, max = 20, message = "Username min 3 - max 20 karakter olabilir.")
    String username;
    @NotBlank
    @Email(message = "Geçerli bir eposta adresi giriniz.")
    String email;
    @NotBlank(message = "password boş geçilemez.")
    @Size(min = 3, max = 20, message = "password min 3 - max 20 karakter olabilir.")
    String password;
    String repassword;

}
