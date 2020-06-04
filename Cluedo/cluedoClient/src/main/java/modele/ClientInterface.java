package modele;

import controleur.Controleur;
import modele.DTO.*;
import modele.exceptions.*;

import java.util.HashMap;
import java.util.List;

public interface ClientInterface {


    public void setMonControleur(Controleur monControleur);
    /**
     * Permet de créer un nouvel utilisateur
     * @param utilisateurDTO
     * @return
     */
    int inscription(UtilisateurDTO utilisateurDTO) throws InscriptionImpossibleException;

    /**
     * Permet de connecter utilisateur
     * @param utilisateurDTO
     * @return
     */
    String connexion(UtilisateurDTO utilisateurDTO) throws ConnexionImpossibleException;

    //TODO
    void deconnexion(UtilisateurDTO utilisateurDTO, String token) throws DeconnexionImpossibleException;
    UtilisateurDTO userGet(UtilisateurDTO utilisateurDTO, String token) throws RecupInfoUtilisateurImpossibleException;
    boolean userDelete(UtilisateurDTO utilisateurDTO, String token) throws SuppressionUtilisateurImpossibleException;
    PartieDTO creerPartie(PartieDTO partieDTO, String token) throws CreationPartieImpossibleException;
    PartieDTO rejoindrePartie(String idPartie, String pseudoJoueur, String token) throws RejoindrePartieImpossibleException;
    PartieDTO lancerPartie(String idPartie,String pseudoMdj, String token) throws LancePartieImpossibleException;
    void accepteSauvegarde(String idPartie, int idJoueur, boolean accept, String token) throws AccepteSauvegardeImpossibleException;
    List<PartieDTO> sauvegardeGET(int idJoueur, String token) throws DemandeSauvegardeImpossibleException;
    void sauvegardePATCH(PartieDTO partieDTO, String token) throws SauvegardeImpossibleException;
    PartieDTO restaurePartie(String idPartie,String pseudoMdj, String token) throws RestaurePartieImpossibleException;
    boolean reveleCarte(String idPartie, String idJoueur, CarteDTO carteRevelee, List<JoueurDTO> aQuiReveler, String token) throws ReveleCarteImpossibleException;
    boolean suppose(String idPartie, String idJoueur, CarteDTO suspect, CarteDTO arme, CarteDTO lieu, String token) throws SuppositionImpossibleException;
    boolean accuse(String idPartie, String idJoueur, CarteDTO suspect, CarteDTO arme, CarteDTO lieu, String token) throws AccusationImpossibleException;
    DesDTO lanceDes(String idPartie, String idJoueur, String token) throws LanceDesImpossibleException;
    JoueurDTO deplacePion(String idPartie, String idJoueur, PointDTO destination, String piece, String token) throws DeplacePionImpossibleException;
    List<List<String>> prendPassage(String idPartie, int idJoueur, PointDTO passage, String token) throws PrendPassageImpossibleException;
    boolean piocheCarteIndice(String idPartie, String idJoueur, String token) throws PiocheCarteIndiceImpossibleException;
    CarteDTO joueCarteIndice(String idPartie, int idJoueur, String token) throws JoueCarteIndiceImpossibleException;
    void consulteCarnet(String idPartie, String idJoueur, String token) throws ConsulteCarnetImpossibleException;
    void remplirCarnet(String idPartie, String idJoueur, List<List<CarteDTO>> cartes, String token) throws ConsulteCarnetImpossibleException;
    List<CarteDTO> consulteCartes(String idPartie, int idJoueur, String token) throws ConsulteCartesImpossibleException;
    List<CarteDTO> consulteCartesJeu(String idPartie, String idJoueur, String token) throws ConsulteCartesImpossibleException;
    void terminePartie(String idPartie, String token) throws TerminePartieImpossibleException;
    boolean quitterPartie(String idPartie, String pseudoJoueur, String token) throws QuitterPartieImpossibleException;

    List<String> utilisateursConnect(String token) throws RecuperationUtilisateursImpossibleException;
    List<PartieDTO> consulterInvitations(String nom, String token) throws RecuperationPartiesImpossibleException;
    JoueurDTO termineTour(String idPartie, String idJoueur, String token) throws TermineTourImpossibleException;

    void recuperationDes(String token)throws InterruptedException;
    void recuperationSuppositions(String token)throws InterruptedException;
    void recuperationAccusation(String token)throws InterruptedException;
    void recuperationLancerPartie(String token)throws InterruptedException;
    void recuperationRejointPartie(String token)throws InterruptedException;
    void recuperationDeplacement(String token)throws InterruptedException;
    void recuperationTermineTour(String token)throws InterruptedException;
    void recuperationQuitterPartie(String token)throws InterruptedException;
    void recuperationReveleCarte(String token)throws InterruptedException;
    void recuperationCarnet(String token)throws InterruptedException;
    void recuperationSauvegarde(String token)throws InterruptedException;
    void recuperationTerminePartie(String token)throws InterruptedException;

    void recuperationPiocheCarteIndice(String token);
    void reset();
}
