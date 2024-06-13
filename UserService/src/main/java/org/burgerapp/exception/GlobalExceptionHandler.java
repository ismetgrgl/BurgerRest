package org.burgerapp.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)// RuntimeException Hata yakalayıcı bir metod olduğunu belirtmek için.
    public ResponseEntity<String> handleException(RuntimeException ex) {
        System.err.println(ex.getMessage());
        return ResponseEntity.badRequest().body("Uygulamada RunTime Exception oluştu................");
    }


    @ExceptionHandler(UserServiceException.class)
    public ResponseEntity<ErrorMessage> handleDemoException(UserServiceException ex) {
        ErrorType errorType = ex.getErrorType();
        return new ResponseEntity(createErrorMessage(ex),
                errorType.getHttpStatus());
    }

    private ErrorMessage createErrorMessage(UserServiceException ex) {
        return ErrorMessage.builder()
                .code(ex.getErrorType().getCode())
                .message(ex.getMessage())
                .build();
    }

}



