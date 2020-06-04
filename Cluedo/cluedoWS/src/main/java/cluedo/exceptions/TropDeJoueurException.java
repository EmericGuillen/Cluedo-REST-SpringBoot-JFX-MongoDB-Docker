package cluedo.exceptions;

public class TropDeJoueurException extends Exception {
    public TropDeJoueurException(String msg){
        System.out.println("msg = " + msg);
    }

    public TropDeJoueurException(){}
}
