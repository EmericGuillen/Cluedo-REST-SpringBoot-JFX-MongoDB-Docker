package modele.exceptions;

public class JoueCarteIndiceImpossibleException extends Throwable {
    public JoueCarteIndiceImpossibleException() {
        super("Impossible de jouer la carte indice !");
    }
}
