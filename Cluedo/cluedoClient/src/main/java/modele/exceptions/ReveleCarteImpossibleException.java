package modele.exceptions;

public class ReveleCarteImpossibleException extends Throwable {
    public ReveleCarteImpossibleException() {
        super("Impossible de révéler la carte !");
    }
}
