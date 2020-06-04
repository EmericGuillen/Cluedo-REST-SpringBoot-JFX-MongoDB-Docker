package modele.exceptions;

public class RecuperationUtilisateursImpossibleException extends Throwable {
    public RecuperationUtilisateursImpossibleException() {
        super("Impossible de récupérer les utilisateurs !");
    }
}
