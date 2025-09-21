package edu.eci.arep.taller5.exception;

import edu.eci.arep.taller5.model.ErrorCode;

public class AppException extends RuntimeException{
    private final ErrorCode errorCode;
    protected AppException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    public ErrorCode getErrorCode() {
        return errorCode;
    }
    public int getHttpStatus() {
        return errorCode.getHttpStatus();
    }

}
