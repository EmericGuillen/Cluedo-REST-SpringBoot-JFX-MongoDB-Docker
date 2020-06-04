package modele.DTO;


public class CarteDTO {

    /*
     * ATTRIBUTS
     */
    private String type;
    private String nom;
    private String descriptif;
    private String choix;


    /*
     * METHODES
     */
    public CarteDTO() {

    }


    /*
     * GETTERS / SETTERS
     */

    public String getChoix() {
        return choix;
    }

    public void setChoix(String choix) {
        this.choix = choix;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescriptif() {
        return descriptif;
    }

    public void setDescriptif(String descriptif) {
        this.descriptif = descriptif;
    }

}