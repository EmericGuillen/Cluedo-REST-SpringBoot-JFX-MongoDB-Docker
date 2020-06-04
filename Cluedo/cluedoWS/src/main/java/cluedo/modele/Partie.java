package cluedo.modele;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Document(collection = "cluedi")
public class Partie {


    /*
     * ATTRIBUTS
     */
    private static int IDSPARTIE = 1;
    @Id
    private String idPartie;
    private String mdj;
    private String joueurCourant;
    private String statut;
    private List<Carte> pioche = new ArrayList<>();
    private List<Carte> solution = new ArrayList<>();
    private List<Carte> hypotheseCourante = new ArrayList<>();
    private List<String> invites = new ArrayList<>();
    private List<Joueur> joueurs = new ArrayList<>();
    private List<String> connectes = new ArrayList<>();
    private List<String> persosJoues = new ArrayList<>();


    /*
     * CONSTRUCTEUR
     */
    public Partie() {

        idPartie = String.valueOf(IDSPARTIE++);
        Utilisateur.setIDSUTIL(IDSPARTIE);
    }

    public Partie(String mdj, List<String> invites) {
        idPartie = String.valueOf(IDSPARTIE++);
        System.out.println(idPartie);
        this.mdj = mdj;
        this.joueurCourant = null;
        this.statut = "attente";
        this.invites = invites;
        this.connectes.add(mdj);
    }


    /*
     * GETTERS / SETTERS
     */
    public String getIdPartie() { return idPartie; }
    public void setIdPartie(String idPartie) { this.idPartie = idPartie; }

    public String getMdj() { return mdj; }
    public void setMdj(String mdj) { this.mdj = mdj; }

    public String getJoueurCourant() { return joueurCourant; }
    public void setJoueurCourant(String joueurCourant) { this.joueurCourant = joueurCourant; }

    public List<Carte> getPioche() { return pioche; }
    public void setPioche(List<Carte> pioche) { this.pioche = pioche; }

    public List<Carte> getSolution() { return solution; }
    public void setSolution(List<Carte> solution) { this.solution = solution; }

    public List<String> getInvites() { return invites; }
    public void setInvites(List<String> invites) { this.invites = invites; }

