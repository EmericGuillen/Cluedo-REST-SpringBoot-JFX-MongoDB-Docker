package cluedo.modele;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "cluedi")
public class Utilisateur {
    private static int IDSUTIL=1;
    @Id
    private String idUtil;
    private String nom;
    private String password;

    public Utilisateur() {

        this.idUtil= String.valueOf(IDSUTIL++);
        Partie.setIDSPARTIE(IDSUTIL);
    }

    public Utilisateur(String nom, String password) {
        this();
        this.nom = nom;
        this.password = password;
    }



    public String getIdUtil() {
        return idUtil;
    }
    public void setIdUtil(String idUtil) {
        this.idUtil = idUtil;
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

    public static int getIDSUTIL() { return IDSUTIL; }
    public static void setIDSUTIL(int IDSUTIL) { Utilisateur.IDSUTIL = IDSUTIL; }
}
