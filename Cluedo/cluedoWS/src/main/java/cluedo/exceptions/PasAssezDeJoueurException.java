package cluedo.exceptions;

public class PasAssezDeJoueurException extends Exception {
    public PasAssezDeJoueurException(String msg){
        System.out.println("msg = " + msg);
    }

    public PasAssezDeJoueurException(){}
}
