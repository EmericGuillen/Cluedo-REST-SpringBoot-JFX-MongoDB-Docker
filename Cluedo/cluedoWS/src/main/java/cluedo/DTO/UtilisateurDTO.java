package cluedo.DTO;

import cluedo.modele.Utilisateur;

public class UtilisateurDTO {

    /*
     * ATTRIBUTS
     */
    private int idUtil;
    private String nom;
    private String password;
    private String confirmPassword;


    /*
     * CONSTRUCTEURS
     */
    public UtilisateurDTO() {

    }


    /*
     * METHODES
     */
    public static UtilisateurDTO creerUtilDTO(Utilisateur util){

        UtilisateurDTO utilDTO = new UtilisateurDTO();

        utilDTO.setNom(util.getNom());
        utilDTO.setIdUtil(Integer.valueOf(util.getIdUtil()));
        utilDTO.setPassword(util.getPassword());

        return utilDTO;
    }



    /*
     * GETTERS / SETTERS
     */
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public int getIdUtil() {
        return idUtil;
    }

    public void setIdUtil(int idUtil) {
        this.idUtil = idUtil;
    }
}