package modele.DTO;

import java.util.ArrayList;
import java.util.List;

public class PartieDTO {

    /*
     * ATTRIBUTS
     */

    private String idPartie;
    private String mdj;
    private String joueurCourant;
    private String statut;
    private List<CarteDTO> pioche = new ArrayList<>();
    private List<CarteDTO> solution = new ArrayList<>();
    private List<CarteDTO> hypotheseCourante = new ArrayList<>();
    private List<String> invites = new ArrayList<>();
    private List<String> connectes = new ArrayList<>();
    private List<JoueurDTO> joueurs = new ArrayList<>();
    private List<String> persosJoues = new ArrayList<>();

    /*
     * CREATION
     */
    public PartieDTO (){

    }


    /*
     * GETTERS / SETTERS
     */
    public String getIdPartie() { return idPartie; }
    public void setIdPartie(String idPartie) { this.idPartie = idPartie; }

    public String getMdj() { return mdj; }
    public void setMdj(String mdj) { this.mdj = mdj; }

    public List<String> getPersosJoues() {
        return persosJoues;
    }

    public void setPersosJoues(List<String> persosJoues) {
        this.persosJoues = persosJoues;
    }

    public String getJoueurCourant() { return joueurCourant; }
    public void setJoueurCourant(String joueurCourant) { this.joueurCourant = joueurCourant; }

    public List<CarteDTO> getPioche() { return pioche; }
    public void setPioche(List<CarteDTO> pioche) { this.pioche = pioche; }

    public List<CarteDTO> getSolution() { return solution; }
    public void setSolution(List<CarteDTO> solution) { this.solution = solution; }

    public List<String> getInvites() { return invites; }
    public void setInvites(List<String> invites) { this.invites = invites; }

    public List<String> getConnectes() { return connectes; }
    public void setConnectes(List<String> connectes) { this.connectes = connectes; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public List<JoueurDTO> getJoueurs() { return joueurs; }
    public void setJoueurs(List<JoueurDTO> joueurs) { this.joueurs = joueurs; }

    public List<CarteDTO> getHypotheseCourante() { return hypotheseCourante; }

    public void setHypotheseCourante(List<CarteDTO> hypotheseCourante) { this.hypotheseCourante = hypotheseCourante; }

    @Override
    public String toString() {
        return idPartie+" : Partie de "+mdj;
    }
}
