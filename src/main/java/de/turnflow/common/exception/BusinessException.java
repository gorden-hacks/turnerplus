package de.turnflow.common.exception;

public class BusinessException extends ApiException {

    public BusinessException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }
}