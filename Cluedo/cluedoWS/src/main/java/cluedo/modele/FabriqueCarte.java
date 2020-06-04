package cluedo.modele;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FabriqueCarte {

    /*
     * ATTRIBUTS
     */
    private List<Carte> cartesJeu = new ArrayList<>();
    private List<Carte> cartesIndices = new ArrayList<>();



    /*
     * CONSTRUCTEUR
     */
    public FabriqueCarte(){

        //========================= Cartes de jeu =========================
        cartesJeu.add(new Carte("perso","Colonel Moutarde"));
        cartesJeu.add(new Carte("perso","Professeur Violet" ));
        cartesJeu.add(new Carte("perso","Docteur Orchidée" ));
        cartesJeu.add(new Carte("perso","Mademoiselle Rose" ));
        cartesJeu.add(new Carte("perso","Madame Pervenche" ));
        cartesJeu.add(new Carte("perso","Monsieur Olive" ));

        cartesJeu.add(new Carte("arme","Corde" ));
        cartesJeu.add(new Carte("arme","Poignard" ));
        cartesJeu.add(new Carte("arme","Clé anglaise" ));
        cartesJeu.add(new Carte("arme","Chandelier" ));
        cartesJeu.add(new Carte("arme","Révolver" ));
        cartesJeu.add(new Carte("arme","Barre de fer" ));

        //cartesJeu.add(new Carte("lieu","Bibliothèque" ));
        //cartesJeu.add(new Carte("lieu","Jardin d'hiver" ));
        cartesJeu.add(new Carte("lieu","Salle à manger" ));
        cartesJeu.add(new Carte("lieu","Chambre" ));
        cartesJeu.add(new Carte("lieu","Cuisine" ));
        cartesJeu.add(new Carte("lieu","Bureau" ));
        cartesJeu.add(new Carte("lieu","Salle de billard" ));
        cartesJeu.add(new Carte("lieu","Entrée" ));
        cartesJeu.add(new Carte("lieu","Salon" ));
        cartesJeu.add(new Carte("lieu","Salle de réception" ));


        //========================= Cartes indice =========================
        cartesIndices.add(new Carte("qui","Colonel Moutarde"));
        cartesIndices.add(new Carte("qui","Professeur Violet" ));
        cartesIndices.add(new Carte("qui","Docteur Orchidée" ));
        cartesIndices.add(new Carte("qui","Mademoiselle Rose" ));
        cartesIndices.add(new Carte("qui","Madame Pervenche" ));
        cartesIndices.add(new Carte("qui","Monsieur Olive" ));

        cartesIndices.add(new Carte("quoi","Corde" ));
        cartesIndices.add(new Carte("quoi","Poignard" ));
        cartesIndices.add(new Carte("quoi","Clé anglaise" ));
        cartesIndices.add(new Carte("quoi","Chandelier" ));
        cartesIndices.add(new Carte("quoi","Révolver" ));
        cartesIndices.add(new Carte("quoi","Barre de fer" ));

        cartesIndices.add(new Carte("ou","Bibliothèque" ));
        cartesIndices.add(new Carte("ou","Salle à manger" ));
        cartesIndices.add(new Carte("ou","Jardin d'hiver" ));
        cartesIndices.add(new Carte("ou","Salle de billard" ));
        cartesIndices.add(new Carte("ou","Salon" ));
        cartesIndices.add(new Carte("ou","Bureau" ));
        cartesIndices.add(new Carte("ou","Cuisine" ));
        cartesIndices.add(new Carte("ou","Entrée" ));
        cartesIndices.add(new Carte("ou","Salle de réception" ));
    }


    /*
     * GETTERS / SETTERS
     */
    public List<Carte> getCartesJeu() { return cartesJeu; }
    public void setCartesJeu(List<Carte> cartesJeu) { this.cartesJeu = cartesJeu; }

    public List<Carte> getCartesIndices() { return cartesIndices; }
    public void setCartesIndices(List<Carte> cartesIndices) { this.cartesIndices = cartesIndices; }


    /*
     * METHODES
     */
    public List<Carte> getCartePerso(){
        List<Carte> listePerso = new ArrayList<>();

        listePerso.add(new Carte("perso","Colonel Moutarde"));
        listePerso.add(new Carte("perso","Professeur Violet" ));
        listePerso.add(new Carte("perso","Docteur Orchidée" ));
        listePerso.add(new Carte("perso","Mademoiselle Rose" ));
        listePerso.add(new Carte("perso","Madame Pervenche" ));
        listePerso.add(new Carte("perso","Monsieur Olive" ));


        Collections.shuffle(listePerso);

        return listePerso;

    }


    public Carte getCarteJeuByNom(String nom){

        for(Carte carte : this.getCartesJeu()){
            if(carte.getNom().equals(nom))
                return carte;
        }

        return null;
    }

}
