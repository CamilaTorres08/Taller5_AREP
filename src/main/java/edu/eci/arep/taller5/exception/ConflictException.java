package edu.eci.arep.taller5.exception;

import edu.eci.arep.taller5.model.ErrorCode;

public class ConflictException extends AppException{
    public ConflictException(String message) {super(ErrorCode.CONFLICT, message);}
}
