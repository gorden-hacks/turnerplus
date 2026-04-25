package de.turnflow.common.exception;

public class NotFoundException extends ApiException {

    public NotFoundException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }
}