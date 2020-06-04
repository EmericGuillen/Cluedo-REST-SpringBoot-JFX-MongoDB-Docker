package cluedo.modele;

public class Des {
    /*
     * ATTRIBUTS
     */
    private int de1;
    private int de2;
    private int somme;

    /*
     * CONSTRUCTEUR
     */

    public Des(int de1, int de2) {
        this.de1 = de1;
        this.de2 = de2;
        this.somme = de1 + de2;
    }

    public Des(){
    }

    /*
     * GETTERS / SETTERS
     */

    public int getDe1() {
        return de1;
    }

    public void setDe1(int de1) {
        this.de1 = de1;
    }

    public int getDe2() {
        return de2;
    }

    public void setDe2(int de2) {
        this.de2 = de2;
    }

    public int getSomme() {
        return somme;
    }

    public void setSomme(int somme) {
        this.somme = somme;
    }

}
