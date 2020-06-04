package cluedo.exceptions;

public class UtilisateurPasConnecteException extends Exception {
    public UtilisateurPasConnecteException(String msg){
        System.out.println("msg = " + msg);
    }

    public UtilisateurPasConnecteException(){}
}
