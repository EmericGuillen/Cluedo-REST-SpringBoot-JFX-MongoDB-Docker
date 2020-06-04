package modele.exceptions;

public class AccepteSauvegardeImpossibleException extends Throwable {
    public AccepteSauvegardeImpossibleException() {
        super("Impossible d'accepter de sauvegarder la partie !");
    }
}
