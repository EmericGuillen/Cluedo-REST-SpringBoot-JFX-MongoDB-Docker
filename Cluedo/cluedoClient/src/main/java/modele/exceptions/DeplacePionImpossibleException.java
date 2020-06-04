package modele.exceptions;

public class DeplacePionImpossibleException extends Throwable {
    public DeplacePionImpossibleException() {
        super("Impossible de déplacer un pion !");
    }
}
