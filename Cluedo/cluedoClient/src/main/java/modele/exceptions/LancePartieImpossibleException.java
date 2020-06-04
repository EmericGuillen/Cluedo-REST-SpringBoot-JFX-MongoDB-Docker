package modele.exceptions;

public class LancePartieImpossibleException extends Throwable {
    public LancePartieImpossibleException() {
        super("Impossible de lancer la partie !");
    }
}
