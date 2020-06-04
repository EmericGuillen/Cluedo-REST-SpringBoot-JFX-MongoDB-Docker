package modele.exceptions;

public class QuitterPartieImpossibleException extends Throwable {
    public QuitterPartieImpossibleException() {
        super("Impossible de quitter la partie !");
    }
}
