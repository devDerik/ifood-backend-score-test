package ifood.score.exception;

public class ScoreServiceException extends Exception {
    public ScoreServiceException(String message) {
        super(message);
    }

    public ScoreServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
