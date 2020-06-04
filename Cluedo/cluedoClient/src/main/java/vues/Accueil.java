package vues;

import controleur.Controleur;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class Accueil {
    private Controleur monControleur;

    public static Accueil creerEtAfficher(Controleur c, Stage laStageUnique){
        URL location = Accueil.class.getResource("/vues/accueil.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Parent root = null;
        try {
            root = (Parent) fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Accueil vue = fxmlLoader.getController();
        laStageUnique.setTitle("Accueil");
        laStageUnique.setScene(new Scene(root, 800, 600));
        laStageUnique.setFullScreenExitHint("");
        laStageUnique.setFullScreen(true);
        laStageUnique.show();
        vue.setMonControleur(c);
        return vue;
    }

    public static Accueil retourAccueil(Controleur controleur, Stage laStageUnique) {

        URL location = Accueil.class.getResource("/vues/accueil.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Parent root = null;
        try {
            root = (Parent) fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Accueil vue = fxmlLoader.getController();
        laStageUnique.setTitle("Accueil");
        laStageUnique.getScene().setRoot(root);
        laStageUnique.setFullScreenExitHint("");
        laStageUnique.setFullScreen(true);
        laStageUnique.show();
        vue.setMonControleur(controleur);
        return vue;

    }

    public void setMonControleur(Controleur monControleur) {
        this.monControleur = monControleur;
    }

    public void incriptionOk(ActionEvent actionEvent) {
        this.monControleur.goToInscriptionPage();
    }

    public void connexionOk(ActionEvent actionEvent) {
        this.monControleur.goToConnexionPage();
    }

    public void quitterOk(ActionEvent actionEvent) {
        Platform.exit();
    }
}
