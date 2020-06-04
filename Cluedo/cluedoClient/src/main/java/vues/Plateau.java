package vues;

import controleur.Controleur;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import modele.DTO.*;
import modele.exceptions.*;
import modele.fabrique.FabriquePopUp;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Plateau {
    private Controleur monControleur;

    private HashMap<JoueurDTO,TilePane> joueursPlateau;

    private Popup popup;
    private Popup popupSavePartie;
    private Popup popupVoirCartes;
    private Popup popupHypothese;
    private Popup popupReveleCarte;
    private Popup popupAccuse;
    private Popup popupVoirCarnet;
    private Popup popupTerminePartieAccusation;

    private Popup popupAfficheReveleCarte;

    private Popup popupNomSalle = new Popup();
    private BorderPane bpNomSalle = new BorderPane();

    ListView<String> listViewvoirCartes;
    private TableView<CarteDTO> tableViewVoirCarnet;
    private TableColumn<CarteDTO, String> nomCarte = new TableColumn("Nom de la carte");
    private TableColumn<CarteDTO, String> valide = new TableColumn("Etat de la carte");
    private List<List<CarteDTO>> leCarnet;

    private ComboBox lieu = new ComboBox();
    private ComboBox arme = new ComboBox();
    private ComboBox suspect = new ComboBox();

    private ComboBox mesCartes = new ComboBox();
    private ListView<String> listUtilsReveleCarte = new ListView<>();

    private ComboBox lieuAccuse = new ComboBox();
    private ComboBox armeAccuse = new ComboBox();
    private ComboBox suspectAccuse = new ComboBox();

    private BorderPane[][] gridCases = new BorderPane[28][16];

    private LecteurTXT lecteur;

    private HashMap<String,HashMap<String,PointDTO>> emplacementPionDansSalle;
    private HashMap<String,List<PointDTO>> emplacementSortieSalle;
    private HashMap<String,String> passages;

    private Text textAfficheCarte = new Text();
    private Text textTerminePartie = new Text();

    @FXML
    BorderPane bpPlateau;

    @FXML
    Text idPartie;

    @FXML
    TextArea consolePlateau;

    @FXML
    Text nbDeplacementAutorise;

    @FXML
    ListView listeJoueurs;

    @FXML
    Button sauvegarderPartie;

    @FXML
    Button lancerPartie;

    @FXML
    Button buttonHypothese;

    @FXML
    Button buttonAccuse;

    @FXML
    Button buttonLanceDes;

    @FXML
    Button buttonVoirCarnet;

    @FXML
    Button buttonVoirCarte;

    @FXML
    Button buttonPrendPassage;

    @FXML
    Button buttonFinirTour;

    public static Plateau creerEtAfficher(Controleur c, Stage laStageUnique) throws RecupInfoUtilisateurImpossibleException, Exception, ConsulteCartesImpossibleException {

        URL location = Plateau.class.getResource("/vues/plateau.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Parent root = null;
        try {
            root = (Parent) fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Plateau vue = fxmlLoader.getController();
        laStageUnique.setTitle("Plateau");
        laStageUnique.getScene().setRoot(root);
        laStageUnique.getScene().getStylesheets().add(Plateau.class.getResource("/css/plateau.css").toExternalForm());
        laStageUnique.setFullScreenExitHint("");
        laStageUnique.setFullScreen(true);
        vue.setMonControleur(c);

        vue.consolePlateau.setEditable(false);
        //vue.consolePlateau.setMouseTransparent(true);
        vue.consolePlateau.setFocusTraversable(false);
        vue.monControleur.activeFlux();
        vue.joueursPlateau = new HashMap<>();

        GridPane grid = new GridPane();
        URL locationPlateau = Plateau.class.getResource("/plateau/plateau.txt");
        vue.lecteur = new LecteurTXT(locationPlateau.getPath());

        vue.lecteur.createPlateau(vue.gridCases,grid);

        vue.initSalle();

        /** MouseEvent sur chaque case du plateau de jeu pour déplacer son pion **/
        for (int i = 0;i<28; i++){
            int finalX = i;
            for (int j = 0; j<16; j++){
                int finalY = j;
                vue.gridCases[i][j].addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        try {
                            vue.deplacePion(finalX, finalY);
                        } catch (DeplacePionImpossibleException e) {
                            e.printStackTrace();
                        }
                    }
                });

                /** MouseEvent sur chaque case du plateau de jeu pour connaitre le nom d'une salle **/
                if (vue.lecteur.getSolCouleurs().contains(vue.lecteur.lettrePiece(finalY,finalX))){
                    vue.gridCases[i][j].addEventFilter(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            Text text = new Text(vue.lecteur.nomPiece(vue.lecteur.lettrePiece(finalY,finalX)));
                            vue.popupNomSalle.getContent().setAll(text);
                            vue.popupNomSalle.setWidth(10);
                            vue.popupNomSalle.setHeight(10);
                            vue.bpNomSalle.setMinWidth(10);
                            vue.bpNomSalle.setMinHeight(10);
                            vue.bpNomSalle.setCenter(text);
                            vue.popupNomSalle.getContent().add(vue.bpNomSalle);

                            vue.bpNomSalle.getScene().getStylesheets().add(Plateau.class.getResource("/css/plateau.css").toExternalForm());
                            vue.bpNomSalle.getStyleClass().add("bpNomSalle");
                            text.getStyleClass().add("textNomSalle");

                            vue.popupNomSalle.setX(mouseEvent.getSceneX());
                            vue.popupNomSalle.setY(mouseEvent.getSceneY()-20);
                            vue.popupNomSalle.show(vue.monControleur.getLaStageUnique());
                        }
                    });
                    vue.gridCases[i][j].addEventFilter(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            vue.popupNomSalle.hide();
                        }
                    });
                }

            }
        }

        vue.bpPlateau.setCenter(grid);
        vue.bpPlateau.setAlignment(grid,Pos.CENTER);
        grid.setAlignment(Pos.CENTER);


        // Création popup accepte sauvegarde partie
        vue.popup = FabriquePopUp.createAccepteSavePartie(vue.monControleur.getLaStageUnique(),vue);

        // Création popup partie sauvegardée retour vers accueil
        vue.popupSavePartie = FabriquePopUp.createSaveRetourAccueil(vue.monControleur.getLaStageUnique(),vue);

        // Création popup voir cartes
        vue.popupVoirCartes = FabriquePopUp.createVoirCartes(vue.monControleur.getLaStageUnique(),vue);

        // Création popup voir carnet
        vue.popupVoirCarnet = FabriquePopUp.createVoirCarnet(vue.monControleur.getLaStageUnique(),vue);

        // Création popup hypothèse
        vue.popupHypothese = FabriquePopUp.createHypothese(vue.monControleur.getLaStageUnique(),vue);

        // Création popup afficheCarteRevele
        vue.popupAfficheReveleCarte = FabriquePopUp.createAfficheCarteRevele(vue.monControleur.getLaStageUnique(),vue);

        // Création popup révèle carte
        vue.popupReveleCarte = FabriquePopUp.createReveleCarte(vue.monControleur.getLaStageUnique(),vue);

        // Création popup accuser
        vue.popupAccuse = FabriquePopUp.createAccuser(vue.monControleur.getLaStageUnique(),vue);

        // Création popup termine partie accusation
        vue.popupTerminePartieAccusation = FabriquePopUp.createTerminePartieAcusation(vue.monControleur.getLaStageUnique(),vue);


        vue.buttonLanceDes.setVisible(false);
        vue.buttonVoirCarnet.setVisible(false);
        vue.buttonVoirCarte.setVisible(false);
        vue.buttonHypothese.setVisible(false);
        vue.buttonAccuse.setVisible(false);
        vue.buttonPrendPassage.setVisible(false);
        vue.buttonFinirTour.setVisible(false);
        vue.getButtonPrendPassage().setVisible(false);

        vue.majAccesBouton(null);

        if (!vue.monControleur.partieGet().getJoueurs().isEmpty()){
            vue.setPions();
        }

        //si on est maitre du jeu
        if (!(vue.monControleur.userGet().getNom().equals(vue.monControleur.partieGet().getMdj()))){
            vue.sauvegarderPartie.setVisible(false);
            vue.lancerPartie.setVisible(false);
            vue.popup.show(laStageUnique);
        }

        vue.listeJoueurs.setMouseTransparent(true);
        vue.listeJoueurs.setFocusTraversable(false);
        vue.majTextIdPartie(vue.monControleur.partieGet());

        laStageUnique.show();

        return vue;
    }

    private void initSalle() {
        this.emplacementPionDansSalle = new HashMap<>();

        HashMap<String,PointDTO> correspondanceMoutarde = new HashMap<>();
        correspondanceMoutarde.put(this.lecteur.getChambre(),new PointDTO(23,9));
        correspondanceMoutarde.put(this.lecteur.getSalleManger(),new PointDTO(1,7));
        correspondanceMoutarde.put(this.lecteur.getCuisine(),new PointDTO(2,1));
        correspondanceMoutarde.put(this.lecteur.getBureau(),new PointDTO(21,14));
        correspondanceMoutarde.put(this.lecteur.getSalleBillard(),new PointDTO(1,12));
        correspondanceMoutarde.put(this.lecteur.getEntree(),new PointDTO(13,12));
        correspondanceMoutarde.put(this.lecteur.getEscalier(),new PointDTO(12,8));
        correspondanceMoutarde.put(this.lecteur.getSalon(),new PointDTO(22,1));
        correspondanceMoutarde.put(this.lecteur.getSalleReception(),new PointDTO(11,1));
        this.emplacementPionDansSalle.put("Colonel Moutarde",correspondanceMoutarde);




        HashMap<String,PointDTO> correspondanceViolet = new HashMap<>();
        correspondanceViolet.put(this.lecteur.getChambre(),new PointDTO(24,9));
        correspondanceViolet.put(this.lecteur.getSalleManger(),new PointDTO(2,7));
        correspondanceViolet.put(this.lecteur.getCuisine(),new PointDTO(3,1));
        correspondanceViolet.put(this.lecteur.getBureau(),new PointDTO(22,14));
        correspondanceViolet.put(this.lecteur.getSalleBillard(),new PointDTO(2,12));
        correspondanceViolet.put(this.lecteur.getEntree(),new PointDTO(13,13));
        correspondanceViolet.put(this.lecteur.getEscalier(),new PointDTO(13,8));
        correspondanceViolet.put(this.lecteur.getSalon(),new PointDTO(23,1));
        correspondanceViolet.put(this.lecteur.getSalleReception(),new PointDTO(12,1));
        this.emplacementPionDansSalle.put("Professeur Violet",correspondanceViolet);




        HashMap<String,PointDTO> correspondanceOrchidee = new HashMap<>();
        correspondanceOrchidee.put(this.lecteur.getChambre(),new PointDTO(25,9));
        correspondanceOrchidee.put(this.lecteur.getSalleManger(),new PointDTO(3,7));
        correspondanceOrchidee.put(this.lecteur.getCuisine(),new PointDTO(4,1));
        correspondanceOrchidee.put(this.lecteur.getBureau(),new PointDTO(23,14));
        correspondanceOrchidee.put(this.lecteur.getSalleBillard(),new PointDTO(3,12));
        correspondanceOrchidee.put(this.lecteur.getEntree(),new PointDTO(13,14));
        correspondanceOrchidee.put(this.lecteur.getEscalier(),new PointDTO(14,8));
        correspondanceOrchidee.put(this.lecteur.getSalon(),new PointDTO(24,1));
        correspondanceOrchidee.put(this.lecteur.getSalleReception(),new PointDTO(13,1));
        this.emplacementPionDansSalle.put("Docteur Orchidée",correspondanceOrchidee);



        HashMap<String,PointDTO> correspondanceRose = new HashMap<>();
        correspondanceRose.put(this.lecteur.getChambre(),new PointDTO(26,9));
        correspondanceRose.put(this.lecteur.getSalleManger(),new PointDTO(4,7));
        correspondanceRose.put(this.lecteur.getCuisine(),new PointDTO(5,1));
        correspondanceRose.put(this.lecteur.getBureau(),new PointDTO(24,14));
        correspondanceRose.put(this.lecteur.getSalleBillard(),new PointDTO(4,12));
        correspondanceRose.put(this.lecteur.getEntree(),new PointDTO(14,14));
        correspondanceRose.put(this.lecteur.getEscalier(),new PointDTO(15,8));
        correspondanceRose.put(this.lecteur.getSalon(),new PointDTO(25,1));
        correspondanceRose.put(this.lecteur.getSalleReception(),new PointDTO(14,1));
        this.emplacementPionDansSalle.put("Mademoiselle Rose",correspondanceRose);




        HashMap<String,PointDTO> correspondancePervanche = new HashMap<>();
        correspondancePervanche.put(this.lecteur.getChambre(),new PointDTO(23,10));
        correspondancePervanche.put(this.lecteur.getSalleManger(),new PointDTO(1,8));
        correspondancePervanche.put(this.lecteur.getCuisine(),new PointDTO(6,1));
        correspondancePervanche.put(this.lecteur.getBureau(),new PointDTO(25,14));
        correspondancePervanche.put(this.lecteur.getSalleBillard(),new PointDTO(5,12));
        correspondancePervanche.put(this.lecteur.getEntree(),new PointDTO(14,13));
        correspondancePervanche.put(this.lecteur.getEscalier(),new PointDTO(14,9));
        correspondancePervanche.put(this.lecteur.getSalon(),new PointDTO(22,2));
        correspondancePervanche.put(this.lecteur.getSalleReception(),new PointDTO(15,1));
        this.emplacementPionDansSalle.put("Madame Pervenche",correspondancePervanche);




        HashMap<String,PointDTO> correspondanceOlive = new HashMap<>();
        correspondanceOlive.put(this.lecteur.getChambre(),new PointDTO(24,10));
        correspondanceOlive.put(this.lecteur.getSalleManger(),new PointDTO(2,8));
        correspondanceOlive.put(this.lecteur.getCuisine(),new PointDTO(1,2));
        correspondanceOlive.put(this.lecteur.getBureau(),new PointDTO(23,13));
        correspondanceOlive.put(this.lecteur.getSalleBillard(),new PointDTO(7,12));
        correspondanceOlive.put(this.lecteur.getEntree(),new PointDTO(14,12));
        correspondanceOlive.put(this.lecteur.getEscalier(),new PointDTO(13,7));
        correspondanceOlive.put(this.lecteur.getSalon(),new PointDTO(23,2));
        correspondanceOlive.put(this.lecteur.getSalleReception(),new PointDTO(16,1));
        this.emplacementPionDansSalle.put("Monsieur Olive",correspondanceOlive);




        this.emplacementSortieSalle = new HashMap<>();
        this.emplacementSortieSalle.put(this.lecteur.getChambre(),List.of(new PointDTO(24,12)));
        this.emplacementSortieSalle.put(this.lecteur.getSalleManger(),List.of(new PointDTO(6,8)));
        this.emplacementSortieSalle.put(this.lecteur.getCuisine(),List.of(new PointDTO(5,5)));
        this.emplacementSortieSalle.put(this.lecteur.getBureau(),List.of(new PointDTO(23,12)));
        this.emplacementSortieSalle.put(this.lecteur.getSalleBillard(),List.of(new PointDTO(6,10)));
        this.emplacementSortieSalle.put(this.lecteur.getEntree(),List.of(new PointDTO(14,10)));
        this.emplacementSortieSalle.put(this.lecteur.getEscalier(),List.of(new PointDTO(14,10),new PointDTO(13,6)));
        this.emplacementSortieSalle.put(this.lecteur.getSalon(),List.of(new PointDTO(20,5)));
        this.emplacementSortieSalle.put(this.lecteur.getSalleReception(),List.of(new PointDTO(14,6),new PointDTO(13,6)));


        this.passages = new HashMap<>();
        this.passages.put("Salle de billard","Salon");
        this.passages.put("Salon","Salle de billard");
        this.passages.put("Cuisine","Bureau");
        this.passages.put("Bureau","Cuisine");


    }

    private void majMesCartesReveleCarte() {
        List<CarteDTO> dataVoirCartes = this.monControleur.partieGet().getJoueurs().stream()
                .filter(j -> this.monControleur.getUtilisateurDTO().getNom().equals(j.getUtil())).findFirst().get().getDeck();
        this.mesCartes.getItems().setAll(dataVoirCartes.stream().map( carte -> carte.getNom()).collect(Collectors.toList()));
    }

    private void majListUtilsReveleCarte(List<String> invites) {
        this.listUtilsReveleCarte.getItems().setAll(invites);
    }

    public void validerReveleCarte(String carte, List<String> personne) throws ReveleCarteImpossibleException {
        System.out.println("personne : "+personne);
        System.out.println("carte : "+carte);
        this.monControleur.revelerCarte(carte,personne);
    }

    private boolean checkTour(){
        boolean res = false;
        if (this.monControleur.partieGet().getJoueurCourant() != null && !("".equals(this.monControleur.partieGet().getJoueurCourant()))){
            res = this.monControleur.partieGet().getJoueurCourant().equals(this.monControleur.getJoueur().getUtil())?true:false;
        }
        return res;
    }

    private void deplacePion(int finalX, int finalY) throws DeplacePionImpossibleException {

        Platform.runLater(
                () -> {

                    String nomPiece = this.lecteur.nomPiece(this.lecteur.lettrePiece(finalY,finalX));

                    if (this.caseACote(finalX,finalY)
                            && this.monControleur.getNbDeplacementAutorise()>0 && !this.estDansUnePiece()){

                        //(this.estDansUnePiece() && this.sortDePiece(finalX,finalY)) || && this.caseLibre(finalX,finalY)  || this.caseLibre(finalX,finalY)
                        if(this.rentreDansPiece(finalX,finalY)){

                            PointDTO destination = this.emplacementPionDansSalle.get(this.monControleur.getJoueur().getPerso().getNom()).get(nomPiece);
                            this.monControleur.seDeplace(destination.getX(),destination.getY(),nomPiece,null);

                            this.majAccesBouton(null);


                        }else if(this.caseLibre(finalX,finalY)){
                            this.monControleur.seDeplace(finalX,finalY,nomPiece,null);
                        }

                    }


                    if(this.estDansUnePiece() && this.monControleur.getNbDeplacementAutorise()>0){
                        List<PointDTO> sorties = this.emplacementSortieSalle.get(this.monControleur.getJoueur().getPiece());
                        int x = this.monControleur.getJoueur().getPositionCourante().getX();
                        int y = this.monControleur.getJoueur().getPositionCourante().getY();

                        for(PointDTO s:sorties){
                            if(s.getX()==finalX && s.getY()==finalY && this.caseLibre(s.getX(),s.getY())){
                                x = s.getX();
                                y = s.getY();
                                break;
                            }
                        }

                        if(x!=this.monControleur.getJoueur().getPositionCourante().getX() || y!=this.monControleur.getJoueur().getPositionCourante().getY()){

                            this.monControleur.seDeplace(x,y,"",null);
                            this.buttonHypothese.setVisible(false);
                            this.buttonAccuse.setVisible(false);
                            this.buttonPrendPassage.setVisible(false);
                        }

                    }


                });

    }

    public void teleporteDansPiece(String nomPiece, JoueurDTO joueurAtp){
        PointDTO pos = this.emplacementPionDansSalle.get(joueurAtp.getPerso().getNom()).get(nomPiece);
        this.monControleur.seDeplace(pos.getX(),pos.getY(),nomPiece,joueurAtp);
    }

    public void prendrePassage(ActionEvent actionEvent) {
        int x = this.emplacementPionDansSalle.get(this.monControleur.getJoueur().getPerso().getNom()).get(this.passages.get(this.monControleur.getJoueur().getPiece())).getX();
        int y = this.emplacementPionDansSalle.get(this.monControleur.getJoueur().getPerso().getNom()).get(this.passages.get(this.monControleur.getJoueur().getPiece())).getY();
        this.monControleur.seDeplace(x,y,this.passages.get(this.monControleur.getJoueur().getPiece()),null);
        this.buttonPrendPassage.setVisible(false);
    }

    private boolean peuxPrendrePassage(String salle){
        return this.passages.containsKey(salle);
    }

    private boolean caseACote(int x, int y){
        boolean res = false;
        JoueurDTO joueur = this.monControleur.getJoueur();
        int xJ = joueur.getPositionCourante().getX();
        int yJ = joueur.getPositionCourante().getY();
        if ((x == xJ-1 || x == xJ+1) && y == yJ){
            res = true;

        }
        if ((y == yJ-1 || y == yJ+1) && x == xJ){
            res = true;
        }
        return res;
    }

    private boolean caseLibre(int x, int y){
        boolean joueurPresent = this.monControleur.partieGet().getJoueurs()
                .stream()
                .filter(j -> j.getPositionCourante().getX() == x && j.getPositionCourante().getY() == y)
                .findFirst()
                .isPresent();

        boolean caseLibre = this.lecteur.getSol_normal().equals(this.lecteur.getPlateau().get(y).get(x));

        boolean piece = this.rentreDansPiece(x,y);

        return (caseLibre || piece) && !joueurPresent?true:false;
    }

    private boolean rentreDansPiece(int x, int y){
        boolean piece = this.lecteur.getSolCouleurs()
                .contains(this.lecteur.getPlateau().get(y).get(x));
        return piece;
    }

    public boolean sortDePiece(int x, int y){
        boolean piece = this.lecteur.getSolCouleurs()
                .contains(this.lecteur.getPlateau().get(y).get(x));
        return !piece;
    }

    private boolean estDansUnePiece(){
        return this.monControleur.getJoueur().getPiece().isEmpty()?false:true;
    }

    public void validerAccuse(String suspect, String arme, String lieu) throws ConsulteCartesImpossibleException, AccusationImpossibleException {
        CarteDTO carteSuspect = this.monControleur.getCartesJeu().stream().filter(s -> suspect.equals(s.getNom())).findFirst().get();
        CarteDTO carteArme = this.monControleur.getCartesJeu().stream().filter(s -> arme.equals(s.getNom())).findFirst().get();
        CarteDTO carteLieu = this.monControleur.getCartesJeu().stream().filter(s -> lieu.equals(s.getNom())).findFirst().get();
        this.monControleur.accuse(carteSuspect,carteArme,carteLieu);
    }

    public void validerHypothese(String suspect, String arme, String lieu) throws SuppositionImpossibleException, ConsulteCartesImpossibleException {
        CarteDTO carteSuspect = this.monControleur.getCartesJeu().stream().filter(s -> suspect.equals(s.getNom())).findFirst().get();
        CarteDTO carteArme = this.monControleur.getCartesJeu().stream().filter(s -> arme.equals(s.getNom())).findFirst().get();
        CarteDTO carteLieu = this.monControleur.getCartesJeu().stream().filter(s -> lieu.equals(s.getNom())).findFirst().get();
        this.monControleur.hypothese(carteSuspect,carteArme,carteLieu);
    }

    public void majTextIdPartie(PartieDTO partieGet) {
        this.idPartie.setText("Partie numéro "+partieGet.getIdPartie());

        List<String> listJ = new ArrayList<>();
        listJ.add(partieGet.getMdj());
        partieGet.getInvites().forEach(p -> {
            listJ.add(p);
        });

        this.listeJoueurs.getItems().setAll(listJ);

    }

    public void majAccesBouton(FluxDTO flux){
        this.buttonVoirCarnet.setVisible(true);
        this.buttonVoirCarte.setVisible(true);
        if(checkTour()){ //Si c'est nous qui jouons
            if(!this.monControleur.isaLanceDes()){
                this.buttonLanceDes.setVisible(true);
            }
            this.buttonFinirTour.setVisible(true);
            this.nbDeplacementAutorise.setVisible(true);

            if(this.estDansUnePiece()){
                this.buttonHypothese.setVisible(true);

                if(this.lecteur.getEscalier().equals(this.monControleur.getJoueur().getPiece())){
                    this.buttonAccuse.setVisible(true);
                }

                if(flux ==null && this.peuxPrendrePassage(this.monControleur.getJoueur().getPiece())){
                    this.buttonPrendPassage.setVisible(true);
                }
            }

            if(flux!=null){

                if("DEPLACE".equals(flux.getAction()) && flux.isaPrisPassage()){
                    this.buttonLanceDes.setVisible(false);
                    this.buttonHypothese.setVisible(false);
                    this.buttonPrendPassage.setVisible(false);
                }

                if("SUPPOSE".equals(flux.getAction()) || "REVELER".equals(flux.getAction())){
                    this.buttonHypothese.setVisible(false);
                    this.buttonLanceDes.setVisible(false);
                    this.nbDeplacementAutorise.setVisible(false);
                    this.buttonPrendPassage.setVisible(false);
                }

                if("DES".equals(flux.getAction())){
                    this.buttonLanceDes.setVisible(false);
                }
            }


        }else{
            this.buttonLanceDes.setVisible(false);
            this.buttonAccuse.setVisible(false);
            this.buttonHypothese.setVisible(false);
            this.buttonFinirTour.setVisible(false);
            this.nbDeplacementAutorise.setVisible(false);
            this.buttonPrendPassage.setVisible(false);
        }

    }

    public void setMonControleur(Controleur monControleur) {
        this.monControleur = monControleur;
    }

    public void sauvegarderPartieOk(ActionEvent actionEvent) throws RecupInfoUtilisateurImpossibleException, SauvegardeImpossibleException {
        if (this.monControleur.partieGet().getMdj().equals(this.monControleur.userGet().getNom())){
            this.monControleur.sauvegardePartie();
        }

        this.monControleur.goToAccueilConnect();
    }

    public void accepteSaveOk(String selectedItem) throws AccepteSauvegardeImpossibleException {
        this.monControleur.accepteSave(selectedItem);
        this.popup.hide();
    }

    public void lancerDes(ActionEvent actionEvent) throws LanceDesImpossibleException, PiocheCarteIndiceImpossibleException {
        DesDTO de = this.monControleur.lancerDes();
        this.nbDeplacementAutorise.setText(de.getSomme()+"="+de.getDe1()+"+"+de.getDe2());
    }


    public void lancerPartieOk() throws LancePartieImpossibleException, RecupInfoUtilisateurImpossibleException {
        if (this.monControleur.partieGet().getMdj()
                .equals(this.monControleur.userGet().getNom())){
            this.monControleur.lancerPartie();
            this.setPions();
        }
        this.lancerPartie.setVisible(false);
    }


    public void setPions(){

        Platform.runLater(
                () -> {
                    this.monControleur.partieGet().getJoueurs().forEach(j -> {
                        this.joueursPlateau.put(j, new TilePane());
                        switch (j.getPerso().getNom()){
                            case "Mademoiselle Rose":
                                this.joueursPlateau.get(j).setStyle("-fx-background-image:url('/images/pions/pionRose.png')");
                                this.gridCases[j.getPositionCourante().getX()][j.getPositionCourante().getY()].setCenter(this.joueursPlateau.get(j));
                                break;

                            case "Colonel Moutarde":
                                this.joueursPlateau.get(j).setStyle("-fx-background-image:url('/images/pions/pionMoutarde.png')");
                                this.gridCases[j.getPositionCourante().getX()][j.getPositionCourante().getY()].setCenter(this.joueursPlateau.get(j));
                                break;

                            case "Docteur Orchidée":
                                this.joueursPlateau.get(j).setStyle("-fx-background-image:url('/images/pions/pionOrchidee.png')");
                                this.gridCases[j.getPositionCourante().getX()][j.getPositionCourante().getY()].setCenter(this.joueursPlateau.get(j));
                                break;

                            case "Monsieur Olive":
                                this.joueursPlateau.get(j).setStyle("-fx-background-image:url('/images/pions/pionOlive.png')");
                                this.gridCases[j.getPositionCourante().getX()][j.getPositionCourante().getY()].setCenter(this.joueursPlateau.get(j));
                                break;

                            case "Madame Pervenche":
                                this.joueursPlateau.get(j).setStyle("-fx-background-image:url('/images/pions/pionPervenche.png')");
                                this.gridCases[j.getPositionCourante().getX()][j.getPositionCourante().getY()].setCenter(this.joueursPlateau.get(j));
                                break;

                            case "Professeur Violet":
                                this.joueursPlateau.get(j).setStyle("-fx-background-image:url('/images/pions/pionViolet.png')");
                                this.gridCases[j.getPositionCourante().getX()][j.getPositionCourante().getY()].setCenter(this.joueursPlateau.get(j));
                                break;



                        }
                    });
                }
        );


    }

    public void voirCartes(ActionEvent actionEvent) {
        List<CarteDTO> dataVoirCartes = this.monControleur.partieGet().getJoueurs().stream()
                .filter(j -> this.monControleur.getUtilisateurDTO().getNom().equals(j.getUtil())).findFirst().get().getDeck();
        this.listViewvoirCartes.getItems().setAll(dataVoirCartes.stream().map( carte -> carte.getNom()).collect(Collectors.toList()));

        this.popupVoirCartes.show(this.monControleur.getLaStageUnique());
    }

    public void hypothese(ActionEvent actionEvent) throws ConsulteCartesImpossibleException {
        this.lieu.getItems().setAll(this.monControleur.getCartesJeu().stream().filter(carte -> "lieu".equals(carte.getType())).map(carte -> carte.getNom()).collect(Collectors.toList()));
        this.arme.getItems().setAll(this.monControleur.getCartesJeu().stream().filter(carte -> "arme".equals(carte.getType())).map(carte -> carte.getNom()).collect(Collectors.toList()));
        this.suspect.getItems().setAll(this.monControleur.getCartesJeu().stream().filter(carte -> "perso".equals(carte.getType())).map(carte -> carte.getNom()).collect(Collectors.toList()));

        this.popupHypothese.show(this.monControleur.getLaStageUnique());
    }

    public void accuser(ActionEvent actionEvent) throws ConsulteCartesImpossibleException {
        this.lieuAccuse.getItems().setAll(this.monControleur.getCartesJeu().stream().filter(carte -> "lieu".equals(carte.getType())).map(carte -> carte.getNom()).collect(Collectors.toList()));
        this.armeAccuse.getItems().setAll(this.monControleur.getCartesJeu().stream().filter(carte -> "arme".equals(carte.getType())).map(carte -> carte.getNom()).collect(Collectors.toList()));
        this.suspectAccuse.getItems().setAll(this.monControleur.getCartesJeu().stream().filter(carte -> "perso".equals(carte.getType())).map(carte -> carte.getNom()).collect(Collectors.toList()));

        this.popupAccuse.show(this.monControleur.getLaStageUnique());
    }

    public void voirCarnet() throws ConsulteCarnetImpossibleException {
        this.monControleur.voirCarnet();
    }

    public void majCarnet(List<List<CarteDTO>> carnet) {
        Platform.runLater(
                () -> {
                    leCarnet = carnet;
                    List<CarteDTO> allCartes = null;
                    try {
                        allCartes = this.monControleur.getCartesJeu();
                    } catch (ConsulteCartesImpossibleException e) {
                        e.printStackTrace();
                    }
                    List<String> listeEcartee = carnet.get(0).stream().map(c -> c.getNom()).collect(Collectors.toList());
                    List<String> listeDoute = carnet.get(1).stream().map(c -> c.getNom()).collect(Collectors.toList());
                    List<String> listeValidee = carnet.get(2).stream().map(c -> c.getNom()).collect(Collectors.toList());


                    ObservableList<String> lesChoix = FXCollections.observableArrayList("V","?","X");


                    allCartes.forEach(c -> {
                        if (listeEcartee.contains(c.getNom())){
                            c.setChoix("X");
                            this.valide.setCellFactory(ComboBoxTableCell.forTableColumn(lesChoix));
                        }
                        if (listeDoute.contains(c.getNom())){
                            c.setChoix("?");
                            this.valide.setCellFactory(ComboBoxTableCell.forTableColumn(lesChoix));
                        }
                        if (listeValidee.contains(c.getNom())){
                            c.setChoix("V");
                            this.valide.setCellFactory(ComboBoxTableCell.forTableColumn(lesChoix));
                        }
                    });



                    this.tableViewVoirCarnet.setEditable(true);
                    this.tableViewVoirCarnet.getItems().setAll(allCartes);
                    this.popupVoirCarnet.show(this.monControleur.getLaStageUnique());
                }
        );
    }

    public void majConsolePlateau(FluxDTO flux){
        if (!"".equals(flux.toString())) {
            this.consolePlateau.setText(flux+"\n"+this.consolePlateau.getText());
        }
    }

    public void unsetPion(JoueurDTO joueurDTO) {
        Platform.runLater(
                () -> {
                    this.joueursPlateau.putIfAbsent(joueurDTO, new TilePane());
                    this.joueursPlateau.get(joueurDTO).setStyle("");
                }
        );
    }

    public void setPion(JoueurDTO j) {
        Platform.runLater(
                () -> {
                    this.joueursPlateau.putIfAbsent(j, new TilePane());
                    switch (j.getPerso().getNom()){
                        case "Mademoiselle Rose":
                            this.joueursPlateau.get(j).setStyle("-fx-background-image:url('/images/pions/pionRose.png')");
                            this.gridCases[j.getPositionCourante().getX()][j.getPositionCourante().getY()].setCenter(this.joueursPlateau.get(j));
                            break;

                        case "Colonel Moutarde":
                            this.joueursPlateau.get(j).setStyle("-fx-background-image:url('/images/pions/pionMoutarde.png')");
                            this.gridCases[j.getPositionCourante().getX()][j.getPositionCourante().getY()].setCenter(this.joueursPlateau.get(j));
                            break;

                        case "Docteur Orchidée":
                            this.joueursPlateau.get(j).setStyle("-fx-background-image:url('/images/pions/pionOrchidee.png')");
                            this.gridCases[j.getPositionCourante().getX()][j.getPositionCourante().getY()].setCenter(this.joueursPlateau.get(j));
                            break;

                        case "Monsieur Olive":
                            this.joueursPlateau.get(j).setStyle("-fx-background-image:url('/images/pions/pionOlive.png')");
                            this.gridCases[j.getPositionCourante().getX()][j.getPositionCourante().getY()].setCenter(this.joueursPlateau.get(j));
                            break;

                        case "Madame Pervenche":
                            this.joueursPlateau.get(j).setStyle("-fx-background-image:url('/images/pions/pionPervenche.png')");
                            this.gridCases[j.getPositionCourante().getX()][j.getPositionCourante().getY()].setCenter(this.joueursPlateau.get(j));
                            break;

                        case "Professeur Violet":
                            this.joueursPlateau.get(j).setStyle("-fx-background-image:url('/images/pions/pionViolet.png')");
                            this.gridCases[j.getPositionCourante().getX()][j.getPositionCourante().getY()].setCenter(this.joueursPlateau.get(j));
                            break;
                    }

                }
        );
    }

    public void revelerCartes() {
        Platform.runLater(
                () -> {

                    List<String> list = this.monControleur.partieGet().getJoueurs().stream().filter(j -> !(j.getUtil().equals(this.monControleur.getUtilisateurDTO().getNom()))).map(j -> j.getUtil()).collect(Collectors.toList());
                    this.majListUtilsReveleCarte(list);
                    this.majMesCartesReveleCarte();
                    this.popupReveleCarte.show(this.monControleur.getLaStageUnique());

                });
    }

    public void finirSonTour(ActionEvent actionEvent) throws TermineTourImpossibleException {
        this.monControleur.termineTour();
    }

    public void quitterPartieOk(ActionEvent actionEvent) throws QuitterPartieImpossibleException, RecupInfoUtilisateurImpossibleException {
        boolean bool = this.monControleur.quitterPartie();
        if (bool){
            this.monControleur.goToAccueilConnect();
        }
    }

    public void afficheCarteRevele(CarteDTO carteRevelee, String quiJoue) {
        Platform.runLater(
                () -> {
                    String textFinal = "";
                    if(!quiJoue.isEmpty()){
                        textFinal = quiJoue+" vous a montré la carte : "+carteRevelee.getNom();
                    }else{
                        textFinal = "Personne ne détient la carte : "+carteRevelee.getNom();
                    }
                    textAfficheCarte.setText(textFinal);
                    popupAfficheReveleCarte.show(this.monControleur.getLaStageUnique());
                });
    }

    public void sauvegardePartieRetourAccueil() {
        Platform.runLater(
                () -> {
                    this.popupSavePartie.show(this.monControleur.getLaStageUnique());
                });
    }

    public Controleur getMonControleur() {
        return monControleur;
    }

    public List<List<CarteDTO>> getLeCarnet() {
        return this.leCarnet;
    }

    public ListView<String> getListViewvoirCartes() {
        return listViewvoirCartes;
    }

    public void setListViewvoirCartes(ListView<String> listViewvoirCartes) {
        this.listViewvoirCartes = listViewvoirCartes;
    }

    public Popup getPopupVoirCartes() {
        return popupVoirCartes;
    }

    public TableView<CarteDTO> getTableViewVoirCarnet() {
        return tableViewVoirCarnet;
    }

    public void setTableViewVoirCarnet(TableView<CarteDTO> tableViewVoirCarnet) {
        this.tableViewVoirCarnet = tableViewVoirCarnet;
    }

    public HashMap<JoueurDTO, TilePane> getJoueursPlateau() {
        return joueursPlateau;
    }

    public TableColumn<CarteDTO, String> getNomCarte() {
        return nomCarte;
    }


    public TableColumn<CarteDTO, String> getValide() {
        return valide;
    }

    public ComboBox getArme() {
        return arme;
    }

    public ComboBox getSuspect() {
        return suspect;
    }

    public ComboBox getMesCartes() {
        return mesCartes;
    }


    public ListView<String> getListUtilsReveleCarte() {
        return listUtilsReveleCarte;
    }

    public ComboBox getLieuAccuse() {
        return lieuAccuse;
    }


    public ComboBox getArmeAccuse() {
        return armeAccuse;
    }


    public ComboBox getSuspectAccuse() {
        return suspectAccuse;
    }


    public BorderPane[][] getGridCases() {
        return gridCases;
    }

    public Text getTextAfficheCarte() {
        return textAfficheCarte;
    }

    public Button getButtonPrendPassage() {
        return buttonPrendPassage;
    }

    public Text getTextTerminePartie() {
        return textTerminePartie;
    }

    public void afficheGagnant(JoueurDTO gagnant) {
        Platform.runLater(
                () -> {
                    this.textTerminePartie.setText(gagnant.getUtil()+" a gagné la partie !");
                    this.popupTerminePartieAccusation.show(this.monControleur.getLaStageUnique());
                });

    }
}
