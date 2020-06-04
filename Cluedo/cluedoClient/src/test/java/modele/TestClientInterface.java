package modele;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.ObservableList;
import modele.DTO.PartieDTO;
import modele.DTO.UtilisateurDTO;
import modele.exceptions.*;
import org.junit.*;
import org.mockserver.junit.MockServerRule;
import org.mockserver.matchers.Times;
import org.mockserver.model.HttpResponse;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import static org.mockserver.model.HttpRequest.request;


public class TestClientInterface {

    private ClientInterface clientInterface;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this, 8080);

    @Before
    public void initialisation(){
        this.clientInterface = new ClientProxy();
    }

    @After
    public void reinitilizeWS(){

        mockServerRule.getClient().when(
                request()
                        .withMethod("DELETE")
                        .withPath("/cluedo"))
                .respond(HttpResponse.response().withStatusCode(200));

        this.clientInterface.reset();
    }

    @Test
    public void testInscriptionUtilisateurOK() throws InscriptionImpossibleException, JsonProcessingException {

        UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
        utilisateurDTO.setIdUtil(0);
        utilisateurDTO.setNom("Util");
        utilisateurDTO.setPassword("6789");
        utilisateurDTO.setConfirmPassword("6789");
        String json = objectMapper.writeValueAsString(utilisateurDTO);

        mockServerRule.getClient().when(
                request()
                        .withMethod("POST")
                        .withPath("/cluedo/inscription")
                        .withHeader("Content-Type","application/json")
                        .withBody(json), Times.exactly(1))
                .respond(HttpResponse.response().withStatusCode(201).withHeader("Location","/cluedo/inscription/1"));


        UtilisateurDTO utilisateurTest = new UtilisateurDTO();
        utilisateurTest.setNom("Util");
        utilisateurTest.setPassword("6789");
        utilisateurTest.setConfirmPassword("6789");
        int id = this.clientInterface.inscription(utilisateurTest);
        Assert.assertEquals(id,1);
    }


    @Test(expected = InscriptionImpossibleException.class)
    public void testInscriptionUtilisateurDejaInscritOK() throws InscriptionImpossibleException, JsonProcessingException {

        UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
        utilisateurDTO.setIdUtil(0);
        utilisateurDTO.setNom("Test");
        utilisateurDTO.setPassword("6789");
        utilisateurDTO.setConfirmPassword("6789");
        String json = objectMapper.writeValueAsString(utilisateurDTO);

        mockServerRule.getClient().when(
                request()
                        .withMethod("POST")
                        .withPath("/cluedo/inscription")
                        .withHeader("Content-Type","application/json")
                        .withBody(json), Times.exactly(1))
                .respond(HttpResponse.response().withStatusCode(201).withHeader("Location","/cluedo/inscription/-2"));


        mockServerRule.getClient().when(
                request()
                        .withMethod("POST")
                        .withPath("/cluedo/inscription")
                        .withHeader("Content-Type","application/json")
                        .withBody(json), Times.exactly(1))
                .respond(HttpResponse.response().withStatusCode(409));

        try {
            UtilisateurDTO utilisateurTest = new UtilisateurDTO();
            utilisateurTest.setNom("Test");
            utilisateurTest.setPassword("6789");
            utilisateurTest.setConfirmPassword("6789");
            this.clientInterface.inscription(utilisateurTest);
        }
        catch (InscriptionImpossibleException e) {
            Assert.fail();
        }

        UtilisateurDTO utilisateurTest = new UtilisateurDTO();
        utilisateurTest.setNom("Test");
        utilisateurTest.setPassword("6789");
        utilisateurTest.setConfirmPassword("6789");
        this.clientInterface.inscription(utilisateurTest);

    }


    @Test(expected = InscriptionImpossibleException.class)
    public void testInscriptionUtilisateurMdpDifferentsOK() throws InscriptionImpossibleException, JsonProcessingException {

        UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
        utilisateurDTO.setIdUtil(0);
        utilisateurDTO.setNom("a");
        utilisateurDTO.setPassword("6789");
        utilisateurDTO.setConfirmPassword("1234");
        String json = objectMapper.writeValueAsString(utilisateurDTO);

        mockServerRule.getClient().when(
                request()
                        .withMethod("POST")
                        .withPath("/cluedo/inscription")
                        .withHeader("Content-Type","application/json")
                        .withBody(json), Times.exactly(1))
                .respond(HttpResponse.response().withStatusCode(409));

        UtilisateurDTO utilisateurTest = new UtilisateurDTO();
        utilisateurTest.setNom("a");
        utilisateurTest.setPassword("6789");
        utilisateurTest.setConfirmPassword("1234");
        int id = this.clientInterface.inscription(utilisateurTest);
        Assert.assertEquals(id,-1);

    }

    @Test
    public void testConnexionOK() throws ConnexionImpossibleException, JsonProcessingException {
        UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
        utilisateurDTO.setIdUtil(0);
        utilisateurDTO.setNom("Util");
        utilisateurDTO.setPassword("6789");
        utilisateurDTO.setConfirmPassword("6789");
        String json = objectMapper.writeValueAsString(utilisateurDTO);

        mockServerRule.getClient().when(
                request()
                        .withMethod("POST")
                        .withPath("/cluedo/connexion")
                        .withHeader("Content-Type","application/json")
                        .withBody(json), Times.exactly(1))
                .respond(HttpResponse.response().withStatusCode(200).withHeader("Location","/cluedo/connexion/1").withHeader("authorization","123"));



        String token = this.clientInterface.connexion(utilisateurDTO);
        Assert.assertEquals(token,"123");
    }


    @Test
    public void testConnexionDejaConnecteOK() throws ConnexionImpossibleException, JsonProcessingException {
        UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
        utilisateurDTO.setIdUtil(0);
        utilisateurDTO.setNom("Util");
        utilisateurDTO.setPassword("6789");
        utilisateurDTO.setConfirmPassword("6789");
        String json = objectMapper.writeValueAsString(utilisateurDTO);

        mockServerRule.getClient().when(
                request()
                        .withMethod("POST")
                        .withPath("/cluedo/connexion")
                        .withHeader("Content-Type","application/json")
                        .withBody(json), Times.exactly(1))
                .respond(HttpResponse.response().withStatusCode(401));

        String token = this.clientInterface.connexion(utilisateurDTO);
        Assert.assertNull(token);
    }


    @Test
    public void testConnexionUtilisateurInconnuOK() throws ConnexionImpossibleException, JsonProcessingException {
        UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
        utilisateurDTO.setIdUtil(0);
        utilisateurDTO.setNom("aa");
        utilisateurDTO.setPassword("6789");
        utilisateurDTO.setConfirmPassword("6789");
        String json = objectMapper.writeValueAsString(utilisateurDTO);

        mockServerRule.getClient().when(
                request()
                        .withMethod("POST")
                        .withPath("/cluedo/connexion")
                        .withHeader("Content-Type","application/json")
                        .withBody(json), Times.exactly(1))
                .respond(HttpResponse.response().withStatusCode(404));

        String token = this.clientInterface.connexion(utilisateurDTO);
        Assert.assertEquals(token,"");
    }


    @Test
    public void testDeconnexionOK() {
        UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
        utilisateurDTO.setIdUtil(0);
        utilisateurDTO.setNom("Util");
        utilisateurDTO.setPassword("6789");
        utilisateurDTO.setConfirmPassword("6789");
        utilisateurDTO.setToken("123");

        mockServerRule.getClient().when(
                request()
                        .withMethod("GET")
                        .withPath("/cluedo/deconnexion?login="+utilisateurDTO.getNom())
                        .withHeader("Content-Type","application/json").withHeader("Authorization",utilisateurDTO.getToken())
                        , Times.exactly(1))
                .respond(HttpResponse.response().withStatusCode(200).withHeader("Location","/cluedo/deconnexion/1"));

        try {
            this.clientInterface.deconnexion(utilisateurDTO,utilisateurDTO.getToken());
        }
        catch (DeconnexionImpossibleException e) {
            Assert.fail();
        }

    }


    @Test
    public void testUserGetOK() throws RecupInfoUtilisateurImpossibleException {
        UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
        utilisateurDTO.setIdUtil(0);
        utilisateurDTO.setNom("Util");
        utilisateurDTO.setPassword("6789");
        utilisateurDTO.setConfirmPassword("6789");

        utilisateurDTO.setToken("123");

        mockServerRule.getClient().when(
                request()
                        .withMethod("GET")
                        .withPath("/cluedo/user/"+utilisateurDTO.getNom())
                        .withHeader("Content-Type","application/json").withHeader("Authorization",utilisateurDTO.getToken())
                        ,Times.exactly(1))
                .respond(HttpResponse.response().withStatusCode(200).withBody("{\"idUtil\":0,\"nom\":\"Util\",\"password\":\"6789\",\"confirmPassword\":\"null\"}"));


        UtilisateurDTO util = this.clientInterface.userGet(utilisateurDTO, utilisateurDTO.getToken());

        Assert.assertEquals(0,util.getIdUtil());
    }


    @Test
    public void testUserDeleteOK() throws SuppressionUtilisateurImpossibleException, JsonProcessingException {
        UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
        utilisateurDTO.setIdUtil(0);
        utilisateurDTO.setNom("Util");
        utilisateurDTO.setPassword("6789");
        utilisateurDTO.setConfirmPassword("6789");

        utilisateurDTO.setToken("123");

        String json = objectMapper.writeValueAsString(utilisateurDTO);

        mockServerRule.getClient().when(
                request()
                        .withMethod("POST")
                        .withPath("/cluedo/desabonnement")
                        .withBody(json)
                        .withHeader("Content-Type","application/json").withHeader("Authorization",utilisateurDTO.getToken())
                ,Times.exactly(1))
                .respond(HttpResponse.response().withStatusCode(200));


        boolean bool = this.clientInterface.userDelete(utilisateurDTO, utilisateurDTO.getToken());

        Assert.assertTrue(bool);
    }


    @Test
    public void testUserDeleteKO() throws SuppressionUtilisateurImpossibleException, JsonProcessingException {
        UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
        utilisateurDTO.setNom("ttt");
        utilisateurDTO.setPassword("6789");
        utilisateurDTO.setConfirmPassword("6789");
        utilisateurDTO.setToken("123456");


        String json = objectMapper.writeValueAsString(utilisateurDTO);

        mockServerRule.getClient().when(
                request()
                        .withMethod("POST")
                        .withPath("/cluedo/desabonnement")
                        .withBody(json)
                        .withHeader("Content-Type","application/json")
                        .withHeader("Authorization",utilisateurDTO.getToken())
                ,Times.exactly(1))
                .respond(HttpResponse.response().withStatusCode(404));


        boolean bool = this.clientInterface.userDelete(utilisateurDTO, utilisateurDTO.getToken());

        Assert.assertFalse(bool);
    }

    @Test
    public void testCreerPartieOK() throws JsonProcessingException, CreationPartieImpossibleException {
        UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
        utilisateurDTO.setIdUtil(0);
        utilisateurDTO.setNom("Util");
        utilisateurDTO.setPassword("6789");
        utilisateurDTO.setConfirmPassword("6789");
        utilisateurDTO.setToken("123");



        PartieDTO partieDTO = new PartieDTO();
        partieDTO.setMdj(utilisateurDTO.getNom());
        partieDTO.setInvites(List.of("c","a"));


        String json = objectMapper.writeValueAsString(partieDTO);

        mockServerRule.getClient().when(
                request()
                        .withMethod("POST")
                        .withPath("/cluedo/creePartie")
                        .withBody(json)
                        .withHeader("Content-Type","application/json")
                        .withHeader("Authorization",utilisateurDTO.getToken())
                        , Times.exactly(1))
                .respond(HttpResponse.response().withStatusCode(201).withBody("{\"idPartie\":\"1\",\"mdj\":\"Util\",\"joueurCourant\":null,\"statut\":\"attente\",\"pioche\":[],\"solution\":[],\"hypotheseCourante\":[],\"invites\":[\"c\",\"a\"],\"connectes\":[\"Util\"],\"joueurs\":[],\"persosJoues\":[]}").withHeader("location","/cluedo/creePartie"));


        PartieDTO a = this.clientInterface.creerPartie(partieDTO, utilisateurDTO.getToken());

        Assert.assertEquals(a.getIdPartie(),"1");
    }


    @Test
    public void testRejoindrePartieOK() throws JsonProcessingException, RejoindrePartieImpossibleException {
        UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
        utilisateurDTO.setIdUtil(0);
        utilisateurDTO.setNom("a");
        utilisateurDTO.setPassword("6789");
        utilisateurDTO.setConfirmPassword("6789");
        utilisateurDTO.setToken("123");



        PartieDTO partieDTO = new PartieDTO();
        String json = objectMapper.writeValueAsString(partieDTO);
        Reader reader = new StringReader("{\"idPartie\":\"22\",\"mdj\":\"c\",\"joueurCourant\":\"a\",\"statut\":\"attente\",\"pioche\":[{\"type\":\"ou\",\"nom\":\"Bibliothèque\",\"descriptif\":null,\"choix\":null},{\"type\":\"ou\",\"nom\":\"Bureau\",\"descriptif\":null,\"choix\":null},{\"type\":\"quoi\",\"nom\":\"Clé anglaise\",\"descriptif\":null,\"choix\":null},{\"type\":\"qui\",\"nom\":\"Monsieur Olive\",\"descriptif\":null,\"choix\":null},{\"type\":\"ou\",\"nom\":\"Salle de réception\",\"descriptif\":null,\"choix\":null},{\"type\":\"qui\",\"nom\":\"Docteur Orchidée\",\"descriptif\":null,\"choix\":null},{\"type\":\"quoi\",\"nom\":\"Révolver\",\"descriptif\":null,\"choix\":null},{\"type\":\"qui\",\"nom\":\"Madame Pervenche\",\"descriptif\":null,\"choix\":null},{\"type\":\"quoi\",\"nom\":\"Barre de fer\",\"descriptif\":null,\"choix\":null},{\"type\":\"qui\",\"nom\":\"Colonel Moutarde\",\"descriptif\":null,\"choix\":null},{\"type\":\"ou\",\"nom\":\"Salon\",\"descriptif\":null,\"choix\":null},{\"type\":\"quoi\",\"nom\":\"Corde\",\"descriptif\":null,\"choix\":null},{\"type\":\"quoi\",\"nom\":\"Chandelier\",\"descriptif\":null,\"choix\":null},{\"type\":\"ou\",\"nom\":\"Salle de billard\",\"descriptif\":null,\"choix\":null},{\"type\":\"quoi\",\"nom\":\"Poignard\",\"descriptif\":null,\"choix\":null},{\"type\":\"ou\",\"nom\":\"Entrée\",\"descriptif\":null,\"choix\":null},{\"type\":\"qui\",\"nom\":\"Mademoiselle Rose\",\"descriptif\":null,\"choix\":null},{\"type\":\"qui\",\"nom\":\"Professeur Violet\",\"descriptif\":null,\"choix\":null},{\"type\":\"ou\",\"nom\":\"Cuisine\",\"descriptif\":null,\"choix\":null},{\"type\":\"ou\",\"nom\":\"Salle à manger\",\"descriptif\":null,\"choix\":null},{\"type\":\"ou\",\"nom\":\"Jardin d'hiver\",\"descriptif\":null,\"choix\":null}],\"solution\":[{\"type\":\"arme\",\"nom\":\"Révolver\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Mademoiselle Rose\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Chambre\",\"descriptif\":null,\"choix\":null}],\"hypotheseCourante\":[{\"type\":\"lieu\",\"nom\":\"Bureau\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Révolver\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Professeur Violet\",\"descriptif\":null,\"choix\":null}],\"invites\":[\"a\",\"b\"],\"connectes\":[\"c\",\"a\"],\"joueurs\":[{\"util\":\"c\",\"piece\":\"Bureau\",\"perso\":{\"type\":\"perso\",\"nom\":\"Colonel Moutarde\",\"descriptif\":null,\"choix\":null},\"statutJoueur\":\"joue\",\"positionCourante\":{\"x\":21,\"y\":14},\"deck\":[{\"type\":\"arme\",\"nom\":\"Chandelier\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Madame Pervenche\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle de réception\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle de billard\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle à manger\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Bureau\",\"descriptif\":null,\"choix\":null}],\"blocnote\":[[{\"type\":\"arme\",\"nom\":\"Chandelier\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Madame Pervenche\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle de réception\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle de billard\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle à manger\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Bureau\",\"descriptif\":null,\"choix\":null}],[{\"type\":\"lieu\",\"nom\":\"Salon\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Professeur Violet\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Clé anglaise\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Monsieur Olive\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Entrée\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Docteur Orchidée\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Cuisine\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Poignard\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Barre de fer\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Révolver\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Mademoiselle Rose\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Chambre\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Corde\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Colonel Moutarde\",\"descriptif\":null,\"choix\":null}],[]]},{\"util\":\"a\",\"piece\":\"Bureau\",\"perso\":{\"type\":\"perso\",\"nom\":\"Professeur Violet\",\"descriptif\":null,\"choix\":null},\"statutJoueur\":\"joue\",\"positionCourante\":{\"x\":22,\"y\":14},\"deck\":[{\"type\":\"arme\",\"nom\":\"Corde\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salon\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Clé anglaise\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Entrée\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Cuisine\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Barre de fer\",\"descriptif\":null,\"choix\":null}],\"blocnote\":[[{\"type\":\"arme\",\"nom\":\"Corde\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salon\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Clé anglaise\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Entrée\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Cuisine\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Barre de fer\",\"descriptif\":null,\"choix\":null}],[{\"type\":\"arme\",\"nom\":\"Chandelier\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Colonel Moutarde\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Madame Pervenche\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Professeur Violet\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle de réception\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Monsieur Olive\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle de billard\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Docteur Orchidée\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle à manger\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Poignard\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Bureau\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Révolver\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Mademoiselle Rose\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Chambre\",\"descriptif\":null,\"choix\":null}],[]]},{\"util\":\"b\",\"piece\":\"\",\"perso\":{\"type\":\"perso\",\"nom\":\"Mademoiselle Rose\",\"descriptif\":null,\"choix\":null},\"statutJoueur\":\"joue\",\"positionCourante\":{\"x\":18,\"y\":12},\"deck\":[{\"type\":\"perso\",\"nom\":\"Colonel Moutarde\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Professeur Violet\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Monsieur Olive\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Docteur Orchidée\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Poignard\",\"descriptif\":null,\"choix\":null}],\"blocnote\":[[{\"type\":\"perso\",\"nom\":\"Colonel Moutarde\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Professeur Violet\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Monsieur Olive\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Docteur Orchidée\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Poignard\",\"descriptif\":null,\"choix\":null}],[{\"type\":\"arme\",\"nom\":\"Chandelier\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Corde\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Madame Pervenche\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salon\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle de réception\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Clé anglaise\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle de billard\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Entrée\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle à manger\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Cuisine\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Bureau\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Barre de fer\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Révolver\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Mademoiselle Rose\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Chambre\",\"descriptif\":null,\"choix\":null}],[]]}],\"persosJoues\":[\"Mademoiselle Rose\",\"Colonel Moutarde\",\"Professeur Violet\"]}");
        try {
            PartieDTO j = objectMapper.readValue(reader, PartieDTO.class);
            partieDTO = j;
        } catch (IOException e) {
            e.printStackTrace();
        }


        mockServerRule.getClient().when(
                request()
                        .withMethod("PATCH")
                        .withPath("/cluedo/rejointPartie")
                        .withBody(json)
                        .withHeader("Content-Type","application/json")
                        .withHeader("Authorization",utilisateurDTO.getToken())
                , Times.exactly(1))
                .respond(HttpResponse.response().withStatusCode(200).withBody("{\"idPartie\":\"22\",\"mdj\":\"c\",\"joueurCourant\":\"a\",\"statut\":\"attente\",\"pioche\":[{\"type\":\"ou\",\"nom\":\"Bibliothèque\",\"descriptif\":null,\"choix\":null},{\"type\":\"ou\",\"nom\":\"Bureau\",\"descriptif\":null,\"choix\":null},{\"type\":\"quoi\",\"nom\":\"Clé anglaise\",\"descriptif\":null,\"choix\":null},{\"type\":\"qui\",\"nom\":\"Monsieur Olive\",\"descriptif\":null,\"choix\":null},{\"type\":\"ou\",\"nom\":\"Salle de réception\",\"descriptif\":null,\"choix\":null},{\"type\":\"qui\",\"nom\":\"Docteur Orchidée\",\"descriptif\":null,\"choix\":null},{\"type\":\"quoi\",\"nom\":\"Révolver\",\"descriptif\":null,\"choix\":null},{\"type\":\"qui\",\"nom\":\"Madame Pervenche\",\"descriptif\":null,\"choix\":null},{\"type\":\"quoi\",\"nom\":\"Barre de fer\",\"descriptif\":null,\"choix\":null},{\"type\":\"qui\",\"nom\":\"Colonel Moutarde\",\"descriptif\":null,\"choix\":null},{\"type\":\"ou\",\"nom\":\"Salon\",\"descriptif\":null,\"choix\":null},{\"type\":\"quoi\",\"nom\":\"Corde\",\"descriptif\":null,\"choix\":null},{\"type\":\"quoi\",\"nom\":\"Chandelier\",\"descriptif\":null,\"choix\":null},{\"type\":\"ou\",\"nom\":\"Salle de billard\",\"descriptif\":null,\"choix\":null},{\"type\":\"quoi\",\"nom\":\"Poignard\",\"descriptif\":null,\"choix\":null},{\"type\":\"ou\",\"nom\":\"Entrée\",\"descriptif\":null,\"choix\":null},{\"type\":\"qui\",\"nom\":\"Mademoiselle Rose\",\"descriptif\":null,\"choix\":null},{\"type\":\"qui\",\"nom\":\"Professeur Violet\",\"descriptif\":null,\"choix\":null},{\"type\":\"ou\",\"nom\":\"Cuisine\",\"descriptif\":null,\"choix\":null},{\"type\":\"ou\",\"nom\":\"Salle à manger\",\"descriptif\":null,\"choix\":null},{\"type\":\"ou\",\"nom\":\"Jardin d'hiver\",\"descriptif\":null,\"choix\":null}],\"solution\":[{\"type\":\"arme\",\"nom\":\"Révolver\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Mademoiselle Rose\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Chambre\",\"descriptif\":null,\"choix\":null}],\"hypotheseCourante\":[{\"type\":\"lieu\",\"nom\":\"Bureau\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Révolver\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Professeur Violet\",\"descriptif\":null,\"choix\":null}],\"invites\":[\"a\",\"b\"],\"connectes\":[\"c\",\"a\"],\"joueurs\":[{\"util\":\"c\",\"piece\":\"Bureau\",\"perso\":{\"type\":\"perso\",\"nom\":\"Colonel Moutarde\",\"descriptif\":null,\"choix\":null},\"statutJoueur\":\"joue\",\"positionCourante\":{\"x\":21,\"y\":14},\"deck\":[{\"type\":\"arme\",\"nom\":\"Chandelier\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Madame Pervenche\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle de réception\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle de billard\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle à manger\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Bureau\",\"descriptif\":null,\"choix\":null}],\"blocnote\":[[{\"type\":\"arme\",\"nom\":\"Chandelier\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Madame Pervenche\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle de réception\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle de billard\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle à manger\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Bureau\",\"descriptif\":null,\"choix\":null}],[{\"type\":\"lieu\",\"nom\":\"Salon\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Professeur Violet\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Clé anglaise\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Monsieur Olive\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Entrée\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Docteur Orchidée\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Cuisine\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Poignard\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Barre de fer\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Révolver\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Mademoiselle Rose\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Chambre\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Corde\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Colonel Moutarde\",\"descriptif\":null,\"choix\":null}],[]]},{\"util\":\"a\",\"piece\":\"Bureau\",\"perso\":{\"type\":\"perso\",\"nom\":\"Professeur Violet\",\"descriptif\":null,\"choix\":null},\"statutJoueur\":\"joue\",\"positionCourante\":{\"x\":22,\"y\":14},\"deck\":[{\"type\":\"arme\",\"nom\":\"Corde\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salon\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Clé anglaise\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Entrée\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Cuisine\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Barre de fer\",\"descriptif\":null,\"choix\":null}],\"blocnote\":[[{\"type\":\"arme\",\"nom\":\"Corde\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salon\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Clé anglaise\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Entrée\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Cuisine\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Barre de fer\",\"descriptif\":null,\"choix\":null}],[{\"type\":\"arme\",\"nom\":\"Chandelier\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Colonel Moutarde\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Madame Pervenche\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Professeur Violet\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle de réception\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Monsieur Olive\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle de billard\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Docteur Orchidée\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle à manger\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Poignard\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Bureau\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Révolver\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Mademoiselle Rose\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Chambre\",\"descriptif\":null,\"choix\":null}],[]]},{\"util\":\"b\",\"piece\":\"\",\"perso\":{\"type\":\"perso\",\"nom\":\"Mademoiselle Rose\",\"descriptif\":null,\"choix\":null},\"statutJoueur\":\"joue\",\"positionCourante\":{\"x\":18,\"y\":12},\"deck\":[{\"type\":\"perso\",\"nom\":\"Colonel Moutarde\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Professeur Violet\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Monsieur Olive\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Docteur Orchidée\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Poignard\",\"descriptif\":null,\"choix\":null}],\"blocnote\":[[{\"type\":\"perso\",\"nom\":\"Colonel Moutarde\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Professeur Violet\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Monsieur Olive\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Docteur Orchidée\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Poignard\",\"descriptif\":null,\"choix\":null}],[{\"type\":\"arme\",\"nom\":\"Chandelier\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Corde\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Madame Pervenche\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salon\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle de réception\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Clé anglaise\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle de billard\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Entrée\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle à manger\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Cuisine\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Bureau\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Barre de fer\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Révolver\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Mademoiselle Rose\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Chambre\",\"descriptif\":null,\"choix\":null}],[]]}],\"persosJoues\":[\"Mademoiselle Rose\",\"Colonel Moutarde\",\"Professeur Violet\"]}")
                        .withHeader("location","/cluedo/rejointPartie?pseudo=a&idPartie=22"));

        PartieDTO a = this.clientInterface.rejoindrePartie(partieDTO.getIdPartie(),utilisateurDTO.getNom(),utilisateurDTO.getToken());

        Assert.assertEquals(a.getIdPartie(),partieDTO.getIdPartie());
    }

    @Test
    public void testLancerPartieOK() throws JsonProcessingException, RejoindrePartieImpossibleException {
        UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
        utilisateurDTO.setIdUtil(0);
        utilisateurDTO.setNom("c");
        utilisateurDTO.setPassword("6789");
        utilisateurDTO.setConfirmPassword("6789");
        utilisateurDTO.setToken("123");



        PartieDTO partieDTO = new PartieDTO();
        String json = objectMapper.writeValueAsString(partieDTO);
        Reader reader = new StringReader("{\"idPartie\":\"22\",\"mdj\":\"c\",\"joueurCourant\":\"a\",\"statut\":\"en cours\",\"pioche\":[{\"type\":\"ou\",\"nom\":\"Bibliothèque\",\"descriptif\":null,\"choix\":null},{\"type\":\"ou\",\"nom\":\"Bureau\",\"descriptif\":null,\"choix\":null},{\"type\":\"quoi\",\"nom\":\"Clé anglaise\",\"descriptif\":null,\"choix\":null},{\"type\":\"qui\",\"nom\":\"Monsieur Olive\",\"descriptif\":null,\"choix\":null},{\"type\":\"ou\",\"nom\":\"Salle de réception\",\"descriptif\":null,\"choix\":null},{\"type\":\"qui\",\"nom\":\"Docteur Orchidée\",\"descriptif\":null,\"choix\":null},{\"type\":\"quoi\",\"nom\":\"Révolver\",\"descriptif\":null,\"choix\":null},{\"type\":\"qui\",\"nom\":\"Madame Pervenche\",\"descriptif\":null,\"choix\":null},{\"type\":\"quoi\",\"nom\":\"Barre de fer\",\"descriptif\":null,\"choix\":null},{\"type\":\"qui\",\"nom\":\"Colonel Moutarde\",\"descriptif\":null,\"choix\":null},{\"type\":\"ou\",\"nom\":\"Salon\",\"descriptif\":null,\"choix\":null},{\"type\":\"quoi\",\"nom\":\"Corde\",\"descriptif\":null,\"choix\":null},{\"type\":\"quoi\",\"nom\":\"Chandelier\",\"descriptif\":null,\"choix\":null},{\"type\":\"ou\",\"nom\":\"Salle de billard\",\"descriptif\":null,\"choix\":null},{\"type\":\"quoi\",\"nom\":\"Poignard\",\"descriptif\":null,\"choix\":null},{\"type\":\"ou\",\"nom\":\"Entrée\",\"descriptif\":null,\"choix\":null},{\"type\":\"qui\",\"nom\":\"Mademoiselle Rose\",\"descriptif\":null,\"choix\":null},{\"type\":\"qui\",\"nom\":\"Professeur Violet\",\"descriptif\":null,\"choix\":null},{\"type\":\"ou\",\"nom\":\"Cuisine\",\"descriptif\":null,\"choix\":null},{\"type\":\"ou\",\"nom\":\"Salle à manger\",\"descriptif\":null,\"choix\":null},{\"type\":\"ou\",\"nom\":\"Jardin d'hiver\",\"descriptif\":null,\"choix\":null}],\"solution\":[{\"type\":\"arme\",\"nom\":\"Révolver\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Mademoiselle Rose\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Chambre\",\"descriptif\":null,\"choix\":null}],\"hypotheseCourante\":[{\"type\":\"lieu\",\"nom\":\"Bureau\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Révolver\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Professeur Violet\",\"descriptif\":null,\"choix\":null}],\"invites\":[\"a\",\"b\"],\"connectes\":[\"c\",\"a\",\"b\"],\"joueurs\":[{\"util\":\"c\",\"piece\":\"Bureau\",\"perso\":{\"type\":\"perso\",\"nom\":\"Colonel Moutarde\",\"descriptif\":null,\"choix\":null},\"statutJoueur\":\"joue\",\"positionCourante\":{\"x\":21,\"y\":14},\"deck\":[{\"type\":\"arme\",\"nom\":\"Chandelier\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Madame Pervenche\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle de réception\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle de billard\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle à manger\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Bureau\",\"descriptif\":null,\"choix\":null}],\"blocnote\":[[{\"type\":\"arme\",\"nom\":\"Chandelier\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Madame Pervenche\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle de réception\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle de billard\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle à manger\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Bureau\",\"descriptif\":null,\"choix\":null}],[{\"type\":\"lieu\",\"nom\":\"Salon\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Professeur Violet\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Clé anglaise\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Monsieur Olive\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Entrée\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Docteur Orchidée\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Cuisine\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Poignard\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Barre de fer\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Révolver\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Mademoiselle Rose\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Chambre\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Corde\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Colonel Moutarde\",\"descriptif\":null,\"choix\":null}],[]]},{\"util\":\"a\",\"piece\":\"Bureau\",\"perso\":{\"type\":\"perso\",\"nom\":\"Professeur Violet\",\"descriptif\":null,\"choix\":null},\"statutJoueur\":\"joue\",\"positionCourante\":{\"x\":22,\"y\":14},\"deck\":[{\"type\":\"arme\",\"nom\":\"Corde\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salon\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Clé anglaise\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Entrée\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Cuisine\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Barre de fer\",\"descriptif\":null,\"choix\":null}],\"blocnote\":[[{\"type\":\"arme\",\"nom\":\"Corde\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salon\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Clé anglaise\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Entrée\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Cuisine\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Barre de fer\",\"descriptif\":null,\"choix\":null}],[{\"type\":\"arme\",\"nom\":\"Chandelier\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Colonel Moutarde\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Madame Pervenche\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Professeur Violet\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle de réception\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Monsieur Olive\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle de billard\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Docteur Orchidée\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle à manger\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Poignard\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Bureau\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Révolver\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Mademoiselle Rose\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Chambre\",\"descriptif\":null,\"choix\":null}],[]]},{\"util\":\"b\",\"piece\":\"\",\"perso\":{\"type\":\"perso\",\"nom\":\"Mademoiselle Rose\",\"descriptif\":null,\"choix\":null},\"statutJoueur\":\"joue\",\"positionCourante\":{\"x\":18,\"y\":12},\"deck\":[{\"type\":\"perso\",\"nom\":\"Colonel Moutarde\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Professeur Violet\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Monsieur Olive\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Docteur Orchidée\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Poignard\",\"descriptif\":null,\"choix\":null}],\"blocnote\":[[{\"type\":\"perso\",\"nom\":\"Colonel Moutarde\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Professeur Violet\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Monsieur Olive\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Docteur Orchidée\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Poignard\",\"descriptif\":null,\"choix\":null}],[{\"type\":\"arme\",\"nom\":\"Chandelier\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Corde\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Madame Pervenche\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salon\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle de réception\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Clé anglaise\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle de billard\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Entrée\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle à manger\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Cuisine\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Bureau\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Barre de fer\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Révolver\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Mademoiselle Rose\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Chambre\",\"descriptif\":null,\"choix\":null}],[]]}],\"persosJoues\":[\"Mademoiselle Rose\",\"Colonel Moutarde\",\"Professeur Violet\"]}");
        try {
            PartieDTO j = objectMapper.readValue(reader, PartieDTO.class);
            partieDTO = j;
        } catch (IOException e) {
            e.printStackTrace();
        }


        mockServerRule.getClient().when(
                request()
                        .withMethod("PATCH")
                        .withPath("/cluedo/rejointPartie")
                        .withBody(json)
                        .withHeader("Content-Type","application/json")
                        .withHeader("Authorization",utilisateurDTO.getToken())
                , Times.exactly(1))
                .respond(HttpResponse.response().withStatusCode(200).withBody("{\"idPartie\":\"22\",\"mdj\":\"c\",\"joueurCourant\":\"a\",\"statut\":\"en cours\",\"pioche\":[{\"type\":\"ou\",\"nom\":\"Bibliothèque\",\"descriptif\":null,\"choix\":null},{\"type\":\"ou\",\"nom\":\"Bureau\",\"descriptif\":null,\"choix\":null},{\"type\":\"quoi\",\"nom\":\"Clé anglaise\",\"descriptif\":null,\"choix\":null},{\"type\":\"qui\",\"nom\":\"Monsieur Olive\",\"descriptif\":null,\"choix\":null},{\"type\":\"ou\",\"nom\":\"Salle de réception\",\"descriptif\":null,\"choix\":null},{\"type\":\"qui\",\"nom\":\"Docteur Orchidée\",\"descriptif\":null,\"choix\":null},{\"type\":\"quoi\",\"nom\":\"Révolver\",\"descriptif\":null,\"choix\":null},{\"type\":\"qui\",\"nom\":\"Madame Pervenche\",\"descriptif\":null,\"choix\":null},{\"type\":\"quoi\",\"nom\":\"Barre de fer\",\"descriptif\":null,\"choix\":null},{\"type\":\"qui\",\"nom\":\"Colonel Moutarde\",\"descriptif\":null,\"choix\":null},{\"type\":\"ou\",\"nom\":\"Salon\",\"descriptif\":null,\"choix\":null},{\"type\":\"quoi\",\"nom\":\"Corde\",\"descriptif\":null,\"choix\":null},{\"type\":\"quoi\",\"nom\":\"Chandelier\",\"descriptif\":null,\"choix\":null},{\"type\":\"ou\",\"nom\":\"Salle de billard\",\"descriptif\":null,\"choix\":null},{\"type\":\"quoi\",\"nom\":\"Poignard\",\"descriptif\":null,\"choix\":null},{\"type\":\"ou\",\"nom\":\"Entrée\",\"descriptif\":null,\"choix\":null},{\"type\":\"qui\",\"nom\":\"Mademoiselle Rose\",\"descriptif\":null,\"choix\":null},{\"type\":\"qui\",\"nom\":\"Professeur Violet\",\"descriptif\":null,\"choix\":null},{\"type\":\"ou\",\"nom\":\"Cuisine\",\"descriptif\":null,\"choix\":null},{\"type\":\"ou\",\"nom\":\"Salle à manger\",\"descriptif\":null,\"choix\":null},{\"type\":\"ou\",\"nom\":\"Jardin d'hiver\",\"descriptif\":null,\"choix\":null}],\"solution\":[{\"type\":\"arme\",\"nom\":\"Révolver\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Mademoiselle Rose\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Chambre\",\"descriptif\":null,\"choix\":null}],\"hypotheseCourante\":[{\"type\":\"lieu\",\"nom\":\"Bureau\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Révolver\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Professeur Violet\",\"descriptif\":null,\"choix\":null}],\"invites\":[\"a\",\"b\"],\"connectes\":[\"c\",\"a\",\"b\"],\"joueurs\":[{\"util\":\"c\",\"piece\":\"Bureau\",\"perso\":{\"type\":\"perso\",\"nom\":\"Colonel Moutarde\",\"descriptif\":null,\"choix\":null},\"statutJoueur\":\"joue\",\"positionCourante\":{\"x\":21,\"y\":14},\"deck\":[{\"type\":\"arme\",\"nom\":\"Chandelier\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Madame Pervenche\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle de réception\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle de billard\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle à manger\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Bureau\",\"descriptif\":null,\"choix\":null}],\"blocnote\":[[{\"type\":\"arme\",\"nom\":\"Chandelier\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Madame Pervenche\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle de réception\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle de billard\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle à manger\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Bureau\",\"descriptif\":null,\"choix\":null}],[{\"type\":\"lieu\",\"nom\":\"Salon\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Professeur Violet\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Clé anglaise\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Monsieur Olive\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Entrée\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Docteur Orchidée\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Cuisine\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Poignard\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Barre de fer\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Révolver\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Mademoiselle Rose\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Chambre\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Corde\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Colonel Moutarde\",\"descriptif\":null,\"choix\":null}],[]]},{\"util\":\"a\",\"piece\":\"Bureau\",\"perso\":{\"type\":\"perso\",\"nom\":\"Professeur Violet\",\"descriptif\":null,\"choix\":null},\"statutJoueur\":\"joue\",\"positionCourante\":{\"x\":22,\"y\":14},\"deck\":[{\"type\":\"arme\",\"nom\":\"Corde\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salon\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Clé anglaise\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Entrée\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Cuisine\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Barre de fer\",\"descriptif\":null,\"choix\":null}],\"blocnote\":[[{\"type\":\"arme\",\"nom\":\"Corde\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salon\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Clé anglaise\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Entrée\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Cuisine\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Barre de fer\",\"descriptif\":null,\"choix\":null}],[{\"type\":\"arme\",\"nom\":\"Chandelier\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Colonel Moutarde\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Madame Pervenche\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Professeur Violet\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle de réception\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Monsieur Olive\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle de billard\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Docteur Orchidée\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle à manger\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Poignard\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Bureau\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Révolver\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Mademoiselle Rose\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Chambre\",\"descriptif\":null,\"choix\":null}],[]]},{\"util\":\"b\",\"piece\":\"\",\"perso\":{\"type\":\"perso\",\"nom\":\"Mademoiselle Rose\",\"descriptif\":null,\"choix\":null},\"statutJoueur\":\"joue\",\"positionCourante\":{\"x\":18,\"y\":12},\"deck\":[{\"type\":\"perso\",\"nom\":\"Colonel Moutarde\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Professeur Violet\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Monsieur Olive\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Docteur Orchidée\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Poignard\",\"descriptif\":null,\"choix\":null}],\"blocnote\":[[{\"type\":\"perso\",\"nom\":\"Colonel Moutarde\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Professeur Violet\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Monsieur Olive\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Docteur Orchidée\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Poignard\",\"descriptif\":null,\"choix\":null}],[{\"type\":\"arme\",\"nom\":\"Chandelier\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Corde\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Madame Pervenche\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salon\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle de réception\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Clé anglaise\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle de billard\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Entrée\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Salle à manger\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Cuisine\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Bureau\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Barre de fer\",\"descriptif\":null,\"choix\":null},{\"type\":\"arme\",\"nom\":\"Révolver\",\"descriptif\":null,\"choix\":null},{\"type\":\"perso\",\"nom\":\"Mademoiselle Rose\",\"descriptif\":null,\"choix\":null},{\"type\":\"lieu\",\"nom\":\"Chambre\",\"descriptif\":null,\"choix\":null}],[]]}],\"persosJoues\":[\"Mademoiselle Rose\",\"Colonel Moutarde\",\"Professeur Violet\"]}")
                        .withHeader("location","/cluedo/lancePartie/22?pseudoMdj=c"));

        PartieDTO a = this.clientInterface.rejoindrePartie(partieDTO.getIdPartie(),utilisateurDTO.getNom(),utilisateurDTO.getToken());

        Assert.assertEquals(a.getIdPartie(),partieDTO.getIdPartie());
    }

}
