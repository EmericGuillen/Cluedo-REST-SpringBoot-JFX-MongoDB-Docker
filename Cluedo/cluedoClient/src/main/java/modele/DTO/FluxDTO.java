package modele.DTO;

import java.util.HashMap;
import java.util.List;

public class FluxDTO {
    private String quiJoue = null;
    private String persoQuiJoue = null;
    private String quiQuitte = null;
    private String quiRejoins = null;
    private String action = null;
    private DesDTO des = null;
    private List<CarteDTO> supposition = null;
    private boolean accusation = false;
    private List<CarteDTO> accusationCartes = null;
    private List<CarteDTO> reveleCartes = null;
    private CarteDTO carteRevelee = null;
    private PointDTO positionCourante = null;
    private PointDTO positionCourantePassage = null;
    private boolean lancerPartie = false;
    private boolean terminerPartie = false;
    private PartieDTO partie = null;
    private JoueurDTO leJoueur = null;
    private JoueurDTO quiDoitReveler = null;
    private JoueurDTO aQuiLeTour = null;
    private JoueurDTO gagnant = null;
    private List<JoueurDTO> aQuiReveler = null;
    private List<List<CarteDTO>> carnet = null;
    private boolean aPrisPassage = false;
    private CarteDTO carteIndiceAreveler = null;


    public FluxDTO() {
    }

    public CarteDTO getCarteRevelee() {
        return carteRevelee;
    }

    public void setCarteRevelee(CarteDTO carteRevelee) {
        this.carteRevelee = carteRevelee;
    }

    public List<JoueurDTO> getaQuiReveler() {
        return aQuiReveler;
    }

    public void setaQuiReveler(List<JoueurDTO> aQuiReveler) {
        this.aQuiReveler = aQuiReveler;
    }

    public List<List<CarteDTO>> getCarnet() {
        return carnet;
    }

    public void setCarnet(List<List<CarteDTO>> carnet) {
        this.carnet = carnet;
    }

    public List<CarteDTO> getReveleCartes() {
        return reveleCartes;
    }

    public void setReveleCartes(List<CarteDTO> reveleCartes) {
        this.reveleCartes = reveleCartes;
    }

    public JoueurDTO getGagnant() {
        return gagnant;
    }

    public void setGagnant(JoueurDTO gagnant) {
        this.gagnant = gagnant;
    }

    public JoueurDTO getQuiDoitReveler() {
        return quiDoitReveler;
    }

    public void setQuiDoitReveler(JoueurDTO quiDoitReveler) {
        this.quiDoitReveler = quiDoitReveler;
    }

    public String getPersoQuiJoue() {
        return persoQuiJoue;
    }

    public void setPersoQuiJoue(String persoQuiJoue) {
        this.persoQuiJoue = persoQuiJoue;
    }

    public JoueurDTO getLeJoueur() {
        return leJoueur;
    }

    public void setLeJoueur(JoueurDTO leJoueur) {
        this.leJoueur = leJoueur;
    }

    public String getQuiJoue() {
        return quiJoue;
    }

    public void setQuiJoue(String quiJoue) {
        this.quiJoue = quiJoue;
    }

    public String getQuiQuitte() {
        return quiQuitte;
    }

    public void setQuiQuitte(String quiQuitte) {
        this.quiQuitte = quiQuitte;
    }

    public String getQuiRejoins() {
        return quiRejoins;
    }

    public void setQuiRejoins(String quiRejoins) {
        this.quiRejoins = quiRejoins;
    }

    public DesDTO getDes() {
        return des;
    }

    public void setDes(DesDTO des) {
        this.des = des;
    }

    public List<CarteDTO> getSupposition() {
        return supposition;
    }

    public void setSupposition(List<CarteDTO> supposition) {
        this.supposition = supposition;
    }


    public PointDTO getPositionCourante() {
        return positionCourante;
    }

    public void setPositionCourante(PointDTO positionCourante) {
        this.positionCourante = positionCourante;
    }

    public PointDTO getPositionCourantePassage() {
        return positionCourantePassage;
    }

