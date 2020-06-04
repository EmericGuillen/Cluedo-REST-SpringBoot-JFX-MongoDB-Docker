package cluedo.modele;

public class Carte {

    /*
     * ATTRIBUTS
     */
    private String type;
    private String nom;
    private String descriptif;


    /*
     * CONSTRUCTEUR
     */
    public Carte(){}

    public Carte(String type, String nom){
        this.type = type;
        this.nom = nom;
    }

    public Carte(String type, String nom, String descriptif){
        this.type = type;
        this.nom = nom;
    }

    /*
     * GETTERS / SETTERS
     */
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescriptif() { return descriptif; }
    public void setDescriptif(String descriptif) { this.descriptif = descriptif; }




    @Override
    public String toString(){
        return "Type : " + getType() + ", nom : " + getNom() + ", descriptif : " + getDescriptif();
    }
}