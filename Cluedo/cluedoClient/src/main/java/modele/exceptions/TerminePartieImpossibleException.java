package modele.exceptions;

public class TerminePartieImpossibleException extends Throwable {
    public TerminePartieImpossibleException() {
        super("Impossible de terminer la partie !");
    }
}