    public List<String> getConnectes() { return connectes; }
    public void setConnectes(List<String> connectes) { this.connectes = connectes; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public static int getIDSPARTIE() { return IDSPARTIE; }
    public static void setIDSPARTIE(int IDSPARTIE) { Partie.IDSPARTIE = IDSPARTIE; }

    public List<Joueur> getJoueurs() { return joueurs; }
    public void setJoueurs(List<Joueur> joueurs) { this.joueurs = joueurs; }

    public List<Carte> getHypotheseCourante() { return hypotheseCourante; }
    public void setHypotheseCourante(List<Carte> hypotheseCourante) { this.hypotheseCourante = hypotheseCourante; }

    public List<String> getPersosJoues() { return persosJoues; }
    public void setPersosJoues(List<String> persosJoues) { this.persosJoues = persosJoues; }

    /*
     * METHODES
     */
    public void ajouterConnecte(String pseudo) {
        connectes.add(pseudo);
    }

    public void enleverConnecte(String pseudo) {
        connectes.remove(pseudo);
    }

    public void enleverInvite(String pseudo) {
        invites.remove(pseudo);
    }

    public void reinitialiserConnectes() {
        this.setConnectes(new ArrayList<>());
    }

    public void restaurerPartie() {
        this.setStatut("attente");
        this.ajouterConnecte(this.getMdj());
    }

    public void ajouterJoueur(Joueur joueur) {
        joueurs.add(joueur);
    }

    public void instancierJoueurs(List<Carte> listeCarteJeu, List<Carte> listePerso, List<Carte> solution) {

        int i = 0;


        List<String> persosJoues = new ArrayList<>();


        for(Carte carte : solution){
            listeCarteJeu.remove(carte);
        }

        for (String pseudo : connectes) {

            Joueur joueur = new Joueur(pseudo);

            joueur.setStatutJoueur("joue");
            joueur.setPerso(listePerso.get(i));
            joueur.initialiserPosition();
            persosJoues.add(joueur.getPerso().getNom());

            for (Carte carte : listeCarteJeu) {
                if (listeCarteJeu.indexOf(carte) % connectes.size() == i)
                    joueur.addCarteToDeck(carte);
            }

            joueur.initialiserBlocNote(listeCarteJeu);
            joueurs.add(joueur);

            i++;
        }

        instancierPersosJoues(persosJoues);
        instancierJoueurCourant();


    }


    public void updateJoueur(Joueur joueur, int i) {
        joueurs.set(i, joueur);
    }


    public Joueur getUnJoueur(String nomJoueur){
        for (Joueur joueur : joueurs) {
            if (nomJoueur.equals(joueur.getUtil()))
                return joueur;
        }

        return null;
    }

    public Joueur getJoueurParPerso(String perso) {
        System.out.println("perso = " + perso);
        for (Joueur joueur : joueurs){
            System.out.println("perso joueur = " + joueur.getPerso().getNom());
            if (joueur.getPerso().getNom().equals(perso))
                return joueur;
        }
        return null;
    }

    public Joueur getLeJoueurCourant(){
        return getUnJoueur(getJoueurCourant());
    }

    public void retirerPersoJoue(String perso){ persosJoues.remove(perso); }



    public void instancierJoueurCourant() {

        if (persosJoues.contains("Mademoiselle Rose"))
            joueurCourant = getJoueurParPerso("Mademoiselle Rose").getUtil();
        else if (persosJoues.contains("Colonel Moutarde"))
            joueurCourant = getJoueurParPerso("Colonel Moutarde").getUtil();
        else if (persosJoues.contains("Docteur Orchidée"))
            joueurCourant = getJoueurParPerso("Docteur Orchidée").getUtil();
        else if (persosJoues.contains("Monsieur Olive"))
            joueurCourant = getJoueurParPerso("Monsieur Olive").getUtil();
        else if (persosJoues.contains("Madame Pervenche"))
            joueurCourant = getJoueurParPerso("Madame Pervenche").getUtil();
        else if (persosJoues.contains("Professeur Violet"))
            joueurCourant = getJoueurParPerso("Professeur Violet").getUtil();

    }

    public void instancierPersosJoues(List<String> persosJoues) {
        if(persosJoues.contains("Mademoiselle Rose"))
            this.persosJoues.add("Mademoiselle Rose");
        if(persosJoues.contains("Colonel Moutarde"))
            this.persosJoues.add("Colonel Moutarde");
        if(persosJoues.contains("Docteur Orchidée"))
            this.persosJoues.add("Docteur Orchidée");
        if(persosJoues.contains("Monsieur Olive"))
            this.persosJoues.add("Monsieur Olive");
        if(persosJoues.contains("Madame Pervenche"))
            this.persosJoues.add("Madame Pervenche");
        if(persosJoues.contains("Professeur Violet"))
            this.persosJoues.add("Professeur Violet");

    }



    public void changerJoueurCourant(){

        System.out.println("Joueur courant = " + joueurCourant);
        System.out.println("Perso courant  = " + getLeJoueurCourant().getPerso().getNom());

        int indexJoueurCourant = persosJoues.indexOf(getLeJoueurCourant().getPerso().getNom());

        System.out.println("indexJoueurCourant = " + indexJoueurCourant);

        if(indexJoueurCourant == persosJoues.size()-1 )
            setJoueurCourant( getJoueurParPerso(persosJoues.get(0)).getUtil());
        else
            setJoueurCourant( getJoueurParPerso(persosJoues.get(indexJoueurCourant +1)).getUtil() );

    }


    public boolean isGagnant(){
        for(Joueur joueur: getJoueurs()){
            System.out.println("joueur = " + joueur);
            System.out.println("joueur = " + joueur.getStatutJoueur());
            if(joueur.getStatutJoueur().equals("gagne"))
                return true;
        }
        return false;
    }


    public Joueur getGagnant(){
        for(Joueur joueur: getJoueurs()) {
            if (joueur.getStatutJoueur().equals("gagne"))
                return joueur;
        }
        return null;
    }


    public void majPioche(){

        Carte carte = this.getPioche().get(0);
        this.getPioche().remove(0);
        List<Carte> pioche = getPioche();
        pioche.add(carte);
        this.setPioche(pioche);

    }

    @Override
    public String toString(){
        return this.getIdPartie() + " avec mdj " + this.getMdj();
    }

}