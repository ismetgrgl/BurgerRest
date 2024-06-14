package org.burgerapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.burgerapp.constant.EndPoints;
import org.burgerapp.dto.request.ActivateCodeRequestDto;
import org.burgerapp.dto.request.LoginRequestDto;
import org.burgerapp.dto.request.RegisterRequestDto;
import org.burgerapp.dto.request.UpdatePasswordRequestDto;
import org.burgerapp.dto.response.RegisterResponseDto;
import org.burgerapp.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(EndPoints.AUTH)
public class AuthController {
    private final AuthService authService;

    @PostMapping(EndPoints.REGISTER)
    public ResponseEntity<RegisterResponseDto> register(@RequestBody @Valid RegisterRequestDto dto){
        return ResponseEntity.ok(authService.registerWithRabbit(dto));
    }
    /**
     * Login İşlemleri
     */
    @PostMapping(EndPoints.LOGIN)
    public ResponseEntity<String> dologin(@RequestBody LoginRequestDto dto) {
        return ResponseEntity.ok(authService.doLogin(dto));
    }
    @PutMapping(EndPoints.ACTIVATECODE)
    public ResponseEntity<String> activatecode(@RequestBody ActivateCodeRequestDto dto) {
        return ResponseEntity.ok(authService.activateCode(dto));
    }


    @GetMapping("/forgotpassword")
    public ResponseEntity<String> sifremiUnuttum(@RequestParam String email){
        authService.sifremiUnuttum(email);
        return ResponseEntity.ok("Şifrenizi onaylamak için updatePassword bölümüne gidiniz.");
    }

    @PutMapping(EndPoints.UPDATEPASSWORD)
    public ResponseEntity<String> updatePassword(@RequestBody UpdatePasswordRequestDto dto) {
        return ResponseEntity.ok(authService.updatePassword(dto));
    }
}
