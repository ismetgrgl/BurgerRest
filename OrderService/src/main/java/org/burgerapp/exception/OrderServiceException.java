package org.burgerapp.exception;

import lombok.Getter;

@Getter
public class OrderServiceException extends RuntimeException{
    private ErrorType errorType;

    public OrderServiceException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }

    public OrderServiceException(ErrorType errorType, String customMessage) {
        super(customMessage);
        this.errorType = errorType;
    }
}
