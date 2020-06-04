package modele.fabrique;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import modele.DTO.CarteDTO;
import modele.exceptions.*;
import vues.Plateau;

import java.util.List;
import java.util.stream.Collectors;

public class FabriquePopUp{

    public static Popup createAccepteSavePartie(Stage laStageUnique,Plateau plateau) {
        Popup popup = new Popup();
        popup.centerOnScreen();

        ListView<String> ouiNon = new ListView<>();
        List<String> listOuiNon = List.of("oui","non");
        ouiNon.getItems().setAll(listOuiNon);


        Button buttonSave = new Button("VALIDER");
        buttonSave.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                try {
                    plateau.accepteSaveOk(ouiNon.getSelectionModel().getSelectedItem());
                } catch (AccepteSauvegardeImpossibleException e1) {
                    e1.printStackTrace();
                }
            }
        });

        Text textSave = new Text("Acceptez vous la sauvegarde de la partie ?");

        BorderPane borderPane = new BorderPane();
        borderPane.setMinSize(laStageUnique.getScene().getWidth(),laStageUnique.getScene().getHeight());
        BorderPane borderPaneBis = new BorderPane();
        borderPane.setCenter(borderPaneBis);
        borderPaneBis.setTop(textSave);
        borderPaneBis.setCenter(ouiNon);
        borderPaneBis.setBottom(buttonSave);


        ouiNon.getItems().remove(3,ouiNon.getItems().size());
        ouiNon.setMaxWidth(100);
        ouiNon.setMaxHeight(70);

        borderPaneBis.setMinSize(700,300);
        borderPaneBis.setMaxSize(700,300);
        borderPaneBis.setAlignment(textSave, Pos.CENTER);
        borderPaneBis.setAlignment(ouiNon, Pos.CENTER);
        borderPaneBis.setAlignment(buttonSave, Pos.CENTER);


        borderPane.getStyleClass().add("bpPopup");
        borderPaneBis.getStyleClass().add("bpPopupBis");
        textSave.getStyleClass().add("FormTitre");
        ouiNon.getStyleClass().add("ouiNon");
        ouiNon.getStylesheets().add(Plateau.class.getResource("/css/popup.css").toExternalForm());
        buttonSave.getStyleClass().add("btnForm");


        popup.getContent().add(borderPane);
        popup.centerOnScreen();

        return popup;
    }

    public static Popup createSaveRetourAccueil(Stage laStageUnique,Plateau plateau) {
        Popup popup = new Popup();
        popup.centerOnScreen();

        Button buttonInfoSave = new Button("OK");
        buttonInfoSave.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                try {
                    plateau.getMonControleur().goToAccueilConnect();
                    popup.hide();
                } catch (RecupInfoUtilisateurImpossibleException e1) {
                    e1.printStackTrace();
                }
            }
        });

        Text textInfoSave = new Text("La partie a été sauvegardée ! \n" +
                "Vous allez être redirigé vers l'accueil !");

        BorderPane borderPaneInfoSave = new BorderPane();
        borderPaneInfoSave.setMinSize(laStageUnique.getScene().getWidth(),laStageUnique.getScene().getHeight());
        BorderPane borderPaneInfoSaveBis = new BorderPane();
        borderPaneInfoSave.setCenter(borderPaneInfoSaveBis);
        borderPaneInfoSaveBis.setCenter(textInfoSave);
        borderPaneInfoSaveBis.setBottom(buttonInfoSave);


        borderPaneInfoSaveBis.setMinSize(700,300);
        borderPaneInfoSaveBis.setMaxSize(700,300);
        borderPaneInfoSaveBis.setAlignment(textInfoSave, Pos.CENTER);
        borderPaneInfoSaveBis.setAlignment(buttonInfoSave, Pos.CENTER);


        borderPaneInfoSave.getStyleClass().add("bpPopup");
        borderPaneInfoSaveBis.getStyleClass().add("bpPopupBis");
        textInfoSave.getStyleClass().add("FormTitre");
        buttonInfoSave.getStyleClass().add("btnForm");


        popup.getContent().add(borderPaneInfoSave);
        popup.centerOnScreen();
        return popup;
    }

    public static Popup createVoirCartes(Stage laStageUnique,Plateau plateau) {
        Popup popup = new Popup();
        popup.centerOnScreen();
        BorderPane bpVoirCartes = new BorderPane();
        Text cartesEnMain = new Text();
        cartesEnMain.setText("Cartes en main");
        Button retourPlateauVoirCarte = new Button();
        retourPlateauVoirCarte.setText("RETOUR");
        plateau.setListViewvoirCartes(new ListView<>());

        bpVoirCartes.setTop(cartesEnMain);
        bpVoirCartes.setCenter(plateau.getListViewvoirCartes());
        bpVoirCartes.setBottom(retourPlateauVoirCarte);
        retourPlateauVoirCarte.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                plateau.getPopupVoirCartes().hide();
            }
        });
        popup.getContent().add(bpVoirCartes);
        return popup;
    }

    public static Popup createVoirCarnet(Stage laStageUnique,Plateau plateau) {
        Popup popupVoirCarnet = new Popup();
        popupVoirCarnet.centerOnScreen();
        BorderPane bpVoirCarnet = new BorderPane();
        Text carnet = new Text();
        carnet.setText("Carnet");
        Button retourPlateauVoirCarnet = new Button();
        retourPlateauVoirCarnet.setText("RETOUR");
        plateau.setTableViewVoirCarnet(new TableView<>());


        plateau.getTableViewVoirCarnet().setRowFactory(tv -> {
            TableRow<CarteDTO> row = new TableRow<>();
            return row;
        });


        plateau.getNomCarte().setCellValueFactory(
                new PropertyValueFactory<CarteDTO, String>("nom")
        );

        plateau.getValide().setCellValueFactory(
                new PropertyValueFactory<CarteDTO, String>("choix")
        );

        plateau.getValide().setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<CarteDTO, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<CarteDTO, String> t) {
                        ((CarteDTO) t.getTableView().getItems().get(t.getTablePosition().getRow())).setChoix(t.getNewValue());
                        System.out.println(t.getNewValue());
                        System.out.println(t.getTableView().getItems().get(t.getTablePosition().getRow()));
                        System.out.println(t.getTablePosition().getRow());

                        String maValeurModif = t.getNewValue();
                        CarteDTO maCarteModif = t.getTableView().getItems().get(t.getTablePosition().getRow());

                        List<CarteDTO> listeEcarteeBis = plateau.getLeCarnet().get(0);
                        List<CarteDTO> listeDouteBis = plateau.getLeCarnet().get(1);
                        List<CarteDTO> listeValideeBis = plateau.getLeCarnet().get(2);



                        if (listeEcarteeBis.stream().map(c -> c.getNom()).collect(Collectors.toList()).contains(maCarteModif.getNom())){
                            listeEcarteeBis.remove(listeEcarteeBis.stream().map(c -> c.getNom()).collect(Collectors.toList()).indexOf(maCarteModif.getNom()));
                        }
                        if (listeDouteBis.stream().map(c -> c.getNom()).collect(Collectors.toList()).contains(maCarteModif.getNom())){
                            listeDouteBis.remove(listeDouteBis.stream().map(c -> c.getNom()).collect(Collectors.toList()).indexOf(maCarteModif.getNom()));
                        }
                        if (listeValideeBis.stream().map(c -> c.getNom()).collect(Collectors.toList()).contains(maCarteModif.getNom())){
                            listeValideeBis.remove(listeValideeBis.stream().map(c -> c.getNom()).collect(Collectors.toList()).indexOf(maCarteModif.getNom()));
                        }
                        if ("V".equals(maValeurModif)){
                            listeValideeBis.add(maCarteModif);
                        }
                        if ("X".equals(maValeurModif)){
                            listeEcarteeBis.add(maCarteModif);
                        }
                        if ("?".equals(maValeurModif)){
                            listeDouteBis.add(maCarteModif);
                        }

                        try {
                            plateau.getMonControleur().remplirCarnet(plateau.getLeCarnet());
                        } catch (ConsulteCarnetImpossibleException e) {
                            e.printStackTrace();
                        }
                    };
                }
        );

        plateau.getTableViewVoirCarnet().getColumns().addAll(plateau.getNomCarte(),plateau.getValide());


        bpVoirCarnet.setTop(carnet);
        bpVoirCarnet.setCenter(plateau.getTableViewVoirCarnet());
        bpVoirCarnet.setBottom(retourPlateauVoirCarnet);
        retourPlateauVoirCarnet.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                popupVoirCarnet.hide();
            }
        });
        popupVoirCarnet.getContent().add(bpVoirCarnet);
        return popupVoirCarnet;
    }

    public static Popup createHypothese(Stage laStageUnique,Plateau plateau) {
        Popup popupHypothese = new Popup();
        popupHypothese.centerOnScreen();
        BorderPane bpHypothese = new BorderPane();
        Text erreurHypothese = new Text("");
        Text hypothese = new Text();
        hypothese.setText("Hypothèse");
        Button validerHypothese = new Button();
        validerHypothese.setText("VALIDER");
        Button retourHypothese = new Button();
        retourHypothese.setText("ANNULER");

        GridPane gridPaneHypothese = new GridPane();
        gridPaneHypothese.addRow(1,erreurHypothese);
        gridPaneHypothese.addRow(2,plateau.getSuspect());
        gridPaneHypothese.addRow(3,plateau.getArme());

        GridPane gridPaneHypotheseButtons = new GridPane();
        gridPaneHypotheseButtons.addColumn(1,validerHypothese);
        gridPaneHypotheseButtons.addColumn(2,retourHypothese);

        bpHypothese.getStyleClass().add("bpPopup");
        bpHypothese.getStyleClass().add("bpPopupBis");
        hypothese.getStyleClass().add("FormTitre");
        gridPaneHypothese.setAlignment(Pos.CENTER);

        bpHypothese.setTop(hypothese);
        bpHypothese.setCenter(gridPaneHypothese);
        bpHypothese.setBottom(gridPaneHypotheseButtons);

        popupHypothese.getContent().add(bpHypothese);
        validerHypothese.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                try {
                    if(!plateau.getSuspect().getSelectionModel().isEmpty() && !plateau.getArme().getSelectionModel().isEmpty()){
                        plateau.validerHypothese(String.valueOf(plateau.getSuspect().getSelectionModel().getSelectedItem()),
                                String.valueOf(plateau.getArme().getSelectionModel().getSelectedItem()),
                                plateau.getMonControleur().getJoueur().getPiece());
                        popupHypothese.hide();
                    }else{
                        erreurHypothese.setText("Vous devez selectionner tous les champs !");
                    }

                } catch (SuppositionImpossibleException e1) {
                    e1.printStackTrace();
                } catch (ConsulteCartesImpossibleException e1) {
                    e1.printStackTrace();
                }

            }
        });

        retourHypothese.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                popupHypothese.hide();
            }
        });
        return popupHypothese;
    }

    public static Popup createAfficheCarteRevele(Stage laStageUnique,Plateau plateau) {
        Popup popupAfficheReveleCarte = new Popup();
        popupAfficheReveleCarte.centerOnScreen();
        BorderPane bpAfficheReveleCarte = new BorderPane();
        bpAfficheReveleCarte.setMinWidth(300);
        bpAfficheReveleCarte.setMinHeight(200);

        Button retourAfficheCarteRevele = new Button();
        retourAfficheCarteRevele.setText("OK");

        bpAfficheReveleCarte.getStyleClass().add("bpPopup");
        bpAfficheReveleCarte.getStyleClass().add("bpPopupBis");

        bpAfficheReveleCarte.setCenter(plateau.getTextAfficheCarte());
        bpAfficheReveleCarte.setBottom(retourAfficheCarteRevele);

        popupAfficheReveleCarte.getContent().add(bpAfficheReveleCarte);

        retourAfficheCarteRevele.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                popupAfficheReveleCarte.hide();
            }
        });
        return popupAfficheReveleCarte;
    }

    public static Popup createReveleCarte(Stage laStageUnique,Plateau plateau) {
        Popup popupReveleCarte = new Popup();
        popupReveleCarte.centerOnScreen();
        BorderPane bpReveleCarte = new BorderPane();
        Text erreurReveleCarte = new Text("");
        Text reveleCarte = new Text();
        reveleCarte.setText("Révèle carte");
        Button validerReveleCarte = new Button();
        validerReveleCarte.setText("VALIDER");

        GridPane gridPaneReveleCarte = new GridPane();
        plateau.getListUtilsReveleCarte().getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        bpReveleCarte.getStyleClass().add("bpPopup");
        bpReveleCarte.getStyleClass().add("bpPopupBis");
        reveleCarte.getStyleClass().add("FormTitre");
        plateau.getListUtilsReveleCarte().getStylesheets().add(Plateau.class.getResource("/css/popup.css").toExternalForm());

        plateau.getListUtilsReveleCarte().getItems().remove(6,plateau.getListUtilsReveleCarte().getItems().size());
        plateau.getListUtilsReveleCarte().setMaxWidth(200);
        plateau.getListUtilsReveleCarte().setMaxHeight(160);

        gridPaneReveleCarte.addRow(1,erreurReveleCarte);
        gridPaneReveleCarte.addRow(2,plateau.getMesCartes());
        gridPaneReveleCarte.addRow(3,plateau.getListUtilsReveleCarte());

        gridPaneReveleCarte.setAlignment(Pos.CENTER);

        validerReveleCarte.setAlignment(Pos.CENTER);


        bpReveleCarte.setTop(reveleCarte);
        bpReveleCarte.setCenter(gridPaneReveleCarte);
        bpReveleCarte.setBottom(validerReveleCarte);

        bpReveleCarte.setAlignment(reveleCarte, Pos.CENTER);
        bpReveleCarte.setAlignment(gridPaneReveleCarte, Pos.CENTER);
        bpReveleCarte.setAlignment(validerReveleCarte, Pos.CENTER);

        popupReveleCarte.getContent().add(bpReveleCarte);
        validerReveleCarte.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                try {
                    if(!plateau.getMesCartes().getSelectionModel().isEmpty() && !plateau.getListUtilsReveleCarte().getSelectionModel().isEmpty()){
                        plateau.validerReveleCarte(String.valueOf(plateau.getMesCartes().getSelectionModel().getSelectedItem()),
                                plateau.getListUtilsReveleCarte().getSelectionModel().getSelectedItems());
                        popupReveleCarte.hide();
                    }else{
                        erreurReveleCarte.setText("Vous devez selectionner une carte ainsi que la personne à qui la révéler !");
                    }

                } catch (ReveleCarteImpossibleException e1) {
                    e1.printStackTrace();
                }
            }
        });
        return popupReveleCarte;
    }

    public static Popup createAccuser(Stage laStageUnique,Plateau plateau) {
        Popup popupAccuse = new Popup();
        popupAccuse.centerOnScreen();
        BorderPane bpAccuse = new BorderPane();
        Text accuse = new Text();
        Text erreurAccuser = new Text("");
        accuse.setText("Accuser");
        Button validerAccuse = new Button();
        validerAccuse.setText("VALIDER");
        Button retourAccuse = new Button();
        retourAccuse.setText("ANNULER");

        GridPane gridPaneAccuse = new GridPane();
        gridPaneAccuse.addRow(1,erreurAccuser);
        gridPaneAccuse.addRow(2,plateau.getSuspectAccuse());
        gridPaneAccuse.addRow(3,plateau.getArmeAccuse());
        gridPaneAccuse.addRow(4,plateau.getLieuAccuse());


        GridPane gridPaneAccuseButtons = new GridPane();
        gridPaneAccuseButtons.addColumn(1,validerAccuse);
        gridPaneAccuseButtons.addColumn(2,retourAccuse);

        bpAccuse.getStyleClass().add("bpPopup");
        bpAccuse.getStyleClass().add("bpPopupBis");
        accuse.getStyleClass().add("FormTitre");
        gridPaneAccuse.setAlignment(Pos.CENTER);

        bpAccuse.setTop(accuse);
        bpAccuse.setCenter(gridPaneAccuse);
        bpAccuse.setBottom(gridPaneAccuseButtons);

        popupAccuse.getContent().add(bpAccuse);
        validerAccuse.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                try {
                    if(!plateau.getSuspectAccuse().getSelectionModel().isEmpty() && !plateau.getArmeAccuse().getSelectionModel().isEmpty()){
                        plateau.validerAccuse(String.valueOf(plateau.getSuspectAccuse().getSelectionModel().getSelectedItem()),
                                String.valueOf(plateau.getArmeAccuse().getSelectionModel().getSelectedItem()),
                                String.valueOf(plateau.getLieuAccuse().getSelectionModel().getSelectedItem()));
                        popupAccuse.hide();
                    }else{
                        erreurAccuser.setText("Vous devez selectionner tous les champs !");
                    }

                } catch (ConsulteCartesImpossibleException e1) {
                    e1.printStackTrace();
                } catch (AccusationImpossibleException e1) {
                    e1.printStackTrace();
                }

            }
        });

        retourAccuse.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                popupAccuse.hide();
            }
        });


        return popupAccuse;
    }


    public static Popup createTerminePartieAcusation(Stage laStageUnique,Plateau plateau) {
        Popup popup = new Popup();
        popup.centerOnScreen();

        Button buttonTerminePartie = new Button("OK");
        buttonTerminePartie.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                try {
                    plateau.getMonControleur().goToAccueilConnect();
                    popup.hide();
                } catch (RecupInfoUtilisateurImpossibleException e1) {
                    e1.printStackTrace();
                }
            }
        });

        Text textTerminePartie = plateau.getTextTerminePartie();

        BorderPane borderPaneTerminePartie = new BorderPane();
        borderPaneTerminePartie.setMinSize(laStageUnique.getScene().getWidth(),laStageUnique.getScene().getHeight());
        BorderPane borderPaneTerminePartieBis = new BorderPane();
        borderPaneTerminePartie.setCenter(borderPaneTerminePartieBis);
        borderPaneTerminePartieBis.setCenter(textTerminePartie);
        borderPaneTerminePartieBis.setBottom(buttonTerminePartie);


        borderPaneTerminePartieBis.setMinSize(700,300);
        borderPaneTerminePartieBis.setMaxSize(700,300);
        borderPaneTerminePartieBis.setAlignment(textTerminePartie, Pos.CENTER);
        borderPaneTerminePartieBis.setAlignment(buttonTerminePartie, Pos.CENTER);


        borderPaneTerminePartie.getStyleClass().add("bpPopup");
        borderPaneTerminePartieBis.getStyleClass().add("bpPopupBis");
        textTerminePartie.getStyleClass().add("FormTitre");
        buttonTerminePartie.getStyleClass().add("btnForm");


        popup.getContent().add(borderPaneTerminePartie);
        popup.centerOnScreen();
        return popup;
    }


}
