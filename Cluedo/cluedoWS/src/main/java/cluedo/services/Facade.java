package cluedo.services;

import cluedo.DTO.*;
import cluedo.controleur.CluedoControl;
import cluedo.exceptions.*;
import cluedo.jwtTokens.TokensG;
import cluedo.modele.*;
import cluedo.repository.JoueurRepository;
import cluedo.repository.PartieRepository;
import cluedo.repository.UtilisateurRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Facade {

    /*
     * ATTRIBUTS
     */
    FabriqueCarte fabriqueCarte;


    /*
     * CONSTRUCTEUR
     */
    public Facade(){
        fabriqueCarte = new FabriqueCarte();
    }


    /*
     * METHODES
     */
    public int inscription(String nom, String pass, String ComfirmPassword ,UtilisateurRepository repo){
        ArrayList<Utilisateur> utils = repo.findAll();
        Utilisateur utilM = new Utilisateur(nom,pass);
        if (!String.valueOf(pass).equals(ComfirmPassword)){
            return -1; //pass et comfirmPass different
        }

        else {
            for (Utilisateur utilisateur : utils) {
                if (String.valueOf(utilisateur.getNom()).equals(utilM.getNom())) {
                    return -2; //utilisateur deja existant
                }
            }
            repo.save(utilM);
            return Integer.parseInt(utilM.getIdUtil());


        }
    }

    public int desabonnement(String nom,String pass,String ComfirmPassword , UtilisateurRepository repo ,PartieRepository partieRepository){
        ArrayList<Utilisateur> utils = repo.findAll();
        ArrayList<Partie> parties = partieRepository.findAll();
        Utilisateur utilM = new Utilisateur(nom,pass);

        if (!pass.equals(ComfirmPassword)) {
            return 0;
        } else {

            for (Utilisateur utilisateur : utils) {
                if (String.valueOf(utilisateur.getNom()).equals(utilM.getNom()) && String.valueOf(utilisateur.getPassword()).equals(utilM.getPassword())) {
                    repo.deleteByNomAndPassword(utilM.getNom(), utilM.getPassword());
                    for (Partie partie : parties) {
                        if (utilM.getNom().equals(partie.getMdj()) && partie.getInvites().size() > 2){
                            partieRepository.delete(partie);
                        }
                        if (partie.getInvites().contains(utilM.getNom()) && partie.getInvites().size() == 2 ){
                            partieRepository.delete(partie);
                        }
                        if(partie.getInvites().contains(utilM.getNom())) {
                            partie.getInvites().remove(utilM.getNom());
                            partieRepository.save(partie);

                        }
                    }

                    return 1;
                }
            }
        }

        return -1;
    }


    /**
     * Permet d'instancier un objet partie, et de la créer en base de donnée.
     * @param mdj
     * @param invites
     * @param partieRepository
     * @return
     * @throws UtilisateurNonExistantException
     */
    public Partie creerPartie(String mdj, List<String> invites, List<String> userConnected, PartieRepository partieRepository, UtilisateurRepository utilisateurRepository) throws UtilisateurNonExistantException, UtilisateurPasConnecteException, PasAssezDeJoueurException, TropDeJoueurException {

        //Vérification du nombre de joueur
        if(invites.size() < 2)
            throw new PasAssezDeJoueurException();
        if(invites.size() > 5)
            throw new TropDeJoueurException();


        //Vérification que tous les joueurs invités existent en bd et sont connectés
        for(String invite : invites){

            if(utilisateurRepository.findByNom(invite) == null)
                throw new UtilisateurNonExistantException();

            if(!userConnected.contains(invite))
                throw new UtilisateurPasConnecteException();

        }

        //Création de la partie
        Partie partie = new Partie(mdj, invites);
        partieRepository.save(partie);
        return partie;
    }


    /**
     * Permet à un joueur invité à une partie de pouvoir la rejoindre
     * @param pseudo joueur invité
     * @param idPartie partie concernée
     * @param partieRepository listes parties en bd
     * @return une instance de la partie modifiée
     * @throws JoueurPasInviteException
     * @throws PartieNonExistanteException
     */
    public Partie rejoindrePartie(String pseudo, String idPartie, PartieRepository partieRepository) throws JoueurPasInviteException, PartieNonExistanteException{

        List<Partie> listePartie = partieRepository.findAll();
        for(Partie partie : listePartie) {

            //Si la partie est dans la liste des parties instanciées
            if (idPartie.equals(partie.getIdPartie()))

                //Si le joueur fait parti des invités
                if (partie.getInvites().contains(pseudo)) {
                    //et qu'il n'est pas déjà connecté
                    if(!partie.getConnectes().contains(pseudo))
                        partie.ajouterConnecte(pseudo);

                    partieRepository.save(partie);
                    return partie;
                } else
                    throw new JoueurPasInviteException();
        }

        throw new PartieNonExistanteException();
    }


    /*
    public int accepterSauvegarde(String idJoueur,Boolean accept){
        if(idJoueur != null && idJoueur != "" && accept == true){
            return 1;
        }
        return -1;
    }
     */

    public ArrayList<Partie> recupererPartieSauvegarder(String idJoueur,UtilisateurRepository utilisateurRepository ,PartieRepository partieRepository){
        String name = utilisateurRepository.findByIdUtil(idJoueur).getNom();
        ArrayList<Partie> parties = partieRepository.findAll();
        ArrayList<Partie> partiesSauvegarde = new ArrayList<>();
        for (Partie partie : parties){
            if(String.valueOf(partie.getMdj()).equals(name) && partie.getStatut().equals("save")){
                partiesSauvegarde.add(partie);
            }
        }

        return partiesSauvegarde;
    }

    public Partie sauvegarderPartie(PartieDTO partieDTO,PartieRepository partieRepository){
        List<String> initConnecte = new ArrayList<String>();
        System.out.println("INPUT");
        System.out.println(partieDTO);

        Partie partieSave = partieRepository.findByIdPartie(partieDTO.getIdPartie());
        partieSave.setStatut("save");
        partieSave.setConnectes(initConnecte);

        partieSave.setJoueurCourant(partieDTO.getJoueurCourant());
        partieSave.setPioche(CarteDTO.listeDTOtoCarte(partieDTO.getPioche()));
        System.out.println("OUTPUT");
        System.out.println(partieSave);
        partieRepository.save(partieSave);
        return partieSave;
    }

    /**
     * Permet de restaurer une partie dans le but de la relancer
     * @param idPartie
     * @param partieRepository
     * @return
     */
    public Partie restaurerPartie(String idPartie, String pseudoMdj, PartieRepository partieRepository) throws PartieNonExistanteException{

        Partie partie = partieRepository.findByIdPartieAndMdj(idPartie, pseudoMdj);

        if(partie == null)
            throw new PartieNonExistanteException();

        partie.restaurerPartie();
        partieRepository.save(partie);

        return partie;
    }


    /**
     * Permet à un joueur de refuser une invitation de partie
     * @param idPartie
     * @param pseudo
     * @param partieRepository
     * @return
     * @throws PartieNonExistanteException
     * @throws JoueurPasInviteException
     */
    public Partie refuserInvitationPartie(String pseudo, String idPartie, PartieRepository partieRepository) throws PartieNonExistanteException, JoueurPasInviteException{

        List<Partie> listePartie = partieRepository.findAll();
        for(Partie partie : listePartie) {

            //Si la partie est dans la liste des parties instanciées
            if (idPartie.equals(partie.getIdPartie()))

                //Si le joueur fais parti des invités
                if (partie.getInvites().contains(pseudo)) {
                    //et qu'il n'est pas déjà connecté
                    if(!partie.getConnectes().contains(pseudo))
                        partie.enleverInvite(pseudo);

                    partieRepository.save(partie);
                    return partie;
                } else
                    throw new JoueurPasInviteException();
        }

        throw new PartieNonExistanteException();
    }


    /**
     * Permet à un joueur de consulter les parties auxquelles il a été invitées
     * @param pseudo
     * @param partieRepository
     * @return
     */
    public List<Partie> consulterInvitation(String pseudo, PartieRepository partieRepository){

        List<Partie> partiesInvites = new ArrayList<>();

        List<Partie> listePartie = partieRepository.findAll();
        for(Partie partie : listePartie){
            if(partie.getInvites().contains(pseudo) && partie.getStatut().equals("attente")){
                partiesInvites.add(partie);
            }
        }

        return partiesInvites;
    }


    /**
     * Permet de récupérer les données d'un utilisateur
     * @param pseudo
     * @param utilisateurRepository
     * @return
     * @throws UtilisateurNonExistantException
     */
    public Utilisateur consulterInfoUtilisateur(String pseudo, UtilisateurRepository utilisateurRepository) throws UtilisateurNonExistantException{

        Utilisateur util = utilisateurRepository.findByNom(pseudo);

        if(util == null)
            throw new UtilisateurNonExistantException();
        else
            return util;

    }


    /**
     * Cette méthode permet à un utilisateur de se connecter à l'application
     * on va donc vérifier si les champs sont non null. Puis on regarder si
     * l'utilisateur qui essaye de se connecter à le bon password et si il
     * existe dans database. Une fois ces conditions validées, on va créer
     * un token qui sera unique pour l'utilisateur. Cet utilisateur sera mis
     * dans une liste d'utilisateurs connectés où on pourra vérifier qu'il ne
     * connecte pas plusieurs fois.
     *
     * @param utilDTO est un utilisateurDTO
     * @return une ResponseEntity.ok avec un token dans le header.
     *         une ResponseEntity.badRequest si il manque des champs.
     *         une ResponseEntity.status(HttpStatus.UNAUTHORIZED) si il est déjà connecté.
     *         une ResponseEntity.status(HttpStatus.NOT_FOUND) si le password est invalide et si
     *         l'tuilisateur n'est pas en bd.
     */
    public String connexion(UtilisateurDTO utilDTO, UtilisateurRepository utilisateurRepository) throws ChampInvalideException, UtilisateurDejaConnecte, MauvaisPasswordException {

        if(utilDTO.getNom() == null || utilDTO.getPassword() == null ||  utilDTO.getNom() == "" || utilDTO.getPassword() == ""){
            //return ResponseEntity.badRequest().build();
            throw new ChampInvalideException();
        }

        Utilisateur user = utilisateurRepository.findByNom(utilDTO.getNom());
        ArrayList<Utilisateur> utils = utilisateurRepository.findAll();
        String name;



        for (Utilisateur utilisateur : utils) {
            if(CluedoControl.userConnect.contains(utilDTO.getNom()) && String.valueOf(utilisateur.getPassword()).equals(utilDTO.getPassword())){
                System.out.println( utilDTO.getNom()+ " already connected");

                throw new UtilisateurDejaConnecte();
                //return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            else {
                // Vérification si l'utilisateur est existant
                if (String.valueOf(utilisateur.getNom()).equals(utilDTO.getNom())) {
                    name = utilisateur.getNom();
                    // Vérification si le password est OK
                    if(String.valueOf(utilisateur.getPassword()).equals(utilDTO.getPassword())){
                        String token = CluedoControl.tokenGenerator.createToken(user);
                        CluedoControl.userConnect.add(utilDTO.getNom());
                        for(String connect: CluedoControl.userConnect){
                            System.out.println(connect + " is connected.");
                        }
                        System.out.println("Token generated and connexion established.");
                        /*return ResponseEntity.ok()
                                .header(TokensG.AUTH_TOKEN_NAME,token)
                                .header(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,TokensG.AUTH_TOKEN_NAME)
                                .build();*/

                        return token;
                    }
                    else {
                        System.out.println("bad credentials for user " + user.getNom()+".");
                        throw new MauvaisPasswordException();
                        //return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

                    }
                }
            }
        }

        System.out.println("User not found: " + utilDTO.getNom()+".");
        //return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        throw new UtilisateurNonExistantException();
    }



    /**
     * Cette méthode va nous permettre de déconnecter un utilisateur quand il le souhaite,
     * pour déconnecter un utilisateur on va tester que le login et le token corresponde et
     * renvoie vers un seul même utilisateur dans le cas contraire, un utilisateur essaye de
     * déconnecter un autre utilisateur. On va donc à partir du token qui garantie la vraie
     * "identité" de l'utilisateur, le décoder pour faire ressortir le login. Si le login du
     * token est identique au login passé en paramètre. Il n'y a pas de soucis sur l'authenticité
     * de l'utilisateur.
     * @param login est l'utilisateur à déconnecté
     * @param authorization Ce string est le token de l'utilisateur qui émet la requete
     * @return  ResponseEntity.status(HttpStatus.OK) si l'utilisateur est le bon il est déconnecté
     *          ResponseEntity.status(HttpStatus.UNAUTHORIZED) si un petit malin essaye de déconnecter
     *          quelqu'un d'autre.
     */
    public boolean deconnexionOK(String login, String authorization) throws TuFaisQuoiChacalException {

        boolean res;
        String token = authorization;
        String user = CluedoControl.tokenGenerator.getClaimFromToken(token);

        if(user.equals(login)) {
            CluedoControl.userConnect.remove(login);
            return true;
        }
        else{
            System.out.println("Dont disconnect your buddy, bad guy");
            throw new TuFaisQuoiChacalException();
            //return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    public Des lancerDes() {
        int de1;
        int de2;

        de1 = ThreadLocalRandom.current().nextInt(1, 6 + 1);
        de2 = ThreadLocalRandom.current().nextInt(1, 6 + 1);
        Des des = new Des(de1, de2);
        return des;
    }


    /**
     * Permet de lancer une partie une fois que tous les joueurs invités s'y sont connectés
     * Instancie les joueurs en affectant un personnage au hasard, et un deck au hasard.
     * @param idPartie
     * @param pseudoMdj
     * @param partieRepository
     * @return
     * @throws PartieNonExistanteException
     * @throws JoueursPasConnecteException
     */
    public HashMap<Partie, String> lancerPartie(String idPartie, String pseudoMdj, PartieRepository partieRepository) throws PartieNonExistanteException, JoueursPasConnecteException {


        //On récupère la partie et on vérifie qu'elle existe
        Partie partie = partieRepository.findByIdPartieAndMdj(idPartie, pseudoMdj);
        HashMap<Partie, String> res = new HashMap<>();


        if(partie == null)
            throw new PartieNonExistanteException();

        if(partie.getInvites().size() + 1 != partie.getConnectes().size())
            throw new JoueursPasConnecteException();


        //La partie a déjà été lancée une fois
        if(partie.getJoueurs().size() > 1){
            partie.setStatut("en cours");
            partieRepository.save(partie);
            res.put(partie, "RESTAURE");
            return res;
        }

        //On récupère nos liste cartesJeu et cartesIndice grâce à la fabrique et on les mélanges au hasard
        List<Carte> cartesJeu = fabriqueCarte.getCartesJeu();
        Collections.shuffle(cartesJeu);

        List<Carte> cartesIndice = fabriqueCarte.getCartesIndices();
        Collections.shuffle(cartesIndice);


        List<Carte> solution = new ArrayList<>();
        boolean containsPerso = false;
        boolean containsArme  = false;
        boolean containsLieu  = false;

        //Création du deck solution
        for(Carte carte : cartesJeu){
            if(carte.getType().equals("perso") && !containsPerso){
                solution.add(carte);
                containsPerso = true;
            }

            if(carte.getType().equals("arme") && !containsArme){
                solution.add(carte);
                containsArme = true;
            }

            if(carte.getType().equals("lieu") && !containsLieu){
                solution.add(carte);
                containsLieu = true;
            }
        }

        /*for(Carte carte : solution){
            cartesJeu.remove(carte);
        }*/


        //On fait les changements nécessaire pour notre partie (instanciation des joueurs, de la solution,
        //des cartes indices, changement de statut) puis on la sauvegarde en BD.
        partie.instancierJoueurs(cartesJeu, fabriqueCarte.getCartePerso(), solution);
        for(Joueur joueur: partie.getJoueurs()){
            joueur.ajouterListeBlocnote(solution, 1);
        }
        partie.setSolution(solution);
        partie.setPioche(cartesIndice);
        partie.setStatut("en cours");
        partieRepository.save(partie);


        res.put(partie, "LANCER PARTIE");
        return res;

    }


    /**
     * Permet à un joueur de réveler une carte si celle-ci est dans sa main
     * @param idPartie
     * @param idJoueur
     * @param listeCarte
     * @param partieRepository
     * @return
     * @throws PartieNonExistanteException
     */
    public HashMap<Joueur, List<Carte>> revelerCarte(String idPartie, String idJoueur, List<Carte> listeCarte, PartieRepository partieRepository) throws PartieNonExistanteException, PartiePasEnCoursException, PasTonTourException {


        //On récupère la partie
        Partie partie = partieRepository.findByIdPartie(idPartie);


        //Et on fait les vérifications nécessaires : partie existante, en cours, si c'est le tour du joueur
        if(partie == null)
            throw new PartieNonExistanteException();

        if( !partie.getStatut().equals("en cours") )
            throw new PartiePasEnCoursException();

        if( !idJoueur.equals(partie.getJoueurCourant()) )
            throw new PasTonTourException();

        List<Carte> cartesAReveler = new ArrayList<>();

        //On récupère l'index du joueur en fonction de son tour dans la liste
        int indexPersoJoueurCourant = partie.getPersosJoues().indexOf(partie.getLeJoueurCourant().getPerso().getNom());

        HashMap<Joueur, List<Carte>> quiRevele = new HashMap<>();

        //Et on parcours la liste des joueurs en fonction de sa place dans la liste

        for(int i = indexPersoJoueurCourant +1; i < partie.getPersosJoues().size(); i++){
            Joueur joueur = partie.getJoueurParPerso(partie.getPersosJoues().get(i));

            for(Carte carte: joueur.getDeck()){
                if(quiRevele.size() == 0){
                    for(Carte hypothese: listeCarte) {
                        if (carte.getNom().equals(hypothese.getNom())) {
                            System.out.println("à mettre dans cartes à réveler");
                            cartesAReveler.add(hypothese);
                        }
                        System.out.println(quiRevele.size());
                    }
                }
            }


            if(cartesAReveler.size() > 0 )
                quiRevele.put(joueur, cartesAReveler);

            cartesAReveler.clear();
        }


        //Si indexPersoJoueurCourant != à -> On a déjà parcouru toute la liste,
        //On a pas besoin de reparcourir la liste.
        //quiRevele.size() != 0 -> On a déjà un joueur qui doit réveler
        if(indexPersoJoueurCourant != 0 && quiRevele.size() == 0){
            for(int i = 0; i < indexPersoJoueurCourant; i++){
                Joueur joueur = partie.getJoueurParPerso(partie.getPersosJoues().get(i));

                for(Carte carte: joueur.getDeck()){
                    if(quiRevele.size() == 0){
                        for(Carte hypothese: listeCarte)
                            if( carte.getNom().equals(hypothese.getNom()) ){
                                //quiRevele.put(joueur, carte);
                                cartesAReveler.add(hypothese);
                            }
                    }
                }

                if(cartesAReveler.size() > 0 )
                    quiRevele.put(joueur, cartesAReveler);
            }
        }

        partieRepository.save(partie);

        return quiRevele;
    }


    public Joueur quiDoisReveler(String idPartie, String idJoueur, List<Carte> listeCarte, PartieRepository partieRepository) throws PasTonTourException, PartiePasEnCoursException, PartieNonExistanteException, PersonneALaCarteException {

        HashMap<Joueur, List<Carte>> revelerCarte = revelerCarte(idPartie, idJoueur, listeCarte, partieRepository);

        if( revelerCarte.size() == 0 )
            throw new PersonneALaCarteException();

        for (Map.Entry<Joueur, List<Carte>> entry : revelerCarte.entrySet()) {
            return entry.getKey();
        }

        return null;
    }

    public List<Carte> quoiReveler(String idPartie, String idJoueur, List<Carte> listeCarte, PartieRepository partieRepository) throws PasTonTourException, PartiePasEnCoursException, PartieNonExistanteException, PersonneALaCarteException {

        HashMap<Joueur, List<Carte>> revelerCarte = revelerCarte(idPartie, idJoueur, listeCarte, partieRepository);


        if( revelerCarte.size() == 0 )
            throw new PersonneALaCarteException();

        for (Map.Entry<Joueur, List<Carte>> entry : revelerCarte.entrySet()) {
            return entry.getValue();
        }

        return null;
    }


    /**
     * Permet pour un joueur de changer de position sur le plateau
     * @param idPartie
     * @param idJoueur
     * @param pointDTO
     * @param partieRepository
     * @return
     * @throws PartieNonExistanteException
     * @throws JoueurPasInviteException
     */
    public Joueur deplacerPion(String idPartie, String idJoueur, String piece, PointDTO pointDTO, PartieRepository partieRepository) throws PartieNonExistanteException, JoueurPasInviteException, JoueurDejaPresentException, PartiePasEnCoursException {

        //On cherche la partie et on vérifie qu'elle existe bien
        Partie partie = partieRepository.findByIdPartie(idPartie);


        if(partie == null)
            throw new PartieNonExistanteException();

        if( !partie.getStatut().equals("en cours") )
            throw new PartiePasEnCoursException();

        //Notre nouveau point
        Point point = new Point(pointDTO.getX(), pointDTO.getY());

        //On initialise un joueur à null, et un indice i qui nous permettra de modifier notre joueur
        Joueur joueur = null;
        int i = -1;
        boolean joueurDejaPresent = false;

        //On parcours la liste des joueurs dans notre partie
        for(Joueur curJoueur: partie.getJoueurs()){

            //Si ce n'est pas notre joueur mais qu'il a la même position que celle où on veut aller -> joueur déjà présent à cette case
            if(!curJoueur.getUtil().equals(idJoueur) &&
                curJoueur.getPositionCourante().getX() == point.getX() &&
                curJoueur.getPositionCourante().getY() == point.getY()){
                    joueurDejaPresent = true;
            }

            //Si c'est notre joueur, on récupère son indice dans la liste des joueurs pour pouvoir l'actualiser
            if(curJoueur.getUtil().equals(idJoueur)){
                joueur = curJoueur;
                i = partie.getJoueurs().indexOf(curJoueur);
            }
        }


        //Si le joueur == null après la boucle, c'est qu'il ne fait pas parti de la partie
        if(joueur == null)
            throw new JoueurPasInviteException();

        if(joueurDejaPresent)
            throw new JoueurDejaPresentException();


        // ============== Vérifier si le joueur n'est pas dans une salle, alors on change de joueur
        // courant. Sinon, il à le droit de supposer/accuser


        //Sinon, on peut changer sa position et l'actualiser dans la partie
        joueur.setPositionCourante(point);
        joueur.setPiece(piece);
        partie.updateJoueur(joueur, i);
        partieRepository.save(partie);

        return joueur;
    }

    /**
     * La fonction supposer va permettre à un joueur de proposer 3 cartes qu'il pense être dans
     * la solution du jeu. Ces 3 cartes vont être ajouté dans l'hypothèse courante qui sera par la
     * suite utilisé pour que les joueurs revèle leur carte.
     *
     * @param lieu
     * @param arme
     * @param coupable
     * @param idPartie
     * @param partieRepository
     * @return
     */
    public Partie supposer(CarteDTO lieu, CarteDTO arme, CarteDTO coupable, String idPartie, PartieRepository partieRepository){

        /*
            Tester la position du joueur pour voir si il est possible de supposer
         */


        Partie partie = partieRepository.findByIdPartie(idPartie);
        ArrayList<Carte> hypCourante = new ArrayList<Carte>();
        Carte carteLieu = new Carte(lieu.getType(),lieu.getNom(),lieu.getDescriptif());
        Carte carteArme = new Carte(arme.getType(),arme.getNom(),arme.getDescriptif());
        Carte carteCoupable = new Carte(coupable.getType(),coupable.getNom(),coupable.getDescriptif());

        hypCourante.add(carteLieu);
        hypCourante.add(carteArme);
        hypCourante.add(carteCoupable);
        partie.setHypotheseCourante(hypCourante);
        partieRepository.save(partie);
        return partie;
    }

    /**
     * La fonction accuser permet de récurer l'accusation finale d'un joueur. Celui-ci va proposer 3 cartes qui doit
     * être identique à la solution du jeu. Si c'est identique le joueur à gagné sinon il a perdu.
     *
     * @param lieu
     * @param arme
     * @param coupable
     * @param idPartie
     * @param partieRepository
     * @return
     */
    public boolean accuser(CarteDTO lieu, CarteDTO arme, CarteDTO coupable, String idPartie, PartieRepository partieRepository){
        /*
               Tester la position du joueur pour voir si il est possible de accuser
         */

        Partie partie = partieRepository.findByIdPartie(idPartie);
        Joueur joueur = partie.getUnJoueur(partie.getJoueurCourant());
        int indexJoueur = partie.getJoueurs().indexOf(joueur);
        ArrayList<String> hypo = new ArrayList<String>();
        boolean res = false;
        hypo.add(lieu.getNom());
        hypo.add(arme.getNom());
        hypo.add(coupable.getNom());

        List<Carte> solution = partie.getSolution();
        ArrayList<String> sol = new ArrayList<String>();
        for (Carte carte : solution) {
            String carteNom = carte.getNom();
            sol.add(carteNom);
        }

        HashSet<String> hashHypo = new HashSet<>(hypo);
        HashSet<String> hashSol = new HashSet<>(sol);

        if(hashHypo.equals(hashSol)){
            res = true;
            joueur.setStatutJoueur("gagne");
        }
        else{
            joueur.setStatutJoueur("perdu");
            partie.retirerPersoJoue(joueur.getPerso().getNom());
        }

        partie.updateJoueur(joueur,indexJoueur);
        partieRepository.save(partie);

        return res;
    }


    public List<Carte> getCartesJeu(String idJoueur, String idPartie, PartieRepository partieRepository) throws PartieNonExistanteException, PartiePasEnCoursException, JoueurPasInviteException {

        //On cherche la partie et on vérifie qu'elle existe bien
        Partie partie = partieRepository.findByIdPartie(idPartie);


        if(partie == null)
            throw new PartieNonExistanteException();

        if( !partie.getStatut().equals("en cours") )
            throw new PartiePasEnCoursException();

        if( !partie.getConnectes().contains(idJoueur) )
            throw new JoueurPasInviteException();


        return fabriqueCarte.getCartesJeu();
    }

    public List<List<CarteDTO>> consulterCarnet(String idJoueur, String idPartie, PartieRepository partieRepository,UtilisateurRepository utilisateurRepository) throws PartiePasEnCoursException , PartieNonExistanteException, UtilisateurNonExistantException {

        Partie partieEnCours = partieRepository.findByIdPartie(idPartie);
        Utilisateur util = utilisateurRepository.findByNom(idJoueur);

        if(util == null){
            throw new UtilisateurNonExistantException();
        }

        if(partieEnCours == null){
            throw new PartieNonExistanteException();
        }

        if(!partieEnCours.getStatut().equals("en cours")) {
            throw new PartiePasEnCoursException();
        }
        System.out.println(partieEnCours.getStatut());
        System.out.println(util.getNom());

        Stream<List<List<Carte>>> blocNote = partieEnCours.getJoueurs().stream()
                .filter(x -> x.getUtil().equals(util.getNom()))
                .map(x -> x.getBlocnote());

        System.out.println(blocNote);

        CarteDTO carteDTO = new CarteDTO();

        return carteDTO.listeListetoCarte(blocNote.collect(Collectors.toList()).get(0));

    }

    public String quitterPartie(String utilJoueur, String idPartie, PartieRepository partieRepository)throws PartieNonExistanteException , JoueursPasConnecteException{

        Partie partieAquitter = partieRepository.findByIdPartie(idPartie);
        if(partieAquitter == null){
            throw new PartieNonExistanteException();
        }

        if(!partieAquitter.getConnectes().contains(utilJoueur) && !partieAquitter.getMdj().equals(utilJoueur)){
            System.out.println(partieAquitter.getConnectes().contains(utilJoueur));
            throw new JoueursPasConnecteException();
        }

        System.out.println(partieAquitter.getMdj());


        List<Joueur> joueurs = partieAquitter.getJoueurs();

        Joueur monJoueur = null;

        Iterator<Joueur> iterator = joueurs.iterator();
        while ( iterator.hasNext() ) {
            Joueur j = iterator.next();
            if (j.getUtil().equals(utilJoueur)) {
                System.out.println("test" + j.getUtil());
                monJoueur = j;
                iterator.remove();
            }
        }


        int nombreAleatoire = 0 + (int)(Math.random() * ((partieAquitter.getJoueurs().size() - 0)));

        if(utilJoueur.equals(partieAquitter.getMdj())){
            partieAquitter.setMdj(partieAquitter.getJoueurs().get(nombreAleatoire).getUtil());
            System.out.println("new2" + partieAquitter.getMdj());
        }

        if(utilJoueur.equals(partieAquitter.getJoueurCourant())){
            partieAquitter.setJoueurCourant(partieAquitter.getJoueurs().get(nombreAleatoire).getUtil());
            //partieAquitter.changerJoueurCourant();
        }

        partieAquitter.getConnectes().remove(utilJoueur);
        if(monJoueur != null)
            partieAquitter.retirerPersoJoue(monJoueur.getPerso().getNom());
        partieRepository.save(partieAquitter);

        return "quitterOK";


    }



    public Map<Joueur, Carte> piocherCarteIndice(String idJoueur, String idPartie, PartieRepository partieRepository) throws PasTonTourException, PartiePasEnCoursException, PartieNonExistanteException{

        Partie partie = partieRepository.findByIdPartie(idPartie);

        if(partie == null)
            throw new PartieNonExistanteException();

        if( !partie.getStatut().equals("en cours") )
            throw new PartiePasEnCoursException();

        if( !partie.getJoueurCourant().equals(idJoueur) )
            throw new PasTonTourException();

        //On récupère la première carte de la pioche, et on met à jour la pioche
        Carte carteInd = partie.getPioche().get(0);
        partie.majPioche();
        partieRepository.save(partie);

        //On appelle la méthode revelerCarte
        Carte carteAReveler = fabriqueCarte.getCarteJeuByNom(carteInd.getNom());
        List<Carte> listeCarteAReveler = new ArrayList<Carte>();
        listeCarteAReveler .add(carteAReveler);

        Map<Joueur, Carte> quiDoisReveler = new HashMap<>();
        Joueur joueurRevele = null;
        try {
            joueurRevele = quiDoisReveler(idPartie, idJoueur, listeCarteAReveler, partieRepository);
            quiDoisReveler.put(joueurRevele, carteAReveler);
            return quiDoisReveler;

        } catch (PersonneALaCarteException e) {
            quiDoisReveler.put(joueurRevele, carteAReveler);
            return quiDoisReveler;
        }

    }



    public String remplirCarnet(List<List<CarteDTO>> cartes, String idPartie,String idJoueur,PartieRepository partieRepository ,UtilisateurRepository utilisateurRepository) throws PartieNonExistanteException , UtilisateurNonExistantException ,JoueursPasConnecteException{

        CarteDTO carteDTO = new CarteDTO();
        List<List<Carte>> carnetAremplir = carteDTO.listeListeDTOtoCarte(cartes);

        Partie partieAremplir = partieRepository.findByIdPartie(idPartie);
        Utilisateur utilConsultCarnet = utilisateurRepository.findByNom(idJoueur);

        if(partieAremplir == null){
            throw new PartieNonExistanteException();
        }

        if(utilConsultCarnet == null){
            throw new UtilisateurNonExistantException();
        }


        Supplier<Stream<Joueur>> joueur = () -> partieAremplir.getJoueurs().stream()
                    .filter(x -> x.getUtil().equals(utilConsultCarnet.getNom()));


        System.out.println(joueur.get().findAny().get());
        joueur.get().findAny().get().setBlocnote(carnetAremplir);
        partieRepository.save(partieAremplir);

        return "remplissage_carnet_ok";
    }

    public Joueur prendrePassage(PointDTO destination,String idPartie,String idJoueur ,String piece,PartieRepository partieRepository) throws PartieNonExistanteException, PartiePasEnCoursException {
        Partie partie = partieRepository.findByIdPartie(idPartie);

        if(partie == null)
            throw new PartieNonExistanteException();

        if( !partie.getStatut().equals("en cours") )
            throw new PartiePasEnCoursException();


        //nouvelle salle a rejoindre
        Point pointSalle = new Point(destination.getX(), destination.getY());

        Supplier<Stream<Joueur>> joueur = () -> partie.getJoueurs().stream()
                .filter(x -> x.getUtil().equals(idJoueur));


        joueur.get().findAny().get().setPiece(piece);
        joueur.get().findAny().get().setPositionCourante(pointSalle);

        partieRepository.save(partie);
        //joueur.findFirst().get().setPositionCourante(pointSalle);

        return joueur.get().findAny().get();
    }



    public void terminerPartie(String idPartie, PartieRepository partieRepository) throws PartieNonExistanteException, PartiePasEnCoursException {

        Partie partie = partieRepository.findByIdPartie(idPartie);

        if(partie == null)
            throw new PartieNonExistanteException();

        if( !partie.getStatut().equals("en cours") )
            throw new PartiePasEnCoursException();

        partie.setStatut("termine");
        partieRepository.save(partie);

    }



    public Joueur terminerTour(String idPartie, String idJoueur, PartieRepository partieRepository) throws PartieNonExistanteException, PartiePasEnCoursException, PasTonTourException {

        Partie partie = partieRepository.findByIdPartie(idPartie);
        String joueurCourant  = partie.getJoueurCourant();

        if(partie == null)
            throw new PartieNonExistanteException();

        if( !partie.getStatut().equals("en cours") )
            throw new PartiePasEnCoursException();

        if( !joueurCourant.equals(idJoueur) )
            throw new PasTonTourException();

        partie.changerJoueurCourant();
        partieRepository.save(partie);

        System.out.println("joueurCourant = " + partie.getJoueurCourant());

        return partie.getLeJoueurCourant();

    }


    public boolean isJoueurChangePiece(String idPartie, String idJoueur, String piece, PartieRepository partieRepository) throws PartieNonExistanteException {

        Partie partie = partieRepository.findByIdPartie(idPartie);

        if(partie == null)
            throw new PartieNonExistanteException();

        Joueur joueur = partie.getUnJoueur(idJoueur);

        //Si mon joueur existe, si le joueur se situe dans une pièce et si cette pièce est différente de cette donnée en paramètre
        if(joueur != null && !joueur.getPiece().equals("") && !joueur.getPiece().equals(piece) )
            //alors le joueur a prit un passage
            return true;

        //sinon, alors soit il rentre dans une pièce, ou il reste dans la même pièce.
        return false;
    }
}
