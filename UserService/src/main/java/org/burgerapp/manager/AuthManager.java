//package org.burgerapp.manager;
//
//
//import com.berkayg.dto.request.UserUpdateRequestDto;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//
//@FeignClient(url = "http://localhost:9090/api/v1/auth", name="authManager")
//public interface AuthManager {
//    @PutMapping("/updatemail/{authId}")
//    ResponseEntity<Boolean> updatemail(@PathVariable Long authId, @RequestBody UserUpdateRequestDto dto);
//}