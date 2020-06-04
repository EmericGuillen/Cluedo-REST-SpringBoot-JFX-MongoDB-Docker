package modele.exceptions;

public class DemandeSauvegardeImpossibleException extends Throwable {
    public DemandeSauvegardeImpossibleException() {
        super("Impossible de demander de sauvegarder la partie !");
    }
}
