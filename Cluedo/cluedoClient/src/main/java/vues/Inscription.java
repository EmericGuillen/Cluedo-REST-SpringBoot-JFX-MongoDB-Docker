package vues;

import controleur.Controleur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Inscription {
    private Controleur monControleur;
    @FXML
    private TextField nom;

    @FXML
    private PasswordField password;

    @FXML
    private PasswordField confirmPassword;

    @FXML
    private Label messageErreur;

    public static Inscription creerEtAfficher(Controleur c, Stage laStageUnique){


        URL location = Inscription.class.getResource("/vues/inscription.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Parent root = null;
        try {
            root = (Parent) fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Inscription vue = fxmlLoader.getController();
        laStageUnique.setTitle("Inscription");
        laStageUnique.getScene().setRoot(root);
        laStageUnique.setFullScreenExitHint("");
        laStageUnique.setFullScreen(true);
        laStageUnique.show();
        vue.setMonControleur(c);
        return vue;
    }

    public void setMonControleur(Controleur monControleur) {
        this.monControleur = monControleur;
    }

    public void retourOk(ActionEvent actionEvent) {
        this.monControleur.retourAccueil();
    }

    public void incriptionOk(ActionEvent actionEvent) {
        int i = this.monControleur.inscription(this.nom, this.password, this.confirmPassword);
        if (i == -1){
            this.messageErreur.setText("Les mots de passe ne sont pas identiques !");
        }else{
            if (i == -2){
                this.messageErreur.setText(this.nom.getText() + " est déjà pris !");
            }else {
                this.messageErreur.setText(this.nom.getText() + " est bien inscrit !");
            }
        }
    }
}
