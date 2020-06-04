package modele.exceptions;

public class ConsulteCartesImpossibleException extends Throwable {
    public ConsulteCartesImpossibleException() {
        super("Impossible de consulter les cartes du jeu !");
    }
}
