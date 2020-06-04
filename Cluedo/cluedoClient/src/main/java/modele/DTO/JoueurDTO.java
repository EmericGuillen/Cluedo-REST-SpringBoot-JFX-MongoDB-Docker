package modele.DTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JoueurDTO {

    /*
     * ATTRIBUTS
     */
    private String util;
    private CarteDTO perso;
    private String statutJoueur;
    private String piece = "";
    private PointDTO positionCourante;
    private List<CarteDTO> deck = new ArrayList<>();
    private List<List<CarteDTO>> blocnote = new ArrayList<>();


    /*
     * METHODES
     */
    public JoueurDTO (){

    }


    /*
     * GETTERS / SETTERS
     */

    public String getUtil() {
        return util;
    }

    public void setUtil(String util) {
        this.util = util;
    }

    public CarteDTO getPerso() {
        return perso;
    }

    public void setPerso(CarteDTO perso) {
        this.perso = perso;
    }

    public String getStatutJoueur() {
        return statutJoueur;
    }

    public void setStatutJoueur(String statutJoueur) {
        this.statutJoueur = statutJoueur;
    }

    public PointDTO getPositionCourante() {
        return positionCourante;
    }

    public void setPositionCourante(PointDTO positionCourante) {
        this.positionCourante = positionCourante;
    }

    public List<CarteDTO> getDeck() {
        return deck;
    }

    public void setDeck(List<CarteDTO> deck) {
        this.deck = deck;
    }

    public List<List<CarteDTO>> getBlocnote() {
        return blocnote;
    }

    public void setBlocnote(List<List<CarteDTO>> blocnote) {
        this.blocnote = blocnote;
    }

    public String getPiece() {
        return piece;
    }

    public void setPiece(String piece) {
        this.piece = piece;
    }
}
