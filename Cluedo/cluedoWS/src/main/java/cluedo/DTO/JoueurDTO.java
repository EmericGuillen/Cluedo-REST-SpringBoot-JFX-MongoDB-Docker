package cluedo.DTO;

import cluedo.modele.Carte;
import cluedo.modele.Joueur;
import cluedo.modele.Point;
import cluedo.modele.Utilisateur;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JoueurDTO {

    /*
     * ATTRIBUTS
     */
    private String util;
    private String piece;
    private CarteDTO perso;
    private String statutJoueur;
    private PointDTO positionCourante;
    private List<CarteDTO> deck = new ArrayList<>();
    private List<List<CarteDTO>> blocnote = new ArrayList<>();
    //Liste 1 : cartes écartées   -   Liste 2 : cartes doute   -  Liste 3 : carte validées


    /*
     * METHODES
     */
    public static JoueurDTO creerJoueurDTO(Joueur joueur){
        JoueurDTO joueurDTO = new JoueurDTO();

        joueurDTO.setUtil(joueur.getUtil());
        joueurDTO.setPiece(joueur.getPiece());
        joueurDTO.setPerso(CarteDTO.creerCarteDTO(joueur.getPerso()));
        joueurDTO.setStatutJoueur(joueur.getStatutJoueur());
        joueurDTO.setDeck(CarteDTO.creerListeCarteDTO(joueur.getDeck()));
        joueurDTO.setPositionCourante(PointDTO.creerPointDTO(joueur.getPositionCourante()));
        joueurDTO.setBlocnote(JoueurDTO.creerBlocNote(joueur.getBlocnote()));

        return joueurDTO;
    }


    public static List<JoueurDTO> creerListeJoueurDTO(List<Joueur> joueurs){

        List<JoueurDTO> joueursDTO = new ArrayList<>();

        for(Joueur joueur: joueurs){
            joueursDTO.add(creerJoueurDTO(joueur));
        }

        return joueursDTO;
    }


    public static List<List<CarteDTO>> creerBlocNote(List<List<Carte>> blocnote){

        List<List<CarteDTO>> liste = new ArrayList<>();

        for(int i = 0; i < blocnote.size() ; i++){
            List<CarteDTO> listeCarteDTO = CarteDTO.creerListeCarteDTO(blocnote.get(i));
            liste.add(listeCarteDTO);
        }

        return liste;
    }

    /*
     * GETTERS / SETTERS
     */
    public String getUtil() { return util; }
    public void setUtil(String util) { this.util = util; }

    public CarteDTO getPerso() { return perso; }
    public void setPerso(CarteDTO perso) { this.perso = perso; }

    public List<CarteDTO> getDeck() { return deck; }
    public void setDeck(List<CarteDTO> deck) { this.deck = deck; }

    public PointDTO getPositionCourante() { return positionCourante; }
    public void setPositionCourante(PointDTO positionCourante) { this.positionCourante = positionCourante; }

    public String getStatutJoueur() { return statutJoueur; }
    public void setStatutJoueur(String statutJoueur) { this.statutJoueur = statutJoueur; }

    public List<List<CarteDTO>> getBlocnote() { return blocnote; }
    public void setBlocnote(List<List<CarteDTO>> blocnote) { this.blocnote = blocnote; }

    public String getPiece() { return piece; }
    public void setPiece(String piece) { this.piece = piece; }


    @Override
    public String toString(){
        return this.util + ", " + this.perso.getNom();
    }
}
