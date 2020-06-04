package vues;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LecteurTXT {

    private String chemin;
    private BufferedReader lecteurAvecBuffer = null;
    private String ligne;
    private String mur_bas = "A";
    private String mur_bas_droite = "B";
    private String mur_bas_gauche = "C";
    private String mur_bas_haut = "D";
    private String mur_droite = "E";
    private String mur_gauche = "F";
    private String mur_gauche_droite = "G";
    private String mur_haut = "H";
    private String mur_haut_droite = "I";
    private String mur_haut_gauche = "J";

    private String sol_normal = "K";
    private String sol_rose = "L";
    private String sol_violet = "M";
    private String sol_vert = "N";
    private String sol_jaune = "O";
    private String sol_rouge = "P";
    private String sol_bleu = "Q";
    private String sol_orange = "R";
    private String sol_noir = "S";
    private String sol_marron = "T";
    private String passage_bas_droite = "U";
    private String passage_bas_gauche = "V";
    private String passage_haut_droite = "3";
    private String passage_haut_gauche = "4";

    private String departRose = "W";
    private String departMoutarde = "X";
    private String departOlive = "Y";
    private String departOrchidee = "Z";
    private String departPervenche = "1";
    private String departViolet = "2";

    private String mur_bas_haut_droite = "7";
    private String mur_bas_haut_gauche = "8";
    private String mur_gauche_droite_bas = "5";
    private String mur_gauche_droite_haut = "6";

    private String chambre = "Chambre";
    private String salleManger = "Salle à manger";
    private String cuisine = "Cuisine";
    private String bureau = "Bureau";
    private String salleBillard = "Salle de billard";
    private String entree = "Entrée";
    private String escalier = "Escalier";
    private String salon = "Salon";
    private String salleReception = "Salle de réception";

    private List<List<String>> plateau;



    public LecteurTXT(String chemin){
        this.chemin = chemin;
        this.plateau=new ArrayList<>();
    }


    public void affiche(List<List<String>> plateau) throws IOException {

        /*
        plateau.forEach(i->{
            int ligne = plateau.indexOf(i);
            plateau.get(plateau.indexOf(i)).forEach(j->{
                System.out.print(plateau.get(plateau.indexOf(i)).get(plateau.get(plateau.indexOf(i)).indexOf(j)));
            });
            System.out.println();
        });
        */

        for(int j=0; j<26; j++){
            for(int i=0; i<14; i++){
                System.out.print(plateau.get(j).get(i));
            }
            System.out.println();
        }
    }

    public void createPlateau(BorderPane[][] gridCases, GridPane grid) throws Exception {
        try{
            InputStream flux=new FileInputStream(this.chemin);
            InputStreamReader lecture=new InputStreamReader(flux);
            BufferedReader buff=new BufferedReader(lecture);
            String ligne;

            while ((ligne=buff.readLine())!=null){
                List<String> ligneString = new ArrayList<>();
                for(int j=0; j<ligne.length();j++){

                    if(this.mur_bas.equals(ligne.split("")[j])){
                        ligneString.add(j,this.mur_bas);
                    }else if(this.mur_bas_droite.equals(ligne.split("")[j])){
                        ligneString.add(j,this.mur_bas_droite);
                    }else if(this.mur_bas_gauche.equals(ligne.split("")[j])){
                        ligneString.add(j,this.mur_bas_gauche);
                    }else if(this.mur_bas_haut.equals(ligne.split("")[j])){
                        ligneString.add(j,this.mur_bas_haut);
                    }else if(this.mur_droite.equals(ligne.split("")[j])){
                        ligneString.add(j,this.mur_droite);
                    }else if(this.mur_gauche.equals(ligne.split("")[j])){
                        ligneString.add(j,this.mur_gauche);
                    }else if(this.mur_gauche_droite.equals(ligne.split("")[j])){
                        ligneString.add(j,this.mur_gauche_droite);
                    }else if(this.mur_haut.equals(ligne.split("")[j])){
                        ligneString.add(j,this.mur_haut);
                    }else if(this.mur_haut_droite.equals(ligne.split("")[j])){
                        ligneString.add(j,this.mur_haut_droite);
                    }else if(this.mur_haut_gauche.equals(ligne.split("")[j])){
                        ligneString.add(j,this.mur_haut_gauche);
                    }else if(this.departRose.equals(ligne.split("")[j])){
                        ligneString.add(j,this.departRose);
                    }else if(this.departMoutarde.equals(ligne.split("")[j])){
                        ligneString.add(j,this.departMoutarde);
                    }else if(this.departOlive.equals(ligne.split("")[j])){
                        ligneString.add(j,this.departOlive);
                    }else if(this.departOrchidee.equals(ligne.split("")[j])){
                        ligneString.add(j,this.departOrchidee);
                    }else if(this.departPervenche.equals(ligne.split("")[j])){
                        ligneString.add(j,this.departPervenche);
                    }else if(this.departViolet.equals(ligne.split("")[j])){
                        ligneString.add(j,this.departViolet);
                    }


                    else if(this.sol_normal.equals(ligne.split("")[j])){
                        ligneString.add(j,this.sol_normal);
                    }
                    else if(this.sol_rose.equals(ligne.split("")[j])){
                        ligneString.add(j,this.sol_rose);
                    }
                    else if(this.sol_violet.equals(ligne.split("")[j])){
                        ligneString.add(j,this.sol_violet);
                    }
                    else if(this.sol_vert.equals(ligne.split("")[j])){
                        ligneString.add(j,this.sol_vert);
                    }
                    else if(this.sol_jaune.equals(ligne.split("")[j])){
                        ligneString.add(j,this.sol_jaune);
                    }
                    else if(this.sol_rouge.equals(ligne.split("")[j])){
                        ligneString.add(j,this.sol_rouge);
                    }
                    else if(this.sol_bleu.equals(ligne.split("")[j])){
                        ligneString.add(j,this.sol_bleu);
                    }
                    else if(this.sol_orange.equals(ligne.split("")[j])){
                        ligneString.add(j,this.sol_orange);
                    }
                    else if(this.sol_noir.equals(ligne.split("")[j])){
                        ligneString.add(j,this.sol_noir);
                    }
                    else if(this.sol_marron.equals(ligne.split("")[j])){
                        ligneString.add(j,this.sol_marron);
                    }
                    else if(this.passage_bas_droite.equals(ligne.split("")[j])){
                        ligneString.add(j,this.passage_bas_droite);
                    }
                    else if(this.passage_bas_gauche.equals(ligne.split("")[j])){
                        ligneString.add(j,this.passage_bas_gauche);
                    }
                    else if(this.passage_haut_droite.equals(ligne.split("")[j])){
                        ligneString.add(j,this.passage_haut_droite);
                    }
                    else if(this.passage_haut_gauche.equals(ligne.split("")[j])){
                        ligneString.add(j,this.passage_haut_gauche);
                    }
                    else if(this.mur_bas_haut_droite.equals(ligne.split("")[j])){
                        ligneString.add(j,this.mur_bas_haut_droite);
                    }
                    else if(this.mur_bas_haut_gauche.equals(ligne.split("")[j])){
                        ligneString.add(j,this.mur_bas_haut_gauche);
                    }
                    else if(this.mur_gauche_droite_bas.equals(ligne.split("")[j])){
                        ligneString.add(j,this.mur_gauche_droite_bas);
                    }
                    else if(this.mur_gauche_droite_haut.equals(ligne.split("")[j])){
                        ligneString.add(j,this.mur_gauche_droite_haut);
                    }

                }
                this.plateau.add(ligneString);

            }
            buff.close();



            for(int j=0; j<28; j++){
                for(int i=0; i<16; i++){
                    BorderPane b = new BorderPane();
                    gridCases[j][i] = b;
                    gridCases[j][i].setPrefSize(33, 33);
                    grid.add(gridCases[j][i], j, i);
                    grid.setGridLinesVisible(true);


                    switch (plateau.get(i).get(j)){
                        case "A":
                            gridCases[j][i].setStyle("-fx-background-image:url('/images/murs/mur_bas.png')");
                            break;
                        case "B":
                            gridCases[j][i].setStyle("-fx-background-image:url('/images/murs/mur_bas_droite.png')");
                            break;
                        case "C":
                            gridCases[j][i].setStyle("-fx-background-image:url('/images/murs/mur_bas_gauche.png')");
                            break;
                        case "D":
                            gridCases[j][i].setStyle("-fx-background-image:url('/images/murs/mur_bas_haut.png')");
                            break;
                        case "E":
                            gridCases[j][i].setStyle("-fx-background-image:url('/images/murs/mur_droite.png')");
                            break;
                        case "F":
                            gridCases[j][i].setStyle("-fx-background-image:url('/images/murs/mur_gauche.png')");
                            break;
                        case "G":
                            gridCases[j][i].setStyle("-fx-background-image:url('/images/murs/mur_gauche_droite.png')");
                            break;
                        case "H":
                            gridCases[j][i].setStyle("-fx-background-image:url('/images/murs/mur_haut.png')");
                            break;
                        case "I":
                            gridCases[j][i].setStyle("-fx-background-image:url('/images/murs/mur_haut_droite.png')");
                            break;
                        case "J":
                            gridCases[j][i].setStyle("-fx-background-image:url('/images/murs/mur_haut_gauche.png')");
                            break;


                        case "K":
                            gridCases[j][i].setStyle("-fx-background-image:url('/images/cases/case_beige.png')");
                            break;
                        case "L":
                            gridCases[j][i].setStyle("-fx-background-image:url('/images/cases/parquet_rose.png')");
                            break;
                        case "M":
                            gridCases[j][i].setStyle("-fx-background-image:url('/images/cases/parquet_violet.png')");
                            break;
                        case "N":
                            gridCases[j][i].setStyle("-fx-background-image:url('/images/cases/parquet_vert.png')");
                            break;
                        case "O":
                            gridCases[j][i].setStyle("-fx-background-image:url('/images/cases/parquet_jaune.png')");
                            break;
                        case "P":
                            gridCases[j][i].setStyle("-fx-background-image:url('/images/cases/parquet_rouge.png')");
                            break;
                        case "Q":
                            gridCases[j][i].setStyle("-fx-background-image:url('/images/cases/parquet_bleu.png')");
                            break;
                        case "R":
                            gridCases[j][i].setStyle("-fx-background-image:url('/images/cases/parquet_orange.png')");
                            break;
                        case "S":
                            gridCases[j][i].setStyle("-fx-background-image:url('/images/cases/parquet_noir.png')");
                            break;
                        case "T":
                            gridCases[j][i].setStyle("-fx-background-image:url('/images/cases/parquet.png')");
                            break;
                        case "U":
                            gridCases[j][i].setStyle("-fx-background-image:url('/images/cases/passage_bas_droite.png')");
                            break;
                        case "V":
                            gridCases[j][i].setStyle("-fx-background-image:url('/images/cases/passage_bas_gauche.png')");
                            break;
                        case "3":
                            gridCases[j][i].setStyle("-fx-background-image:url('/images/cases/passage_haut_droite.png')");
                            break;
                        case "4":
                            gridCases[j][i].setStyle("-fx-background-image:url('/images/cases/passage_haut_gauche.png')");
                            break;
                        case "W":
                            gridCases[j][i].setStyle("-fx-background-image:url('/images/cases/case_depart_rose.png')");
                            break;
                        case "X":
                            gridCases[j][i].setStyle("-fx-background-image:url('/images/cases/case_depart_moutarde.png')");
                            break;
                        case "Y":
                            gridCases[j][i].setStyle("-fx-background-image:url('/images/cases/case_depart_olive.png')");
                            break;
                        case "Z":
                            gridCases[j][i].setStyle("-fx-background-image:url('/images/cases/case_depart_orchidée.png')");
                            break;
                        case "1":
                            gridCases[j][i].setStyle("-fx-background-image:url('/images/cases/case_depart_pervenche.png')");
                            break;
                        case "2":
                            gridCases[j][i].setStyle("-fx-background-image:url('/images/cases/case_depart_violet.png')");
                            break;
                        case "7":
                            gridCases[j][i].setStyle("-fx-background-image:url('/images/murs/mur_bas_haut_droite.png')");
                            break;
                        case "8":
                            gridCases[j][i].setStyle("-fx-background-image:url('/images/murs/mur_bas_haut_gauche.png')");
                            break;
                        case "5":
                            gridCases[j][i].setStyle("-fx-background-image:url('/images/murs/mur_gauche_droite_bas.png')");
                            break;
                        case "6":
                            gridCases[j][i].setStyle("-fx-background-image:url('/images/murs/mur_gauche_droite_haut.png')");
                            break;
                    }

                }
            }

        }
        catch (Exception e){
            System.out.println(e.toString());
        }
    }

    public List<List<String>> getPlateau() {
        return this.plateau;
    }

    public String getSol_normal() {
        return sol_normal;
    }

    public String getSol_rose() {
        return sol_rose;
    }

    public String getSol_violet() {
        return sol_violet;
    }

    public String getSol_vert() {
        return sol_vert;
    }

    public String getSol_jaune() {
        return sol_jaune;
    }

    public String getSol_rouge() {
        return sol_rouge;
    }

    public String getSol_bleu() {
        return sol_bleu;
    }

    public String getSol_orange() {
        return sol_orange;
    }

    public String getSol_noir() {
        return sol_noir;
    }

    public String getSol_marron() {
        return sol_marron;
    }

    public String getChambre() {
        return chambre;
    }

    public void setChambre(String chambre) {
        this.chambre = chambre;
    }

    public String getSalleManger() {
        return salleManger;
    }

    public void setSalleManger(String salleManger) {
        this.salleManger = salleManger;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getBureau() {
        return bureau;
    }

    public void setBureau(String bureau) {
        this.bureau = bureau;
    }

    public String getSalleBillard() {
        return salleBillard;
    }

    public void setSalleBillard(String salleBillard) {
        this.salleBillard = salleBillard;
    }

    public String getEntree() {
        return entree;
    }

    public void setEntree(String entree) {
        this.entree = entree;
    }

    public String getEscalier() {
        return escalier;
    }

    public void setEscalier(String escalier) {
        this.escalier = escalier;
    }

    public String getSalon() {
        return salon;
    }

    public void setSalon(String salon) {
        this.salon = salon;
    }

    public String getSalleReception() {
        return salleReception;
    }

    public void setSalleReception(String salleReception) {
        this.salleReception = salleReception;
    }

    public List<String> getSolCouleurs(){
        return List.of(this.sol_jaune,this.sol_orange,this.sol_rose,this.sol_rouge,this.sol_bleu,this.sol_marron,
        this.sol_noir,this.sol_vert,this.sol_violet);
    }

    public String lettrePiece(int x, int y){
        return this.plateau.get(x).get(y);
    }

    public String nomPiece(String lettrePiece){
        String nom = "";
        switch (lettrePiece){
            case "O":
                nom = this.chambre;
                break;

            case "R":
                nom = this.salleManger;
                break;

            case "L":
                nom = this.cuisine;
                break;

            case "P":
                nom = this.bureau;
                break;

            case "Q":
                nom = this.salleBillard;
                break;

            case "T":
                nom = this.entree;
                break;

            case "S":
                nom = this.escalier;
                break;

            case "N":
                nom = this.salon;
                break;

            case "M":
                nom = this.salleReception;
                break;
        }

        return nom;
    }


}
