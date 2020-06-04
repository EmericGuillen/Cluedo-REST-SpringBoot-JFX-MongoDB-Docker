package modele;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import controleur.Controleur;
import modele.DTO.*;
import modele.exceptions.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.io.Reader;
import java.io.StringReader;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

public class ClientProxy implements ClientInterface {

    private static final String URI_SERVICE="http://localhost:8080/cluedo";

    private static final String INSCRIPTION = "/inscription";
    private static final String CONNEXION = "/connexion";
    private static final String DECONNEXION = "/deconnexion";
    private static final String DESABONNEMENT = "/desabonnement";
    private static final String USER = "/user";
    private static final String CREE_PARTIE = "/creePartie";
    private static final String REJOINT_PARTIE = "/rejointPartie";
    private static final String CONSULTE_PARTIE = "/consulteInvitation";
    private static final String LANCE_PARTIE = "/lancePartie";
    private static final String ACCEPT_SAUVEGARDE = "/acceptSauvegarde";
    private static final String SAUVEGARDE = "/sauvegarde";
    private static final String RESTAURE_PARTIE = "/restaurePartie";
    private static final String JOUE = "/joue";
    private static final String JOUEUR = "/joueur";
    private static final String REVELE_CARTE = "/reveleCarte";
    private static final String SUPPOSE = "/suppose";
    private static final String ACCUSE = "/accuse";
    private static final String LANCE_DES = "/lanceDes";
    private static final String DEPLACE_PION = "/deplacePion";
    private static final String PREND_PASSAGE = "/prendPassage";
    private static final String PIOCHE_CARTE_INDICE = "/piocheCarteIndice";
    private static final String JOUE_CARTE_INDICE = "/joueCarteIndice";
    private static final String CONSULTE_CARNET = "/consulteCarnet";
    private static final String REMPLIR_CARNET = "/remplirCarnet";
    private static final String CONSULTE_CARTES = "/consulteCartes";
    private static final String CONSULTE_CARTES_JEU = "/consulteCartesJeu";
    private static final String TERMINE_PARTIE = "/terminePartie";
    private static final String QUITTE_PARTIE = "/quitterPartie";
    private static final String UTILISATEURS_CONNECT = "/recupUtilisateursConnect";
    private static final String TERMINE_TOUR = "/termineTour";
    private static final String RECUPERE_DES = "/recupereDes";
    private static final String RECUPERE_SUPPOSITIONS = "/recupereSuppositions";
    private static final String RECUPERE_ACCUSATION = "/recupereAccusation";
    private static final String RECUPERE_LANCER_PARTIE = "/recupereLancerPartie";
    private static final String RECUPERE_REJOINT_PARTIE = "/recupereRejointPartie";
    private static final String RECUPERE_DEPLACEMENT = "/recupereDeplacement";
    private static final String RECUPERE_TERMINE_TOUR = "/recupererTerminerTour";
    private static final String RECUPERE_QUITTER_PARTIE = "/recupereQuitterPartie";
    private static final String RECUPERE_REVELE_CARTE = "/recupererRevelerCarte";
    private static final String RECUPERE_CARNET = "/recupererCarnet";
    private static final String RECUPERE_CARTE_INDICE = "/recupererPiocheCarteIndice";
    private static final String RECUPERE_SAUVEGARDE = "/recupererSauvegarde";
    private static final String RECUPERE_TERMINE_PARTIE = "/recupererTerminerPartie";


    private HttpClient httpClient = HttpClient.newHttpClient();
    private WebClient webClient = WebClient.create();
    private Controleur monControleur;

    private ObjectMapper objectMapper = new ObjectMapper();

    private List<UtilisateurDTO> listeJoueurs;
    public static final ClientInterface instance = new ClientProxy();

    //TODO
    public ClientProxy(){

    }
    @Override
    public void setMonControleur(Controleur monControleur) {
        this.monControleur = monControleur;
    }

    /**
     * La méthode inscription permet d'inscire un utilisateur
     * @param utilisateurDTO L'utilisateur qu'on souhaite inscrire
     * @return Un entier -1 si les mots de passe fournit ne sont pas identique, -2 si le pseudo est dejà pris
     * et un entier positif si tout va bien
     * @throws InscriptionImpossibleException
     */
    public int inscription(UtilisateurDTO utilisateurDTO) throws InscriptionImpossibleException{
        int res = -1;
        String json = null;
        try {
            json = objectMapper.writeValueAsString(utilisateurDTO);

/*
            Reconstruction d'un objet UtilisateurDTO à partir d'une String Json
            UtilisateurDTO utilisateurDTO = objectMapper.readerFor(UtilisateurDTO.class).readValue(json);

            Reconstruction d'une liste d'UtilisateurDTO à partir d'une String Json
            objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, UtilisateurDTO.class));
*/
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URI_SERVICE+INSCRIPTION))
                    .header("Content-type","application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json)).build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            switch (response.statusCode()){
                case 201:
                    String[] location =  response.headers().firstValue("Location").get().split("/");
                    res = Integer.parseInt(location[location.length - 1]);
                    break;
                case 400: //Requete incorrect
                    //TODO
                    break;
                case 409: //conflict
                    throw new InscriptionImpossibleException();

            }

