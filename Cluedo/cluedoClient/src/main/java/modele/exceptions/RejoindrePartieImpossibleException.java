package modele.exceptions;

public class RejoindrePartieImpossibleException extends Throwable {
    public RejoindrePartieImpossibleException() {
        super("Impossible de rejoindre la partie !");
    }
}
