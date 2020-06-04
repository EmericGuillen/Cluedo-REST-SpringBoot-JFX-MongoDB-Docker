package vues;

import controleur.Controleur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modele.exceptions.RecupInfoUtilisateurImpossibleException;
import modele.exceptions.SuppressionUtilisateurImpossibleException;

import java.io.IOException;
import java.net.URL;

public class Desabonnement {

    private Controleur monControleur;

    @FXML
    private Label messageErreur;

    @FXML
    private TextField nom;

    @FXML
    private PasswordField mdp;

    public static Desabonnement creerEtAfficher(Controleur c, Stage laStageUnique){
        URL location = Desabonnement.class.getResource("/vues/desabonnement.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Parent root = null;
        try {
            root = (Parent) fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Desabonnement vue = fxmlLoader.getController();
        laStageUnique.setTitle("Désabonnement");
        laStageUnique.getScene().setRoot(root);
        laStageUnique.setFullScreenExitHint("");
        laStageUnique.setFullScreen(true);
        vue.setMonControleur(c);
        laStageUnique.show();
        return vue;
    }

    public void setMonControleur(Controleur monControleur) {
        this.monControleur = monControleur;
    }

    public void retourOk(ActionEvent actionEvent) throws RecupInfoUtilisateurImpossibleException {
        this.monControleur.goToAccueilConnect();
    }


    public void desabonnementOk(ActionEvent actionEvent) throws SuppressionUtilisateurImpossibleException {
        boolean bool = this.monControleur.desabonnement(this.mdp);
        if (bool){
            this.monControleur.retourAccueil();
        }
        else {
            this.messageErreur.setText("Votre mot de passe est incorrect !");
        }

    }
}