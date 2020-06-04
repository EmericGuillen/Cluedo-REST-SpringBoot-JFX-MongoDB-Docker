package cluedo.exceptions;

public class UtilisateurNonExistantException extends RuntimeException {
    public UtilisateurNonExistantException(String msg){
        System.out.println("msg = " + msg);
    }

    public UtilisateurNonExistantException(){}
}
