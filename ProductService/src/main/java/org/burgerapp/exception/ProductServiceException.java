package org.burgerapp.exception;

import lombok.Getter;

@Getter
public class ProductServiceException extends RuntimeException{
    private ErrorType errorType;

    public ProductServiceException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }

    public ProductServiceException(ErrorType errorType, String customMessage) {
        super(customMessage);
        this.errorType = errorType;
    }
}
