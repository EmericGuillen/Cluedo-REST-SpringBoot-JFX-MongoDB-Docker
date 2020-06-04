package cluedo.controleur;

import cluedo.DTO.*;
import cluedo.exceptions.*;
import cluedo.jwtTokens.FabriqueTokensG;
import cluedo.jwtTokens.TokensG;
import cluedo.modele.*;
import cluedo.repository.JoueurRepository;
import cluedo.repository.PartieRepository;
import cluedo.repository.UtilisateurRepository;
import cluedo.services.Facade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ReplayProcessor;

import java.net.URI;
import java.util.*;


@RestController
@RequestMapping("/cluedo")

public class CluedoControl {

    /*
     * ATTRIBUTS
     */
    @Autowired
    UtilisateurRepository utilRepository;
    @Autowired
    PartieRepository partieRepository;
    JoueurRepository joueurRepository;
    Facade facade = new Facade();
    public static final TokensG tokenGenerator = FabriqueTokensG.getInstance().getTokensGenerators();
    public static ArrayList<String> userConnect = new ArrayList<String>();
    HashMap<String, HashMap<String,Boolean>> reponseSave = new HashMap<String,HashMap<String,Boolean>>();


    /*
     * METHODES
     */
    @GetMapping(value="/test")
    public ArrayList<Utilisateur> test() {
        ArrayList<Utilisateur> utils = utilRepository.findAll();
        return utils;

    }



    @PostMapping(value="/inscription")
    public ResponseEntity<String> inscription(@RequestBody UtilisateurDTO util) {

        int uid=facade.inscription(util.getNom(),util.getPassword(),util.getConfirmPassword(), utilRepository);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(uid).toUri();

        return ResponseEntity.created(location).build();

    }

    /**
     * Méthode qui permet de se connecter
     * @param util est un utilisateurDTO
     * @return une ResponseEntity.ok avec un token dans le header.
     *         une ResponseEntity.badRequest si il manque des champs.
     *         une ResponseEntity.status(HttpStatus.UNAUTHORIZED) si il est déjà connecté.
     *         une ResponseEntity.status(HttpStatus.NOT_FOUND) si le password est invalide et si
     *         l'tuilisateur n'est pas en bd.
     */

