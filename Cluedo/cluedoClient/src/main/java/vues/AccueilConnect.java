package vues;

import controleur.Controleur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import modele.exceptions.*;

import java.io.IOException;
import java.net.URL;

public class AccueilConnect {
    private Controleur monControleur;

    @FXML
    private Label nom;

    public static AccueilConnect creerEtAfficher(Controleur c, Stage laStageUnique) throws RecupInfoUtilisateurImpossibleException {
        URL location = Accueil.class.getResource("/vues/accueilConnect.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Parent root = null;
        try {
            root = (Parent) fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        AccueilConnect vue = fxmlLoader.getController();
        laStageUnique.setTitle("Accueil connecté");
        laStageUnique.getScene().setRoot(root);
        laStageUnique.setFullScreenExitHint("");
        laStageUnique.setFullScreen(true);
        vue.setMonControleur(c);
        //vue.setPseudo();
        laStageUnique.show();
        return vue;
    }

    public void setPseudo() throws RecupInfoUtilisateurImpossibleException {
        this.nom.setText(this.monControleur.userGet().getNom());
    }

    public void setMonControleur(Controleur monControleur) {
        this.monControleur = monControleur;
    }


    public void creerPartieOk(ActionEvent actionEvent) throws RecuperationUtilisateursImpossibleException {
        this.monControleur.goToCreerPartiePage();
    }

    public void rejoindrePartieOk(ActionEvent actionEvent) throws RecuperationUtilisateursImpossibleException, RecuperationPartiesImpossibleException {
        this.monControleur.goToRejoindrePartie();
    }

    public void deconnexionOk(ActionEvent actionEvent) throws DeconnexionImpossibleException{
        this.monControleur.deconnexion();
    }

    public void desabonnementOk(ActionEvent actionEvent) {
        this.monControleur.goToDesabonnementPage();
    }

    public void restaurerPartieOk(ActionEvent actionEvent) throws RecuperationPartiesImpossibleException, DemandeSauvegardeImpossibleException {
        this.monControleur.goToRestaurerPartie();
    }
}

