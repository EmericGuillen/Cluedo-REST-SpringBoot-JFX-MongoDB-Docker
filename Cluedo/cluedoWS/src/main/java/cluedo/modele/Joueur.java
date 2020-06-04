package cluedo.modele;

import cluedo.DTO.CarteDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Joueur {

    /*
     * ATTRIBUTS
     */
    private String util;
    private String piece;
    private Carte perso; //mettre une carte personnage plutôt qu'un string
    private String statutJoueur; //perdu, joue, quitte
    private Point positionCourante;
    private List<Carte> deck = new ArrayList<>();
    private List<List<Carte>> blocnote = new ArrayList<>();
    //Liste 1 : cartes écartées   -   Liste 2 : cartes doute   -  Liste 3 : carte validées


    /*
     * CONSTRUCTEUR
     */
    public Joueur(){}

    public Joueur(String util) {

        this.util = util;
        this.piece = "";
        this.statutJoueur = "";
        blocnote.add(new ArrayList<>());
        blocnote.add(new ArrayList<>());
        blocnote.add(new ArrayList<>());

    }


    /*
     * GETTERS / SETTERS
     */
    public String getUtil() { return util; }
    public void setUtil(String util) { this.util = util; }

    public Carte getPerso() { return perso; }
    public void setPerso(Carte perso) { this.perso = perso; }

    public List<Carte> getDeck() { return deck; }
    public void setDeck(List<Carte> deck) { this.deck = deck; }

    public Point getPositionCourante() { return positionCourante; }
    public void setPositionCourante(Point positionCourante) { this.positionCourante = positionCourante; }

    public String getStatutJoueur() { return statutJoueur; }
    public void setStatutJoueur(String statutJoueur) { this.statutJoueur = statutJoueur; }

    public List<List<Carte>> getBlocnote() { return blocnote; }
    public void setBlocnote(List<List<Carte>> blocnote) { this.blocnote = blocnote; }

    public String getPiece() { return piece; }
    public void setPiece(String piece) { this.piece = piece; }

    /*
     * METHODES
     */
    public void addCarteToDeck(Carte carte){
        deck.add(carte);
        //Ajoute la carte au blocnote en tant que carte écartée
        blocnote.get(0).add(carte);
    }

    public void addCarteBlocNote(Carte carte, String statut){
        if(statut.equals("ecarte"))
            blocnote.get(0).add(carte);

        if(statut.equals("doute"))
            blocnote.get(1).add(carte);

        if(statut.equals("valide"))
            blocnote.get(2).add(carte);
    }


    public void addCarteBlocNote(Carte carte, int index){
            blocnote.get(index).add(carte);
    }

    public void ajouterListeBlocnote(List<Carte> listeCarte, int index){

        for(Carte carte: listeCarte){
            addCarteBlocNote(carte, index);
        }

    }

    public void addListesToBlocNote(List<List<Carte>> listeDeListes){

        for(List<Carte> liste: listeDeListes){
            for(Carte carte: liste){
                addCarteBlocNote(carte, listeDeListes.indexOf(liste));
            }
        }
    }


    public void initialiserPosition(){
        switch (this.getPerso().getNom()) {
            case "Mademoiselle Rose":
                setPositionCourante(new Point(10, 14));
                break;

            case "Colonel Moutarde":
                setPositionCourante(new Point(1, 10));
                break;

            case "Docteur Orchidée":
                setPositionCourante(new Point(9, 1));
                break;

            case "Monsieur Olive":
                setPositionCourante(new Point(18, 1));
                break;

            case "Madame Pervenche":
                setPositionCourante(new Point(26, 7));
                break;

            case "Professeur Violet":
                setPositionCourante(new Point(26, 12));
                break;

            default:
                break;
        }
    }

    public boolean isJoueurEnJeu(){
        if(getStatutJoueur().equals("joue"))
            return true;
        else
            return false;
    }


    public boolean isCarteInDeck(Carte carte){
        for(Carte carteDeck: deck){
            if(carteDeck.getNom().equals(carte.getNom()))
                return true;
        }
        return false;
    }


    public void initialiserBlocNote(List<Carte> cartesJeu){

        for(Carte carte: cartesJeu){
            if( !isCarteInDeck(carte) ){
                addCarteBlocNote(carte, 1);
            }
        }
    }


    @Override
    public String toString(){
        return this.getUtil() + ", perso : " + this.getPerso().getNom();
    }
}
