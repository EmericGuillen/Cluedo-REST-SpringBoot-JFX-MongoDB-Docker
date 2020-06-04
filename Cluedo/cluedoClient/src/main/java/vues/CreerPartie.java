package vues;

import controleur.Controleur;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import modele.DTO.PartieDTO;
import modele.exceptions.ConsulteCartesImpossibleException;
import modele.exceptions.CreationPartieImpossibleException;
import modele.exceptions.RecupInfoUtilisateurImpossibleException;
import modele.exceptions.RecuperationUtilisateursImpossibleException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class CreerPartie {
    private Controleur monControleur;

    @FXML
    private Label messageErreur;

    @FXML
    private GridPane gridPane;

    @FXML
    private ListView<String> list;


    public static CreerPartie creerEtAfficher(Controleur c, Stage laStageUnique) throws RecuperationUtilisateursImpossibleException {
        URL location = CreerPartie.class.getResource("/vues/creerPartie.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Parent root = null;
        try {
            root = (Parent) fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        CreerPartie vue = fxmlLoader.getController();
        laStageUnique.setTitle("Accueil connecté");
        laStageUnique.getScene().setRoot(root);
        laStageUnique.setFullScreenExitHint("");
        laStageUnique.setFullScreen(true);
        vue.setMonControleur(c);
        //vue.setPseudo();
        vue.list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        vue.majListUtilsConnect(vue.monControleur.getUtilsConnect());

        laStageUnique.show();
        return vue;
    }

    public void setMonControleur(Controleur monControleur) {
        this.monControleur = monControleur;
    }

    public void retourOk(ActionEvent actionEvent) throws RecupInfoUtilisateurImpossibleException {
        this.monControleur.goToAccueilConnect();
    }

    public void creationOk(ActionEvent actionEvent) throws CreationPartieImpossibleException, RecupInfoUtilisateurImpossibleException, Exception, ConsulteCartesImpossibleException {
        PartieDTO partie = this.monControleur.creerPartie(list.getSelectionModel().getSelectedItems());
        if (partie.getIdPartie()!=null){
            this.monControleur.goToPlateau();
        }
        else {
            this.messageErreur.setText("Une partie ne peut accueillir qu'entre 3 et 6 personnes !");
        }
    }

    public void majListUtilsConnect(List<String> listUtils){
        this.list.getItems().setAll(listUtils);
    }
}
