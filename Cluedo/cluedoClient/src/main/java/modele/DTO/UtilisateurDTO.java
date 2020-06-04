package modele.DTO;


public class UtilisateurDTO {
    private int idUtil;
    private String nom;
    private String password;
    private String confirmPassword;
    private String token;






    public UtilisateurDTO() {

    }


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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
