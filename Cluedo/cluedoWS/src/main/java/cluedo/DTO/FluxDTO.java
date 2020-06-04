package cluedo.DTO;

import cluedo.modele.Carte;
import cluedo.modele.Joueur;

import java.util.HashMap;
import java.util.List;

public class FluxDTO {

    /*
     * ATTRIBUTS
     */
    private String action;
    private String quiJoue;
    private String persoQuiJoue;
    private String quiQuitte;
    private String quiRejoins;
    private DesDTO des;
    private List<CarteDTO> supposition;
    private Boolean accusation;
    private PointDTO positionCourante;
    private JoueurDTO leJoueur;
    private JoueurDTO quiDoitReveler;
    private PointDTO positionCourantePassage;
    private boolean lancerPartie;
    private PartieDTO partie;
    private boolean terminerPartie;
    private List<CarteDTO> accusationsCartes;
    private JoueurDTO aQuiLeTour;
    private JoueurDTO gagnant;
    private List<CarteDTO> reveleCartes;
    private CarteDTO carteRevelee;
    private List<JoueurDTO> aQuiReveler;
    private List<List<CarteDTO>> Carnet ;
    private boolean aPrisPassage;
    private HashMap<Joueur, List<Carte>> piocheCarte;
    private PartieDTO partieSauvegarde;
    private CarteDTO carteIndiceAreveler;


    /*
     * CONSTRUCTEURS
     */
    public FluxDTO() {
        this.action = null;
        this.quiJoue = null;
        this.persoQuiJoue = null;
        this.quiQuitte = null;
        this.quiRejoins = null;
        this.des = null;
        this.supposition = null;
        this.accusation = false;
        this.positionCourante = null;
        this.leJoueur = null;
        this.quiDoitReveler = null;
        this.positionCourantePassage = null;
        this.lancerPartie = false;
        this.partie = null;
        this.terminerPartie = false;
        this.accusationsCartes = null;
        this.aQuiLeTour = null;
        this.gagnant = null;
        this.reveleCartes = null;
        this.aPrisPassage = false;
        this.piocheCarte = null;
        this.partieSauvegarde = null;
        this.carteIndiceAreveler = null;
    }


    public FluxDTO(String util){
        this();
        this.quiJoue = util;
        /*this.action = null;
        this.accusationsCartes = null;
        this.quiQuitte = null;
        this.quiRejoins = null;
        this.accusation = null;
        this.des = null;
        this.lancerPartie = false;
        this.positionCourante = null;
        this.positionCourantePassage = null;
        this.supposition = null;
        this.reveleCarte = null;
        this.terminerPartie = false;*/
    }




    /*
     * GETTERS / SETTERS
     */
    public List<CarteDTO> getAccusationsCartes() { return accusationsCartes; }
    public void setAccusationsCartes(List<CarteDTO> accusationsCartes) { this.accusationsCartes = accusationsCartes; }

    public String getQuiJoue() { return quiJoue; }
    public void setQuiJoue(String quiJoue) { this.quiJoue = quiJoue; }

    public String getQuiQuitte() { return quiQuitte; }
    public void setQuiQuitte(String quiQuitte) { this.quiQuitte = quiQuitte; }

    public String getQuiRejoins() { return quiRejoins; }
    public void setQuiRejoins(String quiRejoins) { this.quiRejoins = quiRejoins; }

    public DesDTO getDes() { return des; }
    public void setDes(DesDTO des) { this.des = des; }

    public List<CarteDTO> getSupposition() { return supposition; }
    public void setSupposition(List<CarteDTO> supposition) { this.supposition = supposition; }

    public Boolean getAccusation() { return accusation; }
    public void setAccusation(Boolean accusation) { this.accusation = accusation; }

    public PointDTO getPositionCourante() { return positionCourante; }
    public void setPositionCourante(PointDTO positionCourante) { this.positionCourante = positionCourante; }

    public PointDTO getPositionCourantePassage() { return positionCourantePassage; }
    public void setPositionCourantePassage(PointDTO positionCourantePassage) { this.positionCourantePassage = positionCourantePassage; }

    public boolean isLancerPartie() { return lancerPartie; }
    public void setLancerPartie(boolean lancerPartie) { this.lancerPartie = lancerPartie; }

    public boolean isTerminerPartie() { return terminerPartie; }
    public void setTerminerPartie(boolean terminerPartie) { this.terminerPartie = terminerPartie; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public PartieDTO getPartie() { return partie; }
    public void setPartie(PartieDTO partie) { this.partie = partie; }

    public JoueurDTO getLeJoueur() { return leJoueur; }
    public void setLeJoueur(JoueurDTO leJoueur) { this.leJoueur = leJoueur; }

    public String getPersoQuiJoue() { return persoQuiJoue; }
    public void setPersoQuiJoue(String persoQuiJoue) { this.persoQuiJoue = persoQuiJoue; }

    public JoueurDTO getQuiDoitReveler() { return quiDoitReveler; }
    public void setQuiDoitReveler(JoueurDTO quiDoitReveler) { this.quiDoitReveler = quiDoitReveler; }

    public JoueurDTO getaQuiLeTour() { return aQuiLeTour; }
    public void setaQuiLeTour(JoueurDTO aQuiLeTour) { this.aQuiLeTour = aQuiLeTour; }

    public JoueurDTO getGagnant() { return gagnant; }
    public void setGagnant(JoueurDTO gagnant) { this.gagnant = gagnant; }

    public List<CarteDTO> getReveleCartes() { return reveleCartes; }
    public void setReveleCartes(List<CarteDTO> reveleCartes) { this.reveleCartes = reveleCartes; }

    public CarteDTO getCarteRevelee() { return carteRevelee; }
    public void setCarteRevelee(CarteDTO carteRevelee) { this.carteRevelee = carteRevelee; }

    public List<JoueurDTO> getaQuiReveler() { return aQuiReveler; }
    public void setaQuiReveler(List<JoueurDTO> aQuiReveler) { this.aQuiReveler = aQuiReveler; }

    public List<List<CarteDTO>> getCarnet() { return Carnet; }
    public void setCarnet(List<List<CarteDTO>> carnet) { Carnet = carnet; }

    public boolean isaPrisPassage() { return aPrisPassage; }
    public void setaPrisPassage(boolean aPrisPassage) { this.aPrisPassage = aPrisPassage; }

    public HashMap<Joueur, List<Carte>> getPiocheCarte() { return piocheCarte; }
    public void setPiocheCarte(HashMap<Joueur, List<Carte>> piocheCarte) { this.piocheCarte = piocheCarte; }

    public PartieDTO getPartieSauvegarde() { return partieSauvegarde; }
    public void setPartieSauvegarde(PartieDTO partieSauvegarde) { this.partieSauvegarde = partieSauvegarde; }

    public CarteDTO getCarteIndiceAreveler() { return carteIndiceAreveler; }
    public void setCarteIndiceAreveler(CarteDTO carteIndiceAreveler) { this.carteIndiceAreveler = carteIndiceAreveler; }
}
