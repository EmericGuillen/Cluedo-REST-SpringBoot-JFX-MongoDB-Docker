package modele.exceptions;

public class RecuperationPartiesImpossibleException extends Throwable {
    public RecuperationPartiesImpossibleException() {
        super("Impossible de récupérer les parties !");
    }
}
