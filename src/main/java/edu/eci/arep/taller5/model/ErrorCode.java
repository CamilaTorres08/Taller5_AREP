package edu.eci.arep.taller5.model;

/**
 * Error code model
 */
public enum ErrorCode {
    NOT_FOUND(404),
    BAD_REQUEST(400),
    CONFLICT(409),
    INTERNAL_ERROR(500);

    private final int httpStatus;
    ErrorCode(int httpStatus) { this.httpStatus = httpStatus; }
    public int getHttpStatus() { return httpStatus; }
}
