package vues;

import controleur.Controleur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import modele.DTO.PartieDTO;
import modele.exceptions.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class RestaurerPartie {
    private Controleur monControleur;

    @FXML
    private Label messageErreur;

    @FXML
    private GridPane gridPane;

    @FXML
    private ListView<PartieDTO> list;


    public static RestaurerPartie creerEtAfficher(Controleur c, Stage laStageUnique) throws DemandeSauvegardeImpossibleException {
        URL location = RestaurerPartie.class.getResource("/vues/restaurerPartie.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Parent root = null;
        try {
            root = (Parent) fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        RestaurerPartie vue = fxmlLoader.getController();
        laStageUnique.setTitle("Restaurer partie");
        laStageUnique.getScene().setRoot(root);
        laStageUnique.setFullScreenExitHint("");
        laStageUnique.setFullScreen(true);
        vue.setMonControleur(c);

        vue.majListPartie(vue.monControleur.getPartiesSauvegardees());

        laStageUnique.show();
        return vue;
    }

    public void setMonControleur(Controleur monControleur) {
        this.monControleur = monControleur;
    }

    public void retourOk(ActionEvent actionEvent) throws RecupInfoUtilisateurImpossibleException {
        this.monControleur.goToAccueilConnect();
    }

    public void restaureOk(ActionEvent actionEvent) throws RecupInfoUtilisateurImpossibleException, RestaurePartieImpossibleException, Exception, ConsulteCartesImpossibleException {
        PartieDTO testList = list.getSelectionModel().getSelectedItem();
        if (testList != null){
            this.monControleur.restaurePartie(list.getSelectionModel().getSelectedItem());
            this.monControleur.goToPlateau();
        }
        else {
            this.messageErreur.setText("Veuillez selectionner une partie à restaurer !");
        }
    }

    public void majListPartie(List<PartieDTO> listUtils){
        this.list.getItems().setAll(listUtils);
    }
}