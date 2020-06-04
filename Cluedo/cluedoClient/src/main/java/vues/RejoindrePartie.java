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
import modele.exceptions.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class RejoindrePartie {
    private Controleur monControleur;

    @FXML
    private Label messageErreur;

    @FXML
    private GridPane gridPane;

    @FXML
    private ListView<PartieDTO> list;


    public static RejoindrePartie creerEtAfficher(Controleur c, Stage laStageUnique) throws RecuperationPartiesImpossibleException {
        URL location = RejoindrePartie.class.getResource("/vues/rejoindrePartie.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Parent root = null;
        try {
            root = (Parent) fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        RejoindrePartie vue = fxmlLoader.getController();
        laStageUnique.setTitle("Rejoindre partie");
        laStageUnique.getScene().setRoot(root);
        laStageUnique.setFullScreenExitHint("");
        laStageUnique.setFullScreen(true);
        vue.setMonControleur(c);

        vue.majListPartie(vue.monControleur.getPartiesDisponibles());

        laStageUnique.show();
        return vue;
    }

    public void setMonControleur(Controleur monControleur) {
        this.monControleur = monControleur;
    }

    public void retourOk(ActionEvent actionEvent) throws RecupInfoUtilisateurImpossibleException {
        this.monControleur.goToAccueilConnect();
    }

    public void rejointOk(ActionEvent actionEvent) throws RejoindrePartieImpossibleException, RecupInfoUtilisateurImpossibleException, Exception, ConsulteCartesImpossibleException {
        PartieDTO testList = list.getSelectionModel().getSelectedItem();
        if (testList != null){
            this.monControleur.rejoindrePartie(list.getSelectionModel().getSelectedItem());
            this.monControleur.goToPlateau();
        }
        else {
            this.messageErreur.setText("Veuillez selectionner une partie à rejoindre !");
        }
    }

    public void majListPartie(List<PartieDTO> listUtils){
        this.list.getItems().setAll(listUtils);
    }
}
