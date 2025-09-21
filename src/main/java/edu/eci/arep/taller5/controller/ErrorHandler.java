package edu.eci.arep.taller5.controller;


import edu.eci.arep.taller5.exception.AppException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.List;
import java.util.Map;


@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ProblemDetail> handle(AppException ex){
        var pd = ProblemDetail.forStatus(ex.getHttpStatus());
        pd.setTitle(ex.getErrorCode().name());
        pd.setDetail(ex.getMessage());
        pd.setProperty("code", ex.getErrorCode().name());
        return ResponseEntity.status(ex.getHttpStatus()).body(pd);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handle(MethodArgumentNotValidException ex){
        var pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("BAD_REQUEST");
        pd.setDetail("Validation Failed");
        List<Map<String, Object>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> Map.<String, Object>of(
                        "field", fe.getField(),
                        "message", fe.getDefaultMessage(),
                        "rejectedValue", fe.getRejectedValue()
                ))
                .toList();
        pd.setProperty("errors", errors);
        pd.setProperty("code", "BAD_REQUEST");
        return ResponseEntity.badRequest().body(pd);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnknown(Exception ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setTitle("INTERNAL_ERROR");
        pd.setDetail(ex.getMessage());
        pd.setProperty("code", "INTERNAL_ERROR");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(pd);
    }

}
