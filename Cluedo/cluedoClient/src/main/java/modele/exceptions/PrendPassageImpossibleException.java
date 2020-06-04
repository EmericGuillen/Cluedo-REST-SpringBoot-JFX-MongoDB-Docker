package modele.exceptions;

public class PrendPassageImpossibleException extends Throwable {
    public PrendPassageImpossibleException() {
        super("Impossible de prendre le passage !");
    }
}
