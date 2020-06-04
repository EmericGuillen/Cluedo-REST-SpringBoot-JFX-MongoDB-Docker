package vues;

import controleur.Controleur;
import javafx.event .ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modele.exceptions.RecupInfoUtilisateurImpossibleException;

import java.io.IOException;
import java.net.URL;

public class Connexion {
    private Controleur monControleur;

    @FXML
    private Label messageErreur;

    @FXML
    private TextField nom;

    @FXML
    private PasswordField mdp;

    public static Connexion creerEtAfficher(Controleur c, Stage laStageUnique){


        URL location = Connexion.class.getResource("/vues/connexion.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Parent root = null;
        try {
            root = (Parent) fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Connexion vue = fxmlLoader.getController();
        laStageUnique.setTitle("Connexion");
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

    public void connexionOk(ActionEvent actionEvent) throws RecupInfoUtilisateurImpossibleException {
        String token = this.monControleur.connexion(this.nom, this.mdp);
        if (token!=null && !token.isEmpty()){
            this.messageErreur.setText(this.nom.getText() + " est connecté !");
            this.monControleur.goToAccueilConnect();
        }
        else {
            if (token==null){
                this.messageErreur.setText("Utilisateur deja connecté !");
            }
            else{
                this.messageErreur.setText("Utilisateur ou mot de passe incorrect !");
            }

        }


    }
}