    public void setPositionCourantePassage(PointDTO positionCourantePassage) {
        this.positionCourantePassage = positionCourantePassage;
    }

    public boolean isLancerPartie() {
        return lancerPartie;
    }

    public void setLancerPartie(boolean lancerPartie) {
        this.lancerPartie = lancerPartie;
    }

    public boolean isTerminerPartie() {
        return terminerPartie;
    }

    public void setTerminerPartie(boolean terminerPartie) {
        this.terminerPartie = terminerPartie;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean isAccusation() {
        return accusation;
    }

    public void setAccusation(boolean accusation) {
        this.accusation = accusation;
    }

    public List<CarteDTO> getAccusationCartes() {
        return accusationCartes;
    }

    public void setAccusationCartes(List<CarteDTO> accusationCartes) {
        this.accusationCartes = accusationCartes;
    }

    public PartieDTO getPartie() {
        return partie;
    }

    public void setPartie(PartieDTO partie) {
        this.partie = partie;
    }

    public JoueurDTO getaQuiLeTour() {
        return aQuiLeTour;
    }

    public void setaQuiLeTour(JoueurDTO aQuiLeTour) {
        this.aQuiLeTour = aQuiLeTour;
    }

    public boolean isaPrisPassage() {
        return aPrisPassage;
    }

    public void setaPrisPassage(boolean aPrisPassage) {
        this.aPrisPassage = aPrisPassage;
    }

    public CarteDTO getCarteIndiceAreveler() {
        return carteIndiceAreveler;
    }

    public void setCarteIndiceAreveler(CarteDTO carteIndiceAreveler) {
        this.carteIndiceAreveler = carteIndiceAreveler;
    }

    @Override
    public String toString() {
        String res = "";
        switch (this.action){
            case "DES":
                res = this.getQuiJoue()+ " ("+ this.persoQuiJoue +")" + " a lancé les dés et obtenu "+this.getDes().getSomme();
                break;
            case "SUPPOSE":
                res = this.quiDoitReveler.getUtil()+ " ("+ this.quiDoitReveler.getPerso().getNom() +")" + " doit révéler une carte à "+this.quiJoue+ " ("+ this.getPersoQuiJoue() +")\n"+
                this.quiJoue+ " ("+ this.persoQuiJoue +")" + " a suspecté "+this.supposition.get(2).getNom()+" d'avoir tué Le Noir avec "+this.supposition.get(1).getNom()+" dans "+this.supposition.get(0).getNom();
                break;
            case "REVELER":
                res = this.quiDoitReveler.getUtil()+ " ("+ this.quiDoitReveler.getPerso().getNom() +")"+ " a montré une carte à "+this.quiJoue+ " ("+ this.getPersoQuiJoue() +")";
                break;
            case "ACCUSE":
                res = this.quiJoue+ " ("+ this.persoQuiJoue +")" + " a accusé "+this.supposition.get(2).getNom()+" d'avoir tué Le Noir avec "+this.supposition.get(1).getNom()+" dans "+this.supposition.get(0).getNom()+"\n"
                +" et ";
                res += this.accusation?"il a gagné !":"il a perdu !";
                break;
            case "PIOCHE_CARTE":
                res = this.quiJoue+ " ("+ this.persoQuiJoue +")" + " a pioché la carte indice : "+this.carteIndiceAreveler.getNom();
                break;
            case "LANCER PARTIE":
                res = "C'est à " + this.partie.getJoueurCourant() + " de jouer !\n"+
                        "La partie a été lancée !";
                break;
            case "REJOINT":
                res = this.quiRejoins + " a rejoint la partie !";
                break;
            case "RESTAURE":
                res = "C'est à " + this.partie.getJoueurCourant() + " de jouer !\n"+
                        "La partie a été restaurée !";
                break;
            case "TERMINE TOUR":
                res = "C'est à "+ this.aQuiLeTour.getUtil()+ " ("+ this.aQuiLeTour.getPerso().getNom() +")" + " de jouer !";
                break;
            case "QUITTER_PARTIE":
                res = this.quiQuitte+ " a quitté la partie !";
                break;
        }
        return res;
    }
}
