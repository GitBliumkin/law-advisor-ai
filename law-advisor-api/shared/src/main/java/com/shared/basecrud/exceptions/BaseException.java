package com.shared.basecrud.exceptions;

public abstract class BaseException extends Exception {

    private final String errorCode;

    public BaseException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public BaseException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
