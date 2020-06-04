package controleur;

import javafx.collections.ObservableList;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import modele.ClientInterface;
import modele.ClientProxy;
import modele.DTO.*;
import modele.exceptions.*;
import vues.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class Controleur {

    private Stage laStageUnique;
    private UtilisateurDTO utilisateurDTO;
    private PartieDTO partieDTO;
    private Accueil accueil;
    private Plateau plateau;
    private ClientInterface clientProxy;
    private int nbDeplacementAutorise;
    private boolean aLanceDes;

    public Controleur(Stage laStageUnique) {
        this.laStageUnique = laStageUnique;
        this.accueil = Accueil.creerEtAfficher(this,laStageUnique);
        Image image = new Image("/images/wallpaper.jpg");
        laStageUnique.getIcons().add(image);
        clientProxy = ClientProxy.instance;
        clientProxy.setMonControleur(this);
        this.nbDeplacementAutorise = 0;
        this.aLanceDes = false;
        this.utilisateurDTO = new UtilisateurDTO();
        this.partieDTO = new PartieDTO();
    }

    public Stage getLaStageUnique() {
        return laStageUnique;
    }

    public UtilisateurDTO getUtilisateurDTO() {
        return utilisateurDTO;
    }

    public void goToInscriptionPage() {
        Inscription inscription = Inscription.creerEtAfficher(this, laStageUnique);
    }

    public void retourAccueil() {
        accueil = Accueil.retourAccueil(this,laStageUnique);
    }

    public void goToConnexionPage() {
        Connexion connexion = Connexion.creerEtAfficher(this, laStageUnique);
    }

    public void goToPlateau() throws RecupInfoUtilisateurImpossibleException, Exception, ConsulteCartesImpossibleException {
        this.plateau = Plateau.creerEtAfficher(this, laStageUnique);
    }

    public void goToAccueilConnect() throws RecupInfoUtilisateurImpossibleException {
        AccueilConnect accueilConnect = AccueilConnect.creerEtAfficher(this, laStageUnique);
    }

    /**
     * La méthode inscription demande au proxy de réaliser une inscription
     * @param nom
     * @param password
     * @param confirmPassword
     * @return L'idUtil qui correpond à l'id Utilisateur
     */
    public int inscription(TextField nom, PasswordField password, PasswordField confirmPassword) {
        UtilisateurDTO util = new UtilisateurDTO();
        util.setNom(nom.getText());
        util.setPassword(password.getText());
        util.setConfirmPassword(confirmPassword.getText());

        int idUtil = this.clientProxy.inscription(util);
        return idUtil;
    }

    /**
     * La méthode connexion demande au proxy de réaliser une connexion
     * @param nom
     * @param mdp
     * @return Le token qui permettra de réaliser l'authentification au serveur
     * @throws RecupInfoUtilisateurImpossibleException
     */
    public String connexion(TextField nom, PasswordField mdp) throws RecupInfoUtilisateurImpossibleException {
        utilisateurDTO.setNom(nom.getText());
        utilisateurDTO.setPassword(mdp.getText());
        String token = this.clientProxy.connexion(utilisateurDTO);
        utilisateurDTO.setToken(token);
        if (token != null){
            this.userGet();
        }

        return token;
    }

    /**
     * Récupère les infomation concernet l'utilisateur actuel
     * @return
     * @throws RecupInfoUtilisateurImpossibleException
     */
    public UtilisateurDTO userGet() throws RecupInfoUtilisateurImpossibleException {
        String token = this.utilisateurDTO.getToken();
        this.utilisateurDTO = this.clientProxy.userGet(this.utilisateurDTO,this.utilisateurDTO.getToken());
        this.utilisateurDTO.setToken(token);
        return this.utilisateurDTO;
    }

    /**
     * Récupère la partie stocké en local
     * @return
     */
    public PartieDTO partieGet(){
        return this.partieDTO;
    }

    /**
     * Récupère la liste des utilisateurs connectés
     * @return La list des utilisateurs connectés
     * @throws RecuperationUtilisateursImpossibleException
     */
    public List<String> getUtilsConnect() throws RecuperationUtilisateursImpossibleException {
        return this.clientProxy.utilisateursConnect(this.utilisateurDTO.getToken())
                .stream()
                .filter(n->!this.utilisateurDTO.getNom().equals(n))
                .collect(Collectors.toList());
    }

    /**
     * Permet de se déconnecter et de retourner à l'acceuil
     * @throws DeconnexionImpossibleException
     */
    public void deconnexion() throws DeconnexionImpossibleException{
        this.clientProxy.deconnexion(this.utilisateurDTO, this.utilisateurDTO.getToken());
        this.retourAccueil();
    }

    /**
     * Renvoie vers la page permettant de creer une partie
     * @throws RecuperationUtilisateursImpossibleException
     */
    public void goToCreerPartiePage() throws RecuperationUtilisateursImpossibleException {
        CreerPartie creerPartie = CreerPartie.creerEtAfficher(this, laStageUnique);
    }

    /**
     * Peremet de creer une Partie
     * @param selectedItems
     * @return La partie rejoins
     * @throws CreationPartieImpossibleException
     */
    public PartieDTO creerPartie(ObservableList<String> selectedItems) throws CreationPartieImpossibleException {
        PartieDTO partieDTO = new PartieDTO();
        partieDTO.setMdj(utilisateurDTO.getNom());
        partieDTO.setInvites(selectedItems);
        this.partieDTO = this.clientProxy.creerPartie(partieDTO, utilisateurDTO.getToken());
        return this.partieDTO;
    }

    /**
     * Permet de rejoindre une partie
     * @param selectedItem
     * @return
     * @throws RejoindrePartieImpossibleException
     */
    public PartieDTO rejoindrePartie(PartieDTO selectedItem) throws RejoindrePartieImpossibleException {
        this.partieDTO = this.clientProxy.rejoindrePartie(selectedItem.getIdPartie(),utilisateurDTO.getNom(),utilisateurDTO.getToken());
        return this.partieDTO;
    }

    /**
     * Récupère la liste des parties disponibles
     * @return La liste des partie disponible
     * @throws RecuperationPartiesImpossibleException
     */
    public List<PartieDTO> getPartiesDisponibles() throws RecuperationPartiesImpossibleException {
        return this.clientProxy.consulterInvitations(this.utilisateurDTO.getNom(), this.utilisateurDTO.getToken());
    }


    /**
     * Permet de rejoindre une partie
     * @throws RecuperationPartiesImpossibleException
     */
    public void goToRejoindrePartie() throws RecuperationPartiesImpossibleException {
        RejoindrePartie rejoindrePartie = RejoindrePartie.creerEtAfficher(this, laStageUnique);
    }

    /**
     * Redirige vers la page de désabonnement
     */
    public void goToDesabonnementPage() {
        Desabonnement desabonnement = Desabonnement.creerEtAfficher(this, laStageUnique);
    }

    /**
     * Permet de se désabonner
     * @param mdp
     * @return Un boolean pour savoir le désabonnement à eu lieu
     * @throws SuppressionUtilisateurImpossibleException
     */
    public boolean desabonnement(PasswordField mdp) throws SuppressionUtilisateurImpossibleException {
        UtilisateurDTO utildesabo = new UtilisateurDTO();
        utildesabo.setNom(this.utilisateurDTO.getNom());
        utildesabo.setPassword(mdp.getText());
        utildesabo.setConfirmPassword(mdp.getText());
        return this.clientProxy.userDelete(utildesabo, utilisateurDTO.getToken());
    }

    /**
     * Permet d'accepter de sauvegarder la partie
     * @param selectedItem
     * @throws AccepteSauvegardeImpossibleException
     */
    public void accepteSave(String selectedItem) throws AccepteSauvegardeImpossibleException {
        boolean bool = "oui".equals(selectedItem)?true:false;
        this.clientProxy.accepteSauvegarde(this.partieDTO.getIdPartie(),this.utilisateurDTO.getIdUtil(),bool,this.utilisateurDTO.getToken());
    }

    /**
     * Permet de sauvegarder la partie
     * @throws SauvegardeImpossibleException
     */
    public void sauvegardePartie() throws SauvegardeImpossibleException {
        this.clientProxy.sauvegardePATCH(this.partieDTO,this.utilisateurDTO.getToken());
    }

    public PartieDTO restaurePartie(PartieDTO selectedItem) throws RestaurePartieImpossibleException {
        this.partieDTO = this.clientProxy.restaurePartie(selectedItem.getIdPartie(),selectedItem.getMdj(),this.utilisateurDTO.getToken());
        return this.partieDTO;
    }

    public List<PartieDTO> getPartiesSauvegardees() throws DemandeSauvegardeImpossibleException {
        return this.clientProxy.sauvegardeGET(this.utilisateurDTO.getIdUtil(),this.utilisateurDTO.getToken());
    }

    public void goToRestaurerPartie() throws DemandeSauvegardeImpossibleException {
        RestaurerPartie restaurerPartie = RestaurerPartie.creerEtAfficher(this, this.laStageUnique);
    }

    public DesDTO lancerDes() throws LanceDesImpossibleException, PiocheCarteIndiceImpossibleException {
        this.aLanceDes = true;
        DesDTO des = this.clientProxy.lanceDes(this.partieDTO.getIdPartie(),this.utilisateurDTO.getNom(),this.utilisateurDTO.getToken());

        if (des.getDe1() == 1) {
            try {
                this.clientProxy.piocheCarteIndice(this.partieDTO.getIdPartie(), this.utilisateurDTO.getNom(), this.utilisateurDTO.getToken());
            } catch (PiocheCarteIndiceImpossibleException e) {
                e.printStackTrace();
            }
        }

        if (des.getDe2() == 1) {
            try {
                this.clientProxy.piocheCarteIndice(this.partieDTO.getIdPartie(), this.utilisateurDTO.getNom(), this.utilisateurDTO.getToken());
            } catch (PiocheCarteIndiceImpossibleException e) {
                e.printStackTrace();
            }
        }



        return des;
    }

    public PartieDTO lancerPartie() throws LancePartieImpossibleException {
        this.partieDTO = this.clientProxy.lancerPartie(this.partieDTO.getIdPartie(),this.utilisateurDTO.getNom(),this.utilisateurDTO.getToken());
        return this.partieDTO;
    }

    public List<CarteDTO> getCartesJeu() throws ConsulteCartesImpossibleException {
        List<CarteDTO> listCartesJeu = this.clientProxy.consulteCartesJeu(this.partieDTO.getIdPartie(),this.partieDTO.getJoueurs().stream()
                .filter(j -> this.utilisateurDTO.getNom().equals(j.getUtil())).findFirst().get().getUtil(),this.utilisateurDTO.getToken());
        return listCartesJeu;
    }

    public void hypothese(CarteDTO suspect, CarteDTO arme, CarteDTO lieu) throws SuppositionImpossibleException {
        boolean bool = this.clientProxy.suppose(this.partieDTO.getIdPartie(),this.partieDTO.getJoueurs().stream()
                .filter(j -> this.utilisateurDTO.getNom().equals(j.getUtil())).findFirst().get().getUtil(),suspect,arme,lieu,this.utilisateurDTO.getToken());

        if (bool){
            this.partieDTO.setHypotheseCourante(List.of(suspect,arme,lieu));
        }

        /* JoueurDTO joueurAtp = null;

        for(JoueurDTO j : this.partieDTO.getJoueurs()){
            if(j.getPerso().getNom().equals(suspect.getNom())){
                joueurAtp = j;
            }
        }

        if (joueurAtp != null) {
            this.plateau.teleporteDansPiece(lieu.getNom(), joueurAtp);
        }
        */
    }

    public void activeFlux() throws InterruptedException {
        this.clientProxy.recuperationDes(this.utilisateurDTO.getToken());
        this.clientProxy.recuperationSuppositions(this.utilisateurDTO.getToken());
        this.clientProxy.recuperationAccusation(this.utilisateurDTO.getToken());
        this.clientProxy.recuperationLancerPartie(this.utilisateurDTO.getToken());
        this.clientProxy.recuperationRejointPartie(this.utilisateurDTO.getToken());
        this.clientProxy.recuperationDeplacement(this.utilisateurDTO.getToken());
        this.clientProxy.recuperationTermineTour(this.utilisateurDTO.getToken());
        this.clientProxy.recuperationQuitterPartie(this.utilisateurDTO.getToken());
        this.clientProxy.recuperationReveleCarte(this.utilisateurDTO.getToken());
        this.clientProxy.recuperationCarnet(this.utilisateurDTO.getToken());
        this.clientProxy.recuperationPiocheCarteIndice(this.utilisateurDTO.getToken());
        this.clientProxy.recuperationSauvegarde(this.utilisateurDTO.getToken());
        this.clientProxy.recuperationTerminePartie(this.utilisateurDTO.getToken());
    }


    public void majConsolePlateau(FluxDTO flux) {
        if(flux.getDes()!=null){
            this.nbDeplacementAutorise = flux.getDes().getSomme();
        }

        if("attente".equals(this.partieDTO.getStatut()) && "LANCER PARTIE".equals(flux.getAction())){
            if(flux.getPartie()!=null){
                this.partieDTO = flux.getPartie();
                this.plateau.setPions();
            }
        }

        if("attente".equals(this.partieDTO.getStatut()) && "RESTAURE".equals(flux.getAction())){
            if(flux.getPartie()!=null){
                this.partieDTO = flux.getPartie();
                this.plateau.setPions();
            }
        }

        if ("DEPLACE".equals(flux.getAction())){
            JoueurDTO joueurDTO = this.partieDTO.getJoueurs().stream().filter(j -> flux.getLeJoueur().getUtil().equals(j.getUtil())).findFirst().get();
            this.plateau.unsetPion(joueurDTO);
            joueurDTO.setPositionCourante(flux.getLeJoueur().getPositionCourante());
            this.plateau.setPion(joueurDTO);
        }

        if("TERMINE TOUR".equals(flux.getAction())){
            this.partieDTO.setJoueurCourant(flux.getaQuiLeTour().getUtil());
            this.plateau.majAccesBouton(flux);
        }
        if("SUPPOSE".equals(flux.getAction()) && flux.getQuiDoitReveler().getUtil().equals(this.utilisateurDTO.getNom())){
            this.plateau.revelerCartes();
        }
        if ("REVELER".equals(flux.getAction())){
            Optional<String> cNous = flux.getaQuiReveler().stream().map(j -> j.getUtil()).filter(j -> j.equals(this.utilisateurDTO.getNom())).findFirst();
            if(cNous.isPresent()){
                this.plateau.afficheCarteRevele(flux.getCarteRevelee(),flux.getQuiDoitReveler().getUtil());
            }

        }
        if ("CONSULTER_CARNET".equals(flux.getAction()) && this.utilisateurDTO.getNom().equals(flux.getQuiJoue())){
            this.plateau.majCarnet(flux.getCarnet());
        }

        if("PIOCHE_CARTE".equals(flux.getAction())){
            if(flux.getQuiDoitReveler()!=null){
                this.plateau.afficheCarteRevele(flux.getCarteIndiceAreveler(),flux.getQuiDoitReveler().getUtil());
            }else{
                this.plateau.afficheCarteRevele(flux.getCarteIndiceAreveler(),"");
            }

        }

        if ("SAUVEGARDE".equals(flux.getAction()) && !(this.partieGet().getMdj().equals(this.utilisateurDTO.getNom()))){
            this.plateau.sauvegardePartieRetourAccueil();
        }

        if("TERMINE PARTIE".equals(flux.getAction())){
            this.plateau.afficheGagnant(flux.getGagnant());
        }

        if ("en cours".equals(this.partieDTO.getStatut())){
            this.plateau.majAccesBouton(flux);
        }

        this.plateau.majConsolePlateau(flux);

    }

    public void accuse(CarteDTO carteSuspect, CarteDTO carteArme, CarteDTO carteLieu) throws AccusationImpossibleException {
        boolean bool = this.clientProxy.accuse(this.partieDTO.getIdPartie(),this.partieDTO.getJoueurs().stream()
                .filter(j -> this.utilisateurDTO.getNom().equals(j.getUtil())).findFirst().get().getUtil(),carteSuspect,carteArme,carteLieu,this.utilisateurDTO.getToken());

        if (bool){
            this.partieDTO.setHypotheseCourante(List.of(carteSuspect,carteArme,carteLieu));
        }
    }

    public JoueurDTO getJoueur(){
        JoueurDTO joueur = this.partieDTO.getJoueurs().stream().filter(j -> this.utilisateurDTO.getNom().equals(j.getUtil())).findFirst().get();
        return joueur;
    }

    public void setPositionJoueur(JoueurDTO joueur){
        this.getJoueur().setPositionCourante(joueur.getPositionCourante());
    }

    public int getNbDeplacementAutorise() {
        return nbDeplacementAutorise;
    }

    public void seDeplace(int finalX, int finalY, String piece, JoueurDTO joueurAtp){

        PointDTO pointDTO = new PointDTO();
        pointDTO.setX(finalX);
        pointDTO.setY(finalY);

        if(joueurAtp==null){
            this.plateau.unsetPion(this.getJoueur());
            if(piece.isEmpty()){
                this.nbDeplacementAutorise = this.nbDeplacementAutorise-1;
                this.getJoueur().setPiece("");
            }else{
                this.nbDeplacementAutorise = 0;
                this.getJoueur().setPiece(piece);
            }

            JoueurDTO joueurDTO = null;
            try {
                joueurDTO = this.clientProxy.deplacePion(this.partieDTO.getIdPartie(), this.getJoueur().getUtil(), pointDTO, piece, this.utilisateurDTO.getToken());
                this.setPositionJoueur(joueurDTO);
            } catch (DeplacePionImpossibleException e) {
                e.printStackTrace();
            }
        }

        else{
            System.out.println("on tp");
            this.plateau.unsetPion(joueurAtp);
            try {
                this.clientProxy.deplacePion(this.partieDTO.getIdPartie(), joueurAtp.getUtil(), pointDTO, piece, this.utilisateurDTO.getToken());
            } catch (DeplacePionImpossibleException e) {
                e.printStackTrace();
            }
        }







    }

    public void termineTour() throws TermineTourImpossibleException {
        this.aLanceDes = false;
        this.clientProxy.termineTour(this.partieDTO.getIdPartie(),this.getJoueur().getUtil(),this.utilisateurDTO.getToken());
    }

    public boolean quitterPartie() throws QuitterPartieImpossibleException {
        return this.clientProxy.quitterPartie(this.partieDTO.getIdPartie(),this.getJoueur().getUtil(),this.utilisateurDTO.getToken());
    }

    public boolean revelerCarte(String carte, List<String> personne) throws ReveleCarteImpossibleException {
        CarteDTO carteDTO = this.getJoueur().getDeck().stream().filter(c -> carte.equals(c.getNom())).findFirst().get();
        List<JoueurDTO> listJoueur = this.partieDTO.getJoueurs().stream().filter(j -> personne.contains(j.getUtil())).collect(Collectors.toList());

        return this.clientProxy.reveleCarte(this.partieDTO.getIdPartie(),this.partieGet().getJoueurCourant(), carteDTO, listJoueur, this.utilisateurDTO.getToken());
    }

    public void voirCarnet() throws ConsulteCarnetImpossibleException {
        this.clientProxy.consulteCarnet(this.partieDTO.getIdPartie(),this.getJoueur().getUtil(),this.utilisateurDTO.getToken());
    }

    public void remplirCarnet(List<List<CarteDTO>> leCarnet) throws ConsulteCarnetImpossibleException {
        this.clientProxy.remplirCarnet(this.partieDTO.getIdPartie(),this.getJoueur().getUtil(),leCarnet,this.utilisateurDTO.getToken());
    }

    public boolean isaLanceDes() {
        return aLanceDes;
    }

    public void setaLanceDes(boolean aLanceDes) {
        this.aLanceDes = aLanceDes;
    }
}
