package modele.exceptions;

public class ConsulteCarnetImpossibleException extends Throwable {
    public ConsulteCarnetImpossibleException() {
        super("Impossible de consulter son carnet !");
    }
}