    @PostMapping(value = "/connexion")
    public ResponseEntity<String> connexion(@RequestBody UtilisateurDTO util) {

        try {
            return ResponseEntity.ok()
                    .header(TokensG.AUTH_TOKEN_NAME, facade.connexion(util, utilRepository))
                    .header(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, TokensG.AUTH_TOKEN_NAME)
                    .build();
        }

        catch (ChampInvalideException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        catch (UtilisateurDejaConnecte utilisateurDejaConnecte) {
            utilisateurDejaConnecte.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        catch (MauvaisPasswordException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        catch (UtilisateurNonExistantException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Permet à un utilisateur de se déconnecter
     * @param login est l'utilisateur à déconnecté
     * @param authorization Ce string est le token de l'utilisateur qui émet la requete
     * @return  ResponseEntity.status(HttpStatus.OK) si l'utilisateur est le bon il est déconnecté
     *          ResponseEntity.status(HttpStatus.UNAUTHORIZED) si un petit malin essaye de déconnecter
     *          quelqu'un d'autre.
     */

    @GetMapping(value="/deconnexion")
    public ResponseEntity<String> deconnexion(@RequestParam String login,@RequestHeader String authorization) {
        try {
            if(facade.deconnexionOK(login, authorization)) {
                System.out.println(login + " is disconnected.");
                return ResponseEntity.status(HttpStatus.OK).build();
            }
            else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (TuFaisQuoiChacalException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping(value="/recupUtilisateursConnect")
    public ResponseEntity<ArrayList<String>> recupUtilisateursConnect(){
        System.out.println("Liste de personnes connectés au client" + userConnect);
        return ResponseEntity.status(HttpStatus.OK).body(userConnect);
    }


    @PostMapping(value = "/desabonnement")
    public ResponseEntity<Integer> desabonnement(@RequestBody UtilisateurDTO util) {
        int d = facade.desabonnement(util.getNom(),util.getPassword(),util.getConfirmPassword(), utilRepository,partieRepository);
        if (d == 1){
            userConnect.remove(util.getNom());
            return ResponseEntity.ok(1);
        }else if (d == 0 || d == 1){
            return ResponseEntity.notFound().build();
        }else {
            return ResponseEntity.notFound().build();
        }

    }

    /**
     * Permet de créer une partie par un maître du jeu
     * @param partieDTO
     * @return
     */
    @PostMapping(value = "/creePartie")
    public ResponseEntity<PartieDTO> creerPartie(@RequestBody PartieDTO partieDTO){
        try {

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest().path("/{id}")
                    .buildAndExpand(partieDTO.getIdPartie()).toUri();
            return ResponseEntity.created(location).body(PartieDTO.creerPartieDTO(facade.creerPartie(partieDTO.getMdj(), partieDTO.getInvites(), userConnect, partieRepository, utilRepository)));
        } catch (UtilisateurNonExistantException e) {
            System.out.println("une");
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); //code retour erreur à vérifier
        }
        catch (UtilisateurPasConnecteException e) {
            System.out.println("upc");
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); //code retour erreur à vérifier
        }
        catch (PasAssezDeJoueurException e) {
            System.out.println("padj");
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); //code retour erreur à vérifier
        }
        catch (TropDeJoueurException e) {
            System.out.println("tdj");
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); //code retour erreur à vérifier
        }
    }


    private ReplayProcessor<FluxDTO> recupererRejointPartie = ReplayProcessor.create(0,false);
    @GetMapping(value = "/joue/recupereRejointPartie", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<FluxDTO> recupererRejointPartie(){ return Flux.from(recupererRejointPartie); }


    /**
     * Permet à un joueur invité à une partie de la rejoindre
     * @param pseudo
     * @param idPartie
     * @return
     */
    @PatchMapping(value = "/rejointPartie")
    public ResponseEntity<PartieDTO> rejoindrePartie(@RequestParam String pseudo, @RequestParam String idPartie){
        try {

            PartieDTO partieDTO = PartieDTO.creerPartieDTO(facade.rejoindrePartie(pseudo, idPartie, partieRepository));

            FluxDTO fluxDTO = new FluxDTO(pseudo);
            fluxDTO.setAction("REJOINT");
            fluxDTO.setQuiRejoins(pseudo);
            recupererRejointPartie.onNext(fluxDTO);

            return ResponseEntity.ok(partieDTO);

        } catch (JoueurPasInviteException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (PartieNonExistanteException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }


    @GetMapping(value = "/acceptSauvegarde")
    public ResponseEntity<String> accepterSauvegarde(@RequestParam String idPartie,@RequestParam String idJoueur,@RequestParam Boolean accept){

        HashMap<String,Boolean> joueur_reponse = new HashMap<String,Boolean>();
        joueur_reponse.put(idJoueur,accept);

        reponseSave.put(idPartie, joueur_reponse);

        return ResponseEntity.ok().build();

    }

    private ReplayProcessor<FluxDTO> recupererSauvegarde = ReplayProcessor.create(0,false);
    @GetMapping(value = "/joue/recupererSauvegarde", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<FluxDTO> recupererSauvegarde(){ return Flux.from(recupererSauvegarde); }

    @GetMapping(value = "/sauvegarde")
    public ResponseEntity<List<PartieDTO>> sauvegarde(@RequestParam int idJoueur){

        System.out.println("**Liste des parties sauvegarder**");
        return ResponseEntity.ok(PartieDTO.listePartiestoDTO(facade.recupererPartieSauvegarder(String.valueOf(idJoueur),utilRepository,partieRepository)));
    }

    @PatchMapping(value = "/sauvegarde")
    public ResponseEntity<Partie> sauvegarde(@RequestBody PartieDTO partieDTO){

        System.out.println("**Sauvegarde**");
        FluxDTO fluxDTO = new FluxDTO();
        fluxDTO.setAction("SAUVEGARDE");

        recupererSauvegarde.onNext(fluxDTO);
        return ResponseEntity.ok(facade.sauvegarderPartie(partieDTO,partieRepository));

    }

    /**
     * Permet de restaurer une partie grâce au pseudo du mdj et à l'idPartie
     * @param idPartie
     * @param pseudoMdj
     * @return
     */
    @PatchMapping(value = "/restaurePartie")
    public ResponseEntity<PartieDTO> restaurerPArtie(@RequestParam String idPartie, @RequestParam String pseudoMdj){
        try {
            return ResponseEntity.ok(PartieDTO.creerPartieDTO(facade.restaurerPartie(idPartie, pseudoMdj, partieRepository)));
        } catch (PartieNonExistanteException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    /**
     * Permet à un joueur invité à une partie de la refuser
     * @param pseudo
     * @param idPartie
     * @return
     */
    @PatchMapping(value = "/refusePartie")
    public ResponseEntity<PartieDTO> refuserInvitation(@RequestParam String pseudo, @RequestParam String idPartie){
        try {
            return ResponseEntity.ok(PartieDTO.creerPartieDTO(facade.refuserInvitationPartie(pseudo, idPartie, partieRepository)));
        } catch (JoueurPasInviteException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (PartieNonExistanteException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }


    /**
     * Permet de récupérer les listes des parties dans lesquelles un utilisateur a été invité
     * @param pseudo
     * @return
     */
    @GetMapping(value = "/consulteInvitation")
    public ResponseEntity<List<PartieDTO>> consulterInvitations(@RequestParam String pseudo){

        List<PartieDTO> listePartieDTO = new ArrayList<>();
        List<Partie> listePartie = facade.consulterInvitation(pseudo, partieRepository);

        for(Partie partie : listePartie){
            listePartieDTO.add(PartieDTO.creerPartieDTO(partie));
        }

        return ResponseEntity.ok(listePartieDTO);
    }


    @GetMapping(value = "/user/{id}")
    public ResponseEntity<UtilisateurDTO> consulterInfoUtilisateur(@PathVariable("id") String pseudo){
        try{
            return ResponseEntity.ok(UtilisateurDTO.creerUtilDTO(facade.consulterInfoUtilisateur(pseudo, utilRepository)));
        }
        catch(UtilisateurNonExistantException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    private ReplayProcessor<FluxDTO> recupererLancerPartie = ReplayProcessor.create(0,false);
    @GetMapping(value = "/joue/recupereLancerPartie", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<FluxDTO> recupererLancerPartie(){
        return Flux.from(recupererLancerPartie);
    }


    @PatchMapping(value = "/lancePartie/{idPartie}")
    public ResponseEntity<PartieDTO> lancerPartie(@PathVariable("idPartie") String idPartie, @RequestParam String pseudoMdj){
        try {

            HashMap<Partie, String> res = facade.lancerPartie(idPartie, pseudoMdj, partieRepository);
            PartieDTO partieDTO = null;
            String action = "";

            for (Map.Entry<Partie, String> entry : res.entrySet()) {
                partieDTO = PartieDTO.creerPartieDTO(entry.getKey());
                action = entry.getValue();
            }

            FluxDTO fluxDTO = new FluxDTO(pseudoMdj);
            fluxDTO.setLancerPartie(true);
            fluxDTO.setPartie(partieDTO);

            if(partieDTO.getJoueurs().size() < 2)
                fluxDTO.setAction(action);
            else
                fluxDTO.setAction(action);

            recupererLancerPartie.onNext(fluxDTO);

            return ResponseEntity.ok(partieDTO);

        } catch (PartieNonExistanteException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (JoueursPasConnecteException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        }
    }


    private ReplayProcessor<FluxDTO> recupererDeplacement = ReplayProcessor.create(0,false);
    @GetMapping(value = "/joue/recupereDeplacement", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<FluxDTO> recupererDeplacement(){
        return Flux.from(recupererDeplacement);
    }

    /**
     * Permet à un joueur d'une partie de changer de position selon un point passé en paramètre
     * @param idPartie
     * @param idJoueur
     * @param point
     * @return
     */
    @PatchMapping(value = "/joue/{idPartie}/joueur/{idJoueur}/deplacePion")
    public ResponseEntity<JoueurDTO> deplacerPion(@PathVariable("idPartie") String idPartie, @PathVariable("idJoueur") String idJoueur, @RequestParam String piece, @RequestBody PointDTO point){
        try {

            boolean aPritPassage = facade.isJoueurChangePiece(idPartie, idJoueur, piece, partieRepository);
            JoueurDTO joueurDTO = JoueurDTO.creerJoueurDTO(facade.deplacerPion(idPartie, idJoueur, piece, point, partieRepository));

            FluxDTO fluxDTO = new FluxDTO(idJoueur);
            fluxDTO.setAction("DEPLACE");
            fluxDTO.setLeJoueur(joueurDTO);
            fluxDTO.setPositionCourante(joueurDTO.getPositionCourante());
            fluxDTO.setaPrisPassage(aPritPassage);
            recupererDeplacement.onNext(fluxDTO);

            return ResponseEntity.ok(joueurDTO);

        } catch (PartieNonExistanteException e) {
            System.out.println("pne");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (JoueurPasInviteException e) {
            System.out.println("jpi");
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (JoueurDejaPresentException e) {
            System.out.println("jdpe");
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (PartiePasEnCoursException e) {
            System.out.println("ppec");
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        }
    }




    /*
     * ================================= CONSULTER CARNET =================================
     */


    private ReplayProcessor<FluxDTO> recupererCarnet = ReplayProcessor.create(0,false);
    @GetMapping(value = "/joue/recupererCarnet", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<FluxDTO> recupererCarnet(){
        return Flux.from(recupererCarnet);
    }



    @GetMapping(value = "/joue/{idPartie}/joueur/{idJoueur}/consulteCarnet")
    public ResponseEntity<String> consulteCarnet(@PathVariable("idPartie") String idPartie, @PathVariable("idJoueur") String idJoueur){
        FluxDTO fluxDTO = new FluxDTO(idJoueur);
        try{
            fluxDTO.setCarnet(facade.consulterCarnet(idJoueur,idPartie,partieRepository,utilRepository));
            fluxDTO.setPersoQuiJoue(idJoueur);
            fluxDTO.setAction("CONSULTER_CARNET");
            recupererCarnet.onNext(fluxDTO);
            return ResponseEntity.ok("ok");
        } catch (PartiePasEnCoursException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (PartieNonExistanteException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }



    }




    /*
     * ================================= ACCUSER =================================
     */

    private ReplayProcessor<FluxDTO> recupererAccusation = ReplayProcessor.create(0,false);
    @GetMapping(value = "/joue/recupereAccusation", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<FluxDTO> recupererAccusation(){
        return Flux.from(recupererAccusation);
    }

    @PatchMapping(value = "/joue/{idPartie}/joueur/{idJoueur}/accuse")
    public ResponseEntity<Boolean> accuse(@PathVariable("idPartie") String idPartie, @PathVariable("idJoueur") String idJoueur, @RequestBody ArrayList<CarteDTO> cartes){
        CarteDTO arme = cartes.get(0);
        CarteDTO lieu = cartes.get(1);
        CarteDTO coupable = cartes.get(2);
        ArrayList<CarteDTO> listeCarte = new ArrayList<CarteDTO>();
        listeCarte.add(arme);
        listeCarte.add(lieu);
        listeCarte.add(coupable);
        Boolean accusation = facade.accuser(lieu,arme,coupable,idPartie,partieRepository);
        Utilisateur utilisateur = utilRepository.findByNom(idJoueur);
        Partie partie = partieRepository.findByIdPartie(idPartie);

        FluxDTO fluxDTO = new FluxDTO(utilisateur.getNom());
        fluxDTO.setAccusation(accusation);
        fluxDTO.setAccusationsCartes(listeCarte);
        fluxDTO.setPersoQuiJoue(partie.getUnJoueur(utilisateur.getNom()).getPerso().getNom());
        fluxDTO.setAction("ACCUSE");
        recupererAccusation.onNext(fluxDTO);


        /*if(accusation)
            return ResponseEntity.ok(accusation);
        else{
            return ResponseEntity.status(HttpStatus.MOVED_TEMPORARILY).build();
        }*/


        return ResponseEntity.ok(accusation);

    }




    /*
     * ================================= SUPPOSER =================================
     */

    private ReplayProcessor<FluxDTO> recupererSuppositions = ReplayProcessor.create(0,false);
    @GetMapping(value = "/joue/recupereSuppositions", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<FluxDTO> recupererSuppositions(){
        return Flux.from(recupererSuppositions);
    }

    @PatchMapping(value = "/joue/{idPartie}/joueur/{idJoueur}/suppose")
    public ResponseEntity<String> suppose(@PathVariable("idPartie") String idPartie, @PathVariable("idJoueur") String idJoueur, @RequestBody ArrayList<CarteDTO> cartes) {

        try {

            CarteDTO arme = cartes.get(0);
            CarteDTO lieu = cartes.get(1);
            CarteDTO coupable = cartes.get(2);

            Partie partie = facade.supposer(lieu,arme,coupable,idPartie,partieRepository);
            Utilisateur utilisateur = utilRepository.findByNom(idJoueur);
            List<CarteDTO> hypCourante = CarteDTO.creerListeCarteDTO(partie.getHypotheseCourante());

            FluxDTO fluxDTO = new FluxDTO();
            fluxDTO.setQuiJoue(utilisateur.getNom());
            fluxDTO.setSupposition(hypCourante);
            fluxDTO.setAction("SUPPOSE");
            fluxDTO.setPersoQuiJoue(partie.getUnJoueur(utilisateur.getNom()).getPerso().getNom());
            fluxDTO.setQuiDoitReveler(JoueurDTO.creerJoueurDTO(facade.quiDoisReveler(idPartie, idJoueur, partie.getHypotheseCourante(), partieRepository)));
            fluxDTO.setReveleCartes(CarteDTO.creerListeCarteDTO(facade.quoiReveler(idPartie, idJoueur, partie.getHypotheseCourante(), partieRepository)));
            recupererSuppositions.onNext(fluxDTO);

            return ResponseEntity.ok().build();

        } catch (PasTonTourException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (PartiePasEnCoursException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        } catch (PartieNonExistanteException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (PersonneALaCarteException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

    }




    /*
     * ================================= CONSULTER CARTES JEU =================================
     */

    @GetMapping(value = "/joue/{idPartie}/joueur/{idJoueur}/consulteCartesJeu")
    public ResponseEntity<List<CarteDTO>> consulteCartesJeu(@PathVariable("idPartie") String idPartie, @PathVariable("idJoueur") String idJoueur){
        try {
            System.out.println(facade.getCartesJeu(idJoueur, idPartie, partieRepository));
            return ResponseEntity.ok(CarteDTO.creerListeCarteDTO(facade.getCartesJeu(idJoueur, idPartie, partieRepository)));
        } catch (PartieNonExistanteException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (PartiePasEnCoursException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (JoueurPasInviteException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }




    /*
     * ================================= QUITTER PARTIE =================================
     */

    private ReplayProcessor<FluxDTO> recupererQuitterPartie = ReplayProcessor.create(0,false);
    @GetMapping(value = "/joue/recupereQuitterPartie", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<FluxDTO> recupererPartieQuitter(){
        return Flux.from(recupererQuitterPartie);
    }

    @PatchMapping(value = "/quitterPartie/{idPartie}")
    public ResponseEntity<String> quitterPartie(@PathVariable("idPartie") String idPartie,@RequestParam String idJoueur){
        try {
            FluxDTO fluxDTO = new FluxDTO();
            fluxDTO.setQuiQuitte(idJoueur);
            fluxDTO.setAction("QUITTER_PARTIE");
            recupererQuitterPartie.onNext(fluxDTO);

            return ResponseEntity.ok(facade.quitterPartie(idJoueur,idPartie,partieRepository));
        } catch (PartieNonExistanteException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (JoueursPasConnecteException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }





    /*
     * ================================= LANCER DES =================================
     */

    private ReplayProcessor<FluxDTO> recupererDesFlux = ReplayProcessor.create(0,false);
    @GetMapping(value = "/joue/recupereDes", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<FluxDTO> recupererDesFlux(){
        System.out.println(recupererDesFlux);
        return Flux.from(recupererDesFlux);
    }
    @GetMapping(value = "/joue/{idPartie}/joueur/{idJoueur}/lanceDes")
    public ResponseEntity<DesDTO> lancerDes(@PathVariable("idPartie") String idPartie, @PathVariable("idJoueur") String idJoueur){

        DesDTO desDTO= DesDTO.creerDesDTO(facade.lancerDes());
        Utilisateur utilisateur = utilRepository.findByNom(idJoueur);
        Partie partie = partieRepository.findByIdPartie(idPartie);
        FluxDTO fluxDTO = new FluxDTO(utilisateur.getNom());
        fluxDTO.setDes(desDTO);
        fluxDTO.setAction("DES");
        fluxDTO.setPersoQuiJoue(partie.getUnJoueur(utilisateur.getNom()).getPerso().getNom());
        recupererDesFlux.onNext(fluxDTO);
        return ResponseEntity.ok(desDTO);
    }





    /*
     * ================================= REMPLIR CARNET =================================
     */



    @PatchMapping(value = "/joue/{idPartie}/joueur/{idJoueur}/remplirCarnet")
    public ResponseEntity<String> remplirCarnet(@RequestBody List<List<CarteDTO>> cartes, @PathVariable("idPartie") String idPartie, @PathVariable("idJoueur") String idJoueur){

        try{
            return ResponseEntity.ok(facade.remplirCarnet(cartes,idPartie,idJoueur,partieRepository,utilRepository));
        } catch (PartieNonExistanteException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }catch (UtilisateurNonExistantException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        catch (JoueursPasConnecteException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }



    /*
     * ================================= RECUPERER PASSAGE =================================
     */
    private ReplayProcessor<FluxDTO> recupererPassage = ReplayProcessor.create(0,false);
    @GetMapping(value = "/joue/recupererPassage", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<FluxDTO> recupererPassage(){
        return Flux.from(recupererPassage);
    }

    @PatchMapping(value = "/joue/{idPartie}/joueur/{idJoueur}/prendPassage")
    public ResponseEntity<JoueurDTO> prendPassage(@RequestBody PointDTO destination, @PathVariable("idPartie") String idPartie, @PathVariable("idJoueur") String idJoueur ,@RequestParam String piece){
        try {

            JoueurDTO joueurDTO = JoueurDTO.creerJoueurDTO(facade.prendrePassage(destination,idPartie,idJoueur,piece,partieRepository));

            FluxDTO fluxDTO = new FluxDTO(idJoueur);
            fluxDTO.setLeJoueur(joueurDTO);
            fluxDTO.setAction("PRENDRE_PASSAGE");
            fluxDTO.setPositionCourantePassage(joueurDTO.getPositionCourante());
            fluxDTO.setPersoQuiJoue(joueurDTO.getPerso().getNom());
            recupererPassage.onNext(fluxDTO);

            return ResponseEntity.ok(joueurDTO);

        } catch (PartieNonExistanteException e) {
            System.out.println("pne");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (PartiePasEnCoursException e) {
            System.out.println("ppec");
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        }
    }




    /*
     * ================================= REVELER CARTE =================================
     */

    private ReplayProcessor<FluxDTO> recupererRevelerCarte = ReplayProcessor.create(0,false);
    @GetMapping(value = "/joue/recupererRevelerCarte", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<FluxDTO> recupererRevelerCarte(){ return Flux.from(recupererRevelerCarte); }

    @PatchMapping(value = "/joue/{idPartie}/joueur/{idJoueur}/reveleCarte")
    public ResponseEntity revelerCarte(@PathVariable("idPartie") String idPartie, @PathVariable("idJoueur") String idJoueur, @RequestParam String carteRevelee, @RequestBody List<JoueurDTO> aQuiReveler){

        try {

            Partie partie = partieRepository.findByIdPartie(idPartie);

            FabriqueCarte fabriqueCarte = new FabriqueCarte();
            List<Carte> listeCarte = new ArrayList<>();
            listeCarte.add(fabriqueCarte.getCarteJeuByNom(carteRevelee));

            FluxDTO fluxDTO = new FluxDTO(idJoueur);
            fluxDTO.setAction("REVELER");
            fluxDTO.setPersoQuiJoue(partie.getUnJoueur(idJoueur).getPerso().getNom());
            fluxDTO.setaQuiReveler(aQuiReveler);
            fluxDTO.setCarteRevelee(CarteDTO.creerCarteDTO(fabriqueCarte.getCarteJeuByNom(carteRevelee)));
            fluxDTO.setQuiDoitReveler(JoueurDTO.creerJoueurDTO(facade.quiDoisReveler(idPartie, idJoueur, listeCarte, partieRepository)));

            recupererRevelerCarte.onNext(fluxDTO);

            return ResponseEntity.ok().build();

        } catch (PasTonTourException e) {
            System.out.println("Pas ton tour");
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        } catch (PartiePasEnCoursException e) {
            System.out.println("Pas en cours");
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        } catch (PartieNonExistanteException e) {
            System.out.println("Partie pas existante");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        } catch (PersonneALaCarteException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }


    }




    /*
     * ================================= TERMINER SON TOUR =================================
     */
    private ReplayProcessor<FluxDTO> recupererTerminerTour = ReplayProcessor.create(0,false);
    @GetMapping(value = "/joue/recupererTerminerTour", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<FluxDTO> recupererTerminerTour(){ return Flux.from(recupererTerminerTour); }


    @PatchMapping(value= "/joue/{idPartie}/joueur/{idJoueur}/termineTour")
    public ResponseEntity<JoueurDTO> terminerTour(@PathVariable String idPartie, @PathVariable String idJoueur){

        try {

            JoueurDTO joueurDTO = JoueurDTO.creerJoueurDTO(facade.terminerTour(idPartie, idJoueur, partieRepository));
            Partie partie = partieRepository.findByIdPartie(idPartie);

            FluxDTO fluxDTO = new FluxDTO(idJoueur);
            fluxDTO.setPersoQuiJoue(partie.getUnJoueur(idJoueur).getPerso().getNom());
            fluxDTO.setaQuiLeTour(joueurDTO);
            fluxDTO.setAction("TERMINE TOUR");
            recupererTerminerTour.onNext(fluxDTO);

            return ResponseEntity.ok(joueurDTO);

        } catch (PartieNonExistanteException e) {
            System.out.println("NOT FOUND");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (PartiePasEnCoursException e) {
            System.out.println("PRECONDITION FAILED");
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        } catch (PasTonTourException e) {
            System.out.println("CONFLICT");
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

    }


    /*
     * ================================= TERMINER LA PARTIE =================================
     */
    private ReplayProcessor<FluxDTO> recupererTerminerPartie = ReplayProcessor.create(0,false);
    @GetMapping(value = "/joue/recupererTerminerPartie", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<FluxDTO> recupererTerminerPartie(){ return Flux.from(recupererTerminerPartie); }


    @PatchMapping(value= "/terminePartie")
    public ResponseEntity terminerPartie(@RequestParam String idPartie){


        try {

            //flux : hypothèse (solution)
            facade.terminerPartie(idPartie, partieRepository);
            Partie partie = partieRepository.findByIdPartie(idPartie);

            FluxDTO fluxDTO = new FluxDTO();
            fluxDTO.setAction("TERMINER PARTIE");
            fluxDTO.setTerminerPartie(true);
            if(partie.isGagnant()) {
                fluxDTO.setGagnant(JoueurDTO.creerJoueurDTO(partie.getGagnant()));
                fluxDTO.setSupposition(CarteDTO.creerListeCarteDTO(partie.getSolution()));
            }

            recupererTerminerPartie.onNext(fluxDTO);

            return ResponseEntity.ok().build();

        } catch (PartieNonExistanteException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (PartiePasEnCoursException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        }
    }


    private ReplayProcessor<FluxDTO> recupererPiocheCarteIndice = ReplayProcessor.create(0,false);
    @GetMapping(value = "/joue/recupererPiocheCarteIndice", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<FluxDTO> recupererPiocheCarte(){ return Flux.from(recupererPiocheCarteIndice); }


    @GetMapping(value = "/joue/{idPartie}/joueur/{idJoueur}/piocheCarteIndice")
    public ResponseEntity piocherCarteIndice(@PathVariable String idPartie, @PathVariable String idJoueur){

        try {
            Map<Joueur, Carte> quiDoisReveler = facade.piocherCarteIndice(idJoueur, idPartie, partieRepository);
            Partie partie = partieRepository.findByIdPartie(idPartie);

            FluxDTO fluxDTO = new FluxDTO(idJoueur);
            fluxDTO.setPersoQuiJoue(partie.getUnJoueur(idJoueur).getPerso().getNom());
            fluxDTO.setAction("PIOCHE_CARTE");
            HttpStatus httpStatus = HttpStatus.OK;
            for (Map.Entry<Joueur, Carte> entry : quiDoisReveler.entrySet()){

                if(entry.getKey() == null)
                    httpStatus = HttpStatus.NO_CONTENT;
                else
                    fluxDTO.setQuiDoitReveler(JoueurDTO.creerJoueurDTO(entry.getKey()));

                fluxDTO.setCarteIndiceAreveler(CarteDTO.creerCarteDTO(entry.getValue()));
            }

            recupererPiocheCarteIndice.onNext(fluxDTO);

            return ResponseEntity.status(httpStatus).build();

        } catch (PasTonTourException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        } catch (PartiePasEnCoursException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (PartieNonExistanteException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }


}
