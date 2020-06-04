package modele.exceptions;

public class TermineTourImpossibleException extends Throwable {
    public TermineTourImpossibleException() {
        super("Impossible de terminer le tour !");
    }
}
