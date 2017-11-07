package ifood.score.controller;

import ifood.score.exception.ScoreServiceException;
import ifood.score.exception.ScoreServiceNoContentException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(ScoreServiceException.class)
    public ResponseEntity<String> scoreServiceException(Exception e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ScoreServiceNoContentException.class)
    public ResponseEntity<String> scoreServiceNoContentException() {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
