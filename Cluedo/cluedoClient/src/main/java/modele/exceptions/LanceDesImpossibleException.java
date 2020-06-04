package modele.exceptions;

public class LanceDesImpossibleException extends Throwable {
    public LanceDesImpossibleException() {
        super("Impossible de lancer les dès !");
    }
}
