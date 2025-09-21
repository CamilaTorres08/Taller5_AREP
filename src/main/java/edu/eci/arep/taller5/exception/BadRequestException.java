package edu.eci.arep.taller5.exception;

import edu.eci.arep.taller5.model.ErrorCode;

public class BadRequestException extends AppException {
    public BadRequestException(String message) {
        super(ErrorCode.BAD_REQUEST,message);
    }
}
