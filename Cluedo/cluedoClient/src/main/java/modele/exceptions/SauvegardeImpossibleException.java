package modele.exceptions;

public class SauvegardeImpossibleException extends Throwable {
    public SauvegardeImpossibleException() {
        super("Impossible de sauvegarder la partie !");
    }
}
