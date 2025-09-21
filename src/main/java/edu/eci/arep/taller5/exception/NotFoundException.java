package edu.eci.arep.taller5.exception;

import edu.eci.arep.taller5.model.ErrorCode;

public class NotFoundException extends AppException {
    public NotFoundException(String message) {
        super(ErrorCode.NOT_FOUND,message);
    }
}