            return res;


        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new InscriptionImpossibleException();
    }

    /**
     * La méthode connexion permet de se connecter à a plateforme de jeu
     * @param utilisateurDTO L'utilisateur qui demande la connexion
     * @return Un token unique associé à l'utilisateur
     * @throws ConnexionImpossibleException
     */
    @Override
    public String connexion(UtilisateurDTO utilisateurDTO) throws ConnexionImpossibleException{
        String res = "";
        String json = null;
        try {
            json = objectMapper.writeValueAsString(utilisateurDTO);
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URI_SERVICE+CONNEXION))
                    .header("Content-type","application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json)).build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            switch (response.statusCode()){
                case 200:
                    res = response.headers().allValues("authorization").get(0);
                    break;
                case 404: //Si l'utilisateur passé dans le body n'est pas retrouvé
                    res = "";
                    break;
                case 401: //Si l'utilisateur est déjà connecté
                    res = null;
                    break;
            }

            return res;

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new ConnexionImpossibleException();
    }

    /**
     * La méthode deconnexion permet de se connecter de la platerforme de jeu
     * @param utilisateurDTO L'utilisateur qui souhaite se déconnecter
     * @throws DeconnexionImpossibleException
     */
    @Override
    public void deconnexion(UtilisateurDTO utilisateurDTO, String token) throws DeconnexionImpossibleException {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URI_SERVICE+DECONNEXION+"?login="+utilisateurDTO.getNom()))
                    .setHeader("Content-type","application/json").setHeader("Authorization",token)
                    .GET().build();


            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200){
                utilisateurDTO.setToken("");
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new DeconnexionImpossibleException();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new DeconnexionImpossibleException();
        } catch (IOException e) {
            e.printStackTrace();
            throw new DeconnexionImpossibleException();
        }

    }

    /**
     * La méthode userGet permet de récuperer toutes les informations d'un utilisateur
     * @param utilisateurDTO L'utilisateur duquel on souhaite récupérer les informations
     * @return Un UtilisateurDTO qui contient toutes les données de l'utilisateur en question
     * @throws RecupInfoUtilisateurImpossibleException
     */
    @Override
    public UtilisateurDTO userGet(UtilisateurDTO utilisateurDTO, String token) throws RecupInfoUtilisateurImpossibleException {
        UtilisateurDTO res = new UtilisateurDTO();
        String json = null;

        try {
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URI_SERVICE+USER+"/"+utilisateurDTO.getNom()))
                    .setHeader("Content-type","application/json")
                    .setHeader("Authorization",token)
                    .GET().build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            switch (response.statusCode()){
                case 200:
                    Reader reader = new StringReader(response.body());
                    res = objectMapper.readValue(reader, UtilisateurDTO.class);
                    break;
                case 404: //si on essaye de voir les informations d'un autre utilisateur
                    //TODO
                    break;
            }

            return res;

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RecupInfoUtilisateurImpossibleException();

    }

    /**
     * La méthode userDelete permet de supprimer un utilisateur
     * @param utilisateurDTO L'utilisateur qu'on souhaite supprimer
     * @return Un boolean indiquant si l'utilisateur a bien été supprimé ou non
     * @throws SuppressionUtilisateurImpossibleException
     */
    @Override
    public boolean userDelete(UtilisateurDTO utilisateurDTO, String token) throws SuppressionUtilisateurImpossibleException {
        boolean res = false;
        String json = null;

        try {
            json = objectMapper.writeValueAsString(utilisateurDTO);
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URI_SERVICE+DESABONNEMENT))
                    .setHeader("Content-type","application/json")
                    .setHeader("Authorization",token)
                    .POST(HttpRequest.BodyPublishers.ofString(json)).build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            switch (response.statusCode()){
                case 200:
                    res = true;
                    break;
                case 404:
                    res = false;
                    break;
            }

            return res;

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new SuppressionUtilisateurImpossibleException();

    }



    /**
     * La méthode creerPartie permet de créer une nouvelle partie
     * @param partieDTO La partie à créer
     * @param token Le token du maitre du jeu
     * @return Un objet PartieDTO qui correpsond à la partie créer avec toutes les informations nécessaire dedans
     * @throws CreationPartieImpossibleException
     */
    @Override
    public PartieDTO creerPartie(PartieDTO partieDTO, String token) throws CreationPartieImpossibleException {
        PartieDTO res = new PartieDTO();
        String json = null;
        try {
            json = objectMapper.writeValueAsString(partieDTO);

            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URI_SERVICE+CREE_PARTIE))
                    .setHeader("Content-type","application/json")
                    .setHeader("Authorization",token)
                    .POST(HttpRequest.BodyPublishers.ofString(json)).build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            switch (response.statusCode()){
                case 201:
                    Reader reader = new StringReader(response.body());
                    res = objectMapper.readValue(reader, PartieDTO.class);
                    break;
                case 404:
                    //TODO
                    break;
            }

            return res;

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new CreationPartieImpossibleException();

    }

    /**
     * La méthode rejoindrePartie permet de rejoindre une partie
     * @param idPartie L'id de la partie qu'on souhaite rejoindre
     * @param pseudoJoueur Le pseudo du joueur qui souhaite rejoindre la partie
     * @throws RejoindrePartieImpossibleException
     */
    @Override
    public PartieDTO rejoindrePartie(String idPartie, String pseudoJoueur, String token) throws RejoindrePartieImpossibleException {
        String json = "";
        PartieDTO res = null;
        try {


            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URI_SERVICE+REJOINT_PARTIE+"?pseudo="+pseudoJoueur+"&idPartie="+idPartie))
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                    .setHeader("Content-type","application/json")
                    .setHeader("Authorization",token)
                    .build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            switch (response.statusCode()){
                case 200:
                    Reader reader = new StringReader(response.body());
                    res = objectMapper.readValue(reader, PartieDTO.class);
                    break;
                case 404:
                    //TODO
                    break;
                case 409:
                    //TODO
                    break;
            }
            return res;

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RejoindrePartieImpossibleException();

    }

    /**
     * La méthode lancerPartie permet de lancer une partie
     * @param idPartie L'id de la partie qu'on souhaite lancer
     * @return Un objet PartieDTO contenant la partie à jour
     * @throws LancePartieImpossibleException
     */
    @Override
    public PartieDTO lancerPartie(String idPartie,String pseudoMdj, String token) throws LancePartieImpossibleException {
        PartieDTO res = new PartieDTO();
        String json = "";

        try {

            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URI_SERVICE+LANCE_PARTIE+"/"+idPartie+"?pseudoMdj="+pseudoMdj))
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                    .setHeader("Content-type","application/json")
                    .setHeader("Authorization",token)
                    .build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            switch (response.statusCode()){
                case 200:
                    Reader reader = new StringReader(response.body());
                    res = objectMapper.readValue(reader, PartieDTO.class);
                    break;
                case 404:
                    //TODO
                    break;
                case 409:
                    //TODO
                    System.out.println("création partie impossible");
                    break;
            }

            return res;

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new LancePartieImpossibleException();

    }

    /**
     * La méthode accepteSauvegarde permet d'accepter ou non de sauvegarder la partie en cour
     * @param idPartie L'id de la partie à sauvegarder
     * @param idJoueur Le joueur voulant sauvegarder la partie ou non
     * @param accept La reponse du joueur, si oui ou non il accepte de sauvegarder la partie en cour
     * @throws AccepteSauvegardeImpossibleException
     */
    @Override
    public void accepteSauvegarde(String idPartie, int idJoueur, boolean accept, String token) throws AccepteSauvegardeImpossibleException {
        String json = null;

        try {

            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URI_SERVICE+ACCEPT_SAUVEGARDE
                    +"?idPartie="+idPartie+"&idJoueur="+idJoueur+"&accept="+accept))
                    .setHeader("Content-type","application/json")
                    .setHeader("Authorization",token)
                    .GET().build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200){
                System.out.println("Le joueur : "+idJoueur+" a bien accepté de sauvegarder la partie : "+idPartie);
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new AccepteSauvegardeImpossibleException();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new AccepteSauvegardeImpossibleException();
        } catch (IOException e) {
            e.printStackTrace();
            throw new AccepteSauvegardeImpossibleException();
        }


    }

    /**
     * La méthode sauvegardeGET permet récupérer les parties sauvegardées d'un utilisateur
     * @param idJoueur L'id du joueur qui fait la demande de sauvegarde
     * @param token Le token du joueur qui fait la demande
     * @throws DemandeSauvegardeImpossibleException
     */
    @Override
    public List<PartieDTO> sauvegardeGET(int idJoueur, String token) throws DemandeSauvegardeImpossibleException {
        String json = null;
        List<PartieDTO> res = new ArrayList<>();
        try {

            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URI_SERVICE+SAUVEGARDE
                    +"?idJoueur="+idJoueur))
                    .setHeader("Content-type","application/json")
                    .setHeader("Authorization",token)
                    .GET().build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());


            if(response.statusCode()==200){
                Reader reader = new StringReader(response.body());
                res = objectMapper.readValue(reader, objectMapper.getTypeFactory().constructCollectionType(List.class, PartieDTO.class));
            }

            return res;

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new DemandeSauvegardeImpossibleException();

    }

    /**
     * La méthode sauvegardePATCH permet de sauvegarder la partie en cour
     * @param partieDTO La partie à sauvegarder
     * @param token Le token du joueur qui sauvegarde la partie (le maitre du jeu)
     * @throws SauvegardeImpossibleException
     */
    @Override
    public void sauvegardePATCH(PartieDTO partieDTO, String token) throws SauvegardeImpossibleException {
        String json = null;

        try {
            json = objectMapper.writeValueAsString(partieDTO);

            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URI_SERVICE+SAUVEGARDE))
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                    .setHeader("Content-type","application/json")
                    .setHeader("Authorization",token)
                    .build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            switch (response.statusCode()){
                case 201:
                    System.out.println("partie "+partieDTO.getIdPartie()+" sauvegardée");
                    break;
                case 403:
                    //TODO
                    break;
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new SauvegardeImpossibleException();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new SauvegardeImpossibleException();
        } catch (IOException e) {
            e.printStackTrace();
            throw new SauvegardeImpossibleException();
        }

    }

    /**
     * La méthode RestaurePartie permet de restaurer une partie
     * @param idPartie L'id de la partie à restaurer
     * @return Un objet PartieDTO qui correspond à la partie que l'on veux restaurer
     * @throws RestaurePartieImpossibleException
     */
    @Override
    public PartieDTO restaurePartie(String idPartie, String pseudoMdj, String token) throws RestaurePartieImpossibleException {
        PartieDTO res = new PartieDTO();
        String json = "";

        try {

            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URI_SERVICE+RESTAURE_PARTIE+"?idPartie="+idPartie+"&pseudoMdj="+pseudoMdj))
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                    .setHeader("Content-type","application/json")
                    .setHeader("Authorization",token)
                    .build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            switch (response.statusCode()){
                case 200:
                    Reader reader = new StringReader(response.body());
                    res = objectMapper.readValue(reader, PartieDTO.class);
                    break;
                case 404:
                    //TODO
                    break;
            }
            return res;

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RestaurePartieImpossibleException();
    }

    /**
     * La méthode reveleCarte permet à un joueur de révéler un carte à un autre joueur
     * @param idPartie L'id de la partie un cour
     * @param idJoueur L'id du joueur qui veux révéler une carte
     * @param carteRevelee
     * @param aQuiReveler
     * @param token L'id de la carte en question
     * @return Un objet CarteDTO qui correspond à la carte qu'on va révéler
     * @throws ReveleCarteImpossibleException
     */
    @Override
    public boolean reveleCarte(String idPartie, String idJoueur, CarteDTO carteRevelee, List<JoueurDTO> aQuiReveler, String token) throws ReveleCarteImpossibleException {
        boolean res = false;
        String json = null;

        try {
            String nomCarteRevele = URLEncoder.encode(carteRevelee.getNom(), "UTF-8");

            json = objectMapper.writeValueAsString(aQuiReveler);
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URI_SERVICE+JOUE
                    +"/"+idPartie+JOUEUR+"/"+idJoueur+REVELE_CARTE+"?carteRevelee="+nomCarteRevele))
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                    .setHeader("Content-type","application/json")
                    .setHeader("Authorization",token)
                    .build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            System.out.println("status : "+response.statusCode());
            System.out.println("body : "+response.body());
            if(response.statusCode()==200){
                res = true;
            }
            else{
                res = false;
            }

            return res;

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new ReveleCarteImpossibleException();
    }

    /**
     * La méthode suppose permet d'emettre une supposition
     * @param idPartie L'id de la partie en cour
     * @param idJoueur L'id du joueur qui emet la supposition
     * @param suspect La carte suspect
     * @param arme La carte arme
     * @param lieu La carte lieu
     * @param token Le token de l'utilisateur
     * @throws SuppositionImpossibleException
     */
    @Override
    public boolean suppose(String idPartie, String idJoueur, CarteDTO suspect, CarteDTO arme, CarteDTO lieu, String token) throws SuppositionImpossibleException {
        String json = null;
        boolean res = false;
        CarteDTO params[] = new CarteDTO[3];
        params[0] = arme;
        params[1] = lieu;
        params[2] = suspect;
        try {
            json = objectMapper.writeValueAsString(params);
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URI_SERVICE+JOUE
                    +"/"+idPartie+JOUEUR+"/"+idJoueur+SUPPOSE))
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                    .setHeader("Content-type","application/json")
                    .setHeader("Authorization",token)
                    .build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            switch (response.statusCode()){
                case 200:
                    res = true;
                    break;
                case 403: //si on essaye de jouer pour un autre joueur
                    res = false;
                    break;
            }
            return res;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new SuppositionImpossibleException();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new SuppositionImpossibleException();
        } catch (IOException e) {
            e.printStackTrace();
            throw new SuppositionImpossibleException();
        }

    }

    /**
     * La méthode suppose permet de faire une accusation
     * @param idPartie L'id de la partie en cour
     * @param idJoueur L'id du joueur qui fait l'accusation
     * @param suspect La carte suspect
     * @param arme La carte arme
     * @param lieu La carte lieu
     * @param token Le token de l'utilisateur
     * @throws SuppositionImpossibleException
     */
    @Override
    public boolean accuse(String idPartie, String idJoueur, CarteDTO suspect, CarteDTO arme, CarteDTO lieu, String token) throws AccusationImpossibleException {
        String json = null;
        boolean res = false;
        CarteDTO params[] = new CarteDTO[3];
        params[0] = arme;
        params[1] = lieu;
        params[2] = suspect;
        try {
            json = objectMapper.writeValueAsString(params);
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URI_SERVICE+JOUE
                    +"/"+idPartie+JOUEUR+"/"+idJoueur+ACCUSE))
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                    .setHeader("Content-type","application/json")
                    .setHeader("Authorization",token)
                    .build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            switch (response.statusCode()){
                case 200:
                    res = true;
                    break;
                case 403: //si on essaye de jouer pour un autre joueur
                    res = false;
                    break;
            }
            return res;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new AccusationImpossibleException();
    }


    @Override
    public void recuperationDes(String token) throws InterruptedException {

        WebClient clientWebFlux = WebClient
                .builder()
                .baseUrl(URI_SERVICE+JOUE+RECUPERE_DES)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Authorization",token)
                .defaultUriVariables(Collections.singletonMap("url", URI_SERVICE+JOUE+RECUPERE_DES))
                                .build();

        Flux<FluxDTO> flux = clientWebFlux
                .get()
                .retrieve()
                .bodyToFlux(FluxDTO.class);
        Disposable disposable = flux.subscribe(
                des -> {
                    this.monControleur.majConsolePlateau(des);
                }
        );

        /**
        while (!disposable.isDisposed()){
            Thread.sleep(1000);
        }*/
    }

    @Override
    public void recuperationSuppositions(String token) throws InterruptedException {
        WebClient clientWebFlux = WebClient
                .builder()
                .baseUrl(URI_SERVICE+JOUE+RECUPERE_SUPPOSITIONS)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Authorization",token)
                .defaultUriVariables(Collections.singletonMap("url", URI_SERVICE+JOUE+RECUPERE_SUPPOSITIONS))
                .build();

        Flux<FluxDTO> flux = clientWebFlux
                .get()
                .retrieve()
                .bodyToFlux(FluxDTO.class);
        Disposable disposable = flux.subscribe(
                supposition -> {
                    this.monControleur.majConsolePlateau(supposition);
                }
        );
    }

    @Override
    public void recuperationAccusation(String token) throws InterruptedException {
        WebClient clientWebFlux = WebClient
                .builder()
                .baseUrl(URI_SERVICE+JOUE+RECUPERE_ACCUSATION)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Authorization",token)
                .defaultUriVariables(Collections.singletonMap("url", URI_SERVICE+JOUE+RECUPERE_ACCUSATION))
                .build();

        Flux<FluxDTO> flux = clientWebFlux
                .get()
                .retrieve()
                .bodyToFlux(FluxDTO.class);
        Disposable disposable = flux.subscribe(
                accusation -> {
                    this.monControleur.majConsolePlateau(accusation);
                }
        );
    }

    @Override
    public void recuperationLancerPartie(String token) throws InterruptedException {
        WebClient clientWebFlux = WebClient
                .builder()
                .baseUrl(URI_SERVICE+JOUE+RECUPERE_LANCER_PARTIE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Authorization",token)
                .defaultUriVariables(Collections.singletonMap("url", URI_SERVICE+JOUE+RECUPERE_LANCER_PARTIE))
                .build();

        Flux<FluxDTO> flux = clientWebFlux
                .get()
                .retrieve()
                .bodyToFlux(FluxDTO.class);
        Disposable disposable = flux.subscribe(
                lancerPartie -> {
                    this.monControleur.majConsolePlateau(lancerPartie);
                }
        );
    }

    @Override
    public void recuperationRejointPartie(String token) throws InterruptedException {
        WebClient clientWebFlux = WebClient
                .builder()
                .baseUrl(URI_SERVICE+JOUE+RECUPERE_REJOINT_PARTIE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Authorization",token)
                .defaultUriVariables(Collections.singletonMap("url", URI_SERVICE+JOUE+RECUPERE_REJOINT_PARTIE))
                .build();

        Flux<FluxDTO> flux = clientWebFlux
                .get()
                .retrieve()
                .bodyToFlux(FluxDTO.class);
        Disposable disposable = flux.subscribe(
                rejointPartie -> {
                    this.monControleur.majConsolePlateau(rejointPartie);
                }
        );
    }

    @Override
    public void recuperationDeplacement(String token) throws InterruptedException {
        WebClient clientWebFlux = WebClient
                .builder()
                .baseUrl(URI_SERVICE+JOUE+RECUPERE_DEPLACEMENT)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Authorization",token)
                .defaultUriVariables(Collections.singletonMap("url", URI_SERVICE+JOUE+RECUPERE_DEPLACEMENT))
                .build();

        Flux<FluxDTO> flux = clientWebFlux
                .get()
                .retrieve()
                .bodyToFlux(FluxDTO.class);
        Disposable disposable = flux.subscribe(
                deplacement -> {
                    this.monControleur.majConsolePlateau(deplacement);
                }
        );
    }

    @Override
    public void recuperationTermineTour(String token) throws InterruptedException {
        WebClient clientWebFlux = WebClient
                .builder()
                .baseUrl(URI_SERVICE+JOUE+RECUPERE_TERMINE_TOUR)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Authorization",token)
                .defaultUriVariables(Collections.singletonMap("url", URI_SERVICE+JOUE+RECUPERE_TERMINE_TOUR))
                .build();

        Flux<FluxDTO> flux = clientWebFlux
                .get()
                .retrieve()
                .bodyToFlux(FluxDTO.class);
        Disposable disposable = flux.subscribe(
                deplacement -> {
                    this.monControleur.majConsolePlateau(deplacement);
                }
        );
    }

    @Override
    public void recuperationQuitterPartie(String token) throws InterruptedException {
        WebClient clientWebFlux = WebClient
                .builder()
                .baseUrl(URI_SERVICE+JOUE+RECUPERE_QUITTER_PARTIE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Authorization",token)
                .defaultUriVariables(Collections.singletonMap("url", URI_SERVICE+JOUE+RECUPERE_QUITTER_PARTIE))
                .build();

        Flux<FluxDTO> flux = clientWebFlux
                .get()
                .retrieve()
                .bodyToFlux(FluxDTO.class);
        Disposable disposable = flux.subscribe(
                quittePartie -> {
                    this.monControleur.majConsolePlateau(quittePartie);
                }
        );
    }

    @Override
    public void recuperationReveleCarte(String token) throws InterruptedException {
        WebClient clientWebFlux = WebClient
                .builder()
                .baseUrl(URI_SERVICE+JOUE+RECUPERE_REVELE_CARTE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Authorization",token)
                .defaultUriVariables(Collections.singletonMap("url", URI_SERVICE+JOUE+RECUPERE_REVELE_CARTE))
                .build();

        Flux<FluxDTO> flux = clientWebFlux
                .get()
                .retrieve()
                .bodyToFlux(FluxDTO.class);
        Disposable disposable = flux.subscribe(
                reveleCarte -> {
                    this.monControleur.majConsolePlateau(reveleCarte);
                }
        );
    }

    @Override
    public void recuperationCarnet(String token) throws InterruptedException {
        WebClient clientWebFlux = WebClient
                .builder()
                .baseUrl(URI_SERVICE+JOUE+RECUPERE_CARNET)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Authorization",token)
                .defaultUriVariables(Collections.singletonMap("url", URI_SERVICE+JOUE+RECUPERE_CARNET))
                .build();

        Flux<FluxDTO> flux = clientWebFlux
                .get()
                .retrieve()
                .bodyToFlux(FluxDTO.class);
        Disposable disposable = flux.subscribe(
                carnet -> {
                    this.monControleur.majConsolePlateau(carnet);
                }
        );
    }

    @Override
    public void recuperationSauvegarde(String token) throws InterruptedException {
        WebClient clientWebFlux = WebClient
                .builder()
                .baseUrl(URI_SERVICE+JOUE+RECUPERE_SAUVEGARDE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Authorization",token)
                .defaultUriVariables(Collections.singletonMap("url", URI_SERVICE+JOUE+RECUPERE_SAUVEGARDE))
                .build();

        Flux<FluxDTO> flux = clientWebFlux
                .get()
                .retrieve()
                .bodyToFlux(FluxDTO.class);
        Disposable disposable = flux.subscribe(
                save -> {
                    this.monControleur.majConsolePlateau(save);
                }
        );
    }

    @Override
    public void recuperationTerminePartie(String token) throws InterruptedException {
        WebClient clientWebFlux = WebClient
                .builder()
                .baseUrl(URI_SERVICE+JOUE+RECUPERE_TERMINE_PARTIE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Authorization",token)
                .defaultUriVariables(Collections.singletonMap("url", URI_SERVICE+JOUE+RECUPERE_TERMINE_PARTIE))
                .build();

        Flux<FluxDTO> flux = clientWebFlux
                .get()
                .retrieve()
                .bodyToFlux(FluxDTO.class);
        Disposable disposable = flux.subscribe(
                termine -> {
                    this.monControleur.majConsolePlateau(termine);
                }
        );
    }

    @Override
    public void recuperationPiocheCarteIndice(String token) {
        WebClient clientWebFlux = WebClient
                .builder()
                .baseUrl(URI_SERVICE+JOUE+RECUPERE_CARTE_INDICE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Authorization",token)
                .defaultUriVariables(Collections.singletonMap("url", URI_SERVICE+JOUE+RECUPERE_CARTE_INDICE))
                .build();

        Flux<FluxDTO> flux = clientWebFlux
                .get()
                .retrieve()
                .bodyToFlux(FluxDTO.class);
        Disposable disposable = flux.subscribe(
                carte -> {
                    this.monControleur.majConsolePlateau(carte);
                }
        );
    }

    /**
     * La méthode lancesDes permet de lancer les dès
     * @param idPartie L'id de la partie en cour
     * @param idJoueur L'id du joueur qui lance les dès
     * @return Le résultat obtenu
     * @throws LanceDesImpossibleException
     */
    @Override
    public DesDTO lanceDes(String idPartie, String idJoueur, String token) throws LanceDesImpossibleException {
        DesDTO res = new DesDTO();
        String json = null;

        try {

            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URI_SERVICE+JOUE
                    +"/"+idPartie+JOUEUR+"/"+idJoueur+LANCE_DES))
                    .setHeader("Content-type","application/json")
                    .setHeader("Authorization",token)
                    .GET().build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());


            switch (response.statusCode()){
                case 200:
                    Reader reader = new StringReader(response.body());
                    //res = objectMapper.readValue(json, objectMapper.getTypeFactory().constructMapType(HashMap.class, String.class, DesDTO.class));
                    res = objectMapper.readValue(reader, DesDTO.class);
                    break;
                case 403: //si on essaye de jouer pour un autre joueur
                    //TODO
                    break;
            }

            return res;

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new LanceDesImpossibleException();
    }

    /**
     * La méthode deplacePion permet de déplacer un pion sur le plateau
     * @param idPartie L'id de la partie en cours
     * @param idJoueur L'id du joueur qui veux déplacer le pion
     * @param destination La destination du pion
     * @return Un objet JoueurDTO qui correspond au Joueur mis à jour
     * @throws DeplacePionImpossibleException
     */
    @Override
    public JoueurDTO deplacePion(String idPartie, String idJoueur, PointDTO destination, String piece, String token) throws DeplacePionImpossibleException {
        JoueurDTO res = new JoueurDTO();
        String json = "";

        try {
            json = objectMapper.writeValueAsString(destination);
            String pieceBis = URLEncoder.encode(piece, "UTF-8");

            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URI_SERVICE+JOUE
                    +"/"+idPartie+JOUEUR+"/"+idJoueur+DEPLACE_PION+"?piece="+pieceBis))
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                    .setHeader("Content-type","application/json")
                    .setHeader("Authorization",token)
                    .build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            switch (response.statusCode()){
                case 200:
                    Reader reader = new StringReader(response.body());
                    res = objectMapper.readValue(reader, JoueurDTO.class);
                    break;
                case 403:
                    //TODO
                    break;
            }
            return res;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new DeplacePionImpossibleException();
    }

    /**
     * La méthode prendPassage permet à un joueur de prendre un passage
     * @param idPartie L'id de la partie un cour
     * @param idJoueur L'id du joueur qui veux prendre le passage
     * @param passage Le passage qu'on veux prendre
     * @return Un objet List<List<String>> qui correspond au plateau à jour
     * @throws PrendPassageImpossibleException
     */
    @Override
    public List<List<String>> prendPassage(String idPartie, int idJoueur, PointDTO passage, String token) throws PrendPassageImpossibleException {
        List<List<String>> res = null;
        String json = null;

        try {
            json = objectMapper.writeValueAsString(passage);

            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URI_SERVICE+JOUE
                    +"/"+idPartie+JOUEUR+"/"+idJoueur+PREND_PASSAGE))
                    .setHeader("Content-type","application/json")
                    .setHeader("Authorization",token)
                    .PUT(HttpRequest.BodyPublishers.ofString(json)).build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            switch (response.statusCode()){
                case 200:
                    //TODO
                    res = null;
                    break;
                case 403:
                    //TODO
                    break;
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new PrendPassageImpossibleException();
    }

    /**
     * La méthode piocheCarteIndice permet à un joueur de piocher une carte indice
     * @param idPartie L'id de la partie en cour
     * @param idJoueur L'id du joueur qui veux piocher une carte indice
     * @return Un objet CarteDTO qui correspond à la carte indice pioché
     * @throws PiocheCarteIndiceImpossibleException
     */
    @Override
    public boolean piocheCarteIndice(String idPartie, String idJoueur, String token) throws PiocheCarteIndiceImpossibleException {
        boolean res = false;
        String json = "";
        try {

            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URI_SERVICE+JOUE
                    +"/"+idPartie+JOUEUR+"/"+idJoueur+PIOCHE_CARTE_INDICE))
                    .setHeader("Content-type","application/json")
                    .setHeader("Authorization",token)
                    .GET().build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            System.out.println(response.statusCode());
            switch (response.statusCode()){
                case 200:
                    res = true;
                    break;
                case 403:
                    res = false;
                    break;
                case 204:
                    res = false;
                    break;
            }

            return res;

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new PiocheCarteIndiceImpossibleException();
    }

    /**
     * La méthode joueCarteIndice permet à un joueur de jouer une carte indice
     * @param idPartie L'id de la partie en cour
     * @param idJoueur L'id du joueur qui veux jouer la carte indice
     * @return Un objet CarteDTO correpondant à la carte indice qu'on va jouer
     * @throws JoueCarteIndiceImpossibleException
     */
    @Override
    public CarteDTO joueCarteIndice(String idPartie, int idJoueur, String token) throws JoueCarteIndiceImpossibleException {
        CarteDTO res = null;
        String json = null;
        try {

            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URI_SERVICE+JOUE
                    +"/"+idPartie+JOUEUR+"/"+idJoueur+JOUE_CARTE_INDICE))
                    .setHeader("Content-type","application/json")
                    .setHeader("Authorization",token)
                    .PUT(HttpRequest.BodyPublishers.ofString(json)).build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            switch (response.statusCode()){
                case 200:
                    //TODO
                    res = null;
                    break;
                case 403: //si on essaye de jouer la carte d'un autre joueur ou de jouer à la place d'un autre joueur
                    //TODO
                    break;
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new JoueCarteIndiceImpossibleException();
    }

    /**
     * La méthode consulteCarnet permet de consulter son carnet
     * @param idPartie L'id de la partie en cour
     * @param idJoueur L'id du joueur qui veux consulter son carnet
     * @return Une liste de liste List<List<CarteDTO>> qui correspond au carnet du joueur en question
     * @throws ConsulteCarnetImpossibleException
     */
    @Override
    public void consulteCarnet(String idPartie, String idJoueur, String token) throws ConsulteCarnetImpossibleException {

        String json = "";

        try {

            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URI_SERVICE+JOUE
                    +"/"+idPartie+JOUEUR+"/"+idJoueur+CONSULTE_CARNET))
                    .setHeader("Content-type","application/json")
                    .setHeader("Authorization",token)
                    .GET().build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            switch (response.statusCode()){
                case 200:
                    break;
                case 403: //Si on essaye de voir le carnet d'un autre utilisateur
                    //TODO
                    break;
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new ConsulteCarnetImpossibleException();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new ConsulteCarnetImpossibleException();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ConsulteCarnetImpossibleException();
        }

    }

    /**
     * La méthode remplirCarnet permet de remplir son carnet
     * @param idPartie
     * @param idJoueur
     * @param cartes
     * @param token
     * @throws ConsulteCarnetImpossibleException
     */
    @Override
    public void remplirCarnet(String idPartie, String idJoueur, List<List<CarteDTO>> cartes, String token) throws ConsulteCarnetImpossibleException {

        String json = "";

        try {
            json = objectMapper.writeValueAsString(cartes);

            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URI_SERVICE+JOUE
                    +"/"+idPartie+JOUEUR+"/"+idJoueur+REMPLIR_CARNET))
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                    .setHeader("Content-type","application/json")
                    .setHeader("Authorization",token)
                    .build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            switch (response.statusCode()){
                case 200:
                    break;
                case 403: //Si on essaye de voir le carnet d'un autre utilisateur
                    //TODO
                    break;
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new ConsulteCarnetImpossibleException();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new ConsulteCarnetImpossibleException();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ConsulteCarnetImpossibleException();
        }
    }

    @Override
    public List<CarteDTO> consulteCartes(String idPartie, int idJoueur, String token) throws ConsulteCartesImpossibleException {
        return null;
    }

    /**
     * La méthode consulteCartesJeu permet à un joueur de consulter toutes les cartes du jeu
     * @param idPartie L'id de la partie en cour
     * @param idJoueur L'id du joueur qui veux consulter ses cartes
     * @return Un objet List<CarteDTO> qui contient toutes les cartes du jeu
     * @throws ConsulteCartesImpossibleException
     */
    @Override
    public List<CarteDTO> consulteCartesJeu(String idPartie, String idJoueur, String token) throws ConsulteCartesImpossibleException {
        List<CarteDTO> res = new ArrayList<>();
        String json = "";

        try {

            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URI_SERVICE+JOUE
                    +"/"+idPartie+JOUEUR+"/"+idJoueur+CONSULTE_CARTES_JEU))
                    .setHeader("Content-type","application/json")
                    .setHeader("Authorization",token)
                    .GET().build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            switch (response.statusCode()){
                case 200:
                    Reader reader = new StringReader(response.body());
                    res = objectMapper.readValue(reader, objectMapper.getTypeFactory().constructCollectionType(List.class, CarteDTO.class));
                    break;
                case 403:
                    //TODO
                    break;
            }
            return res;

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new ConsulteCartesImpossibleException();
    }

    /**
     * La méthode terminePartie permet de terminer une partie
     * @param idPartie L'id de la partie que l'on veux terminer
     * @throws TerminePartieImpossibleException
     */
    @Override
    public void terminePartie(String idPartie, String token) throws TerminePartieImpossibleException {
        String json = "";

        try {

            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URI_SERVICE+TERMINE_PARTIE+"?idPartie="+idPartie))
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                    .setHeader("Content-type","application/json")
                    .setHeader("Authorization",token)
                    .build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode()==200){
                System.out.println("TQT ça passe !");
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new TerminePartieImpossibleException();

    }

    /**
     * La méthode quitterPartie permet de quitter la partie en cour
     * @param idPartie L'id de la partie en cour
     * @param pseudoJoueur Le pseudo du joueur qui demande à quitter la partie
     * @return Le pseudo du joueur qui à quitté la partie
     * @throws QuitterPartieImpossibleException
     */
    @Override
    public boolean quitterPartie(String idPartie, String pseudoJoueur, String token) throws QuitterPartieImpossibleException {
        boolean res = false;
        String json = "";

        try {

            json = objectMapper.writeValueAsString(pseudoJoueur);

            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URI_SERVICE+QUITTE_PARTIE
                    +"/"+idPartie+"?idJoueur="+pseudoJoueur))
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                    .setHeader("Content-type","application/json")
                    .setHeader("Authorization",token)
                    .build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode()==200){
                res = true;
            }
            else{
                res = false;
            }

            return res;

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new QuitterPartieImpossibleException();

    }

    @Override
    public List<String> utilisateursConnect(String token) throws RecuperationUtilisateursImpossibleException {
        String json = null;
        List<String> res = new ArrayList<>();
        try {

            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URI_SERVICE+UTILISATEURS_CONNECT))
                    .setHeader("Content-type","application/json")
                    .setHeader("Authorization",token)
                    .GET().build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode()==200){
                res = Arrays.stream(response.body().substring(1,response.body().length()-1).split(","))
                        .map(t-> t.substring(1,t.length()-1))
                        .collect(Collectors.toList());
            }

            return res;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RecuperationUtilisateursImpossibleException();
    }

    @Override
    public List<PartieDTO> consulterInvitations(String nom, String token) throws RecuperationPartiesImpossibleException {
        String json = null;
        List<PartieDTO> res = new ArrayList<>();
        try {

            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URI_SERVICE+CONSULTE_PARTIE+"?pseudo="+nom))
                    .setHeader("Content-type","application/json")
                    .setHeader("Authorization",token)
                    .GET().build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode()==200){
                Reader reader = new StringReader(response.body());
                res = objectMapper.readValue(reader, objectMapper.getTypeFactory().constructCollectionType(List.class, PartieDTO.class));
            }

            return res;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RecuperationPartiesImpossibleException();
    }

    @Override
    public JoueurDTO termineTour(String idPartie, String idJoueur, String token) throws TermineTourImpossibleException {
        JoueurDTO res = new JoueurDTO();
        String json = "";

        try {
            json = objectMapper.writeValueAsString(json);

            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URI_SERVICE+JOUE
                    +"/"+idPartie+JOUEUR+"/"+idJoueur+TERMINE_TOUR))
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                    .setHeader("Content-type","application/json")
                    .setHeader("Authorization",token)
                    .build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            switch (response.statusCode()){
                case 200:
                    Reader reader = new StringReader(response.body());
                    res = objectMapper.readValue(reader, JoueurDTO.class);
                    break;
                case 404:
                    //TODO
                    break;
                case 409:
                    //TODO
                    break;
                case 412:
                    //TODO
                    break;
            }
            return res;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new TermineTourImpossibleException();
    }

    @Override
    public void reset() {
        HttpRequest request = HttpRequest.newBuilder(URI.create(URI_SERVICE)).DELETE().build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException  | InterruptedException e) {
            throw new RuntimeException("Erreur innatendue !");
        }

    }
}
