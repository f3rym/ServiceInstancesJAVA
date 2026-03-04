package ferym.project.exception;

public class SystemFailureException extends RuntimeException {
    public SystemFailureException(String message) {
        super(message);
    }
}