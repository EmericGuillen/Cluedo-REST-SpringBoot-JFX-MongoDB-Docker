package modele.exceptions;

public class UnexpectedError extends RuntimeException {

    public UnexpectedError() {
    }

    public UnexpectedError(String message) {
        super(message);
    }
}
