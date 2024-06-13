package org.burgerapp.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Bu sınıf tüm controller sınıfları için merkezi bir şekilde hata yönetimi sağlayacaktır.
@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(RuntimeException.class)// RuntimeException Hata yakalayıcı bir metod olduğunu belirtmek için.
	public ResponseEntity<String> handleException() {
		return ResponseEntity.badRequest().body("Uygulamada RunTime Exception oluştu................");
	}


	@ExceptionHandler(ProductServiceException.class)
	public ResponseEntity<ErrorMessage> handleDemoException(ProductServiceException ex) {
		ErrorType errorType = ex.getErrorType();
		return new ResponseEntity(createErrorMessage(ex),
		                          errorType.getHttpStatus());
	}
	
	private ErrorMessage createErrorMessage(ProductServiceException ex) {
		return ErrorMessage.builder()
		                   .code(ex.getErrorType().getCode())
		                   .message(ex.getMessage())
		                   .build();
	}

	
	
	
}