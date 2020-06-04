package modele.exceptions;

public class RestaurePartieImpossibleException extends Throwable {
    public RestaurePartieImpossibleException() {
        super("Impossible de restaurer la partie !");
    }
}
