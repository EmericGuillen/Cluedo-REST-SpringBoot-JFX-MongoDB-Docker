package cluedo.DTO;

import cluedo.modele.Carte;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarteDTO {

    /*
     * ATTRIBUTS
     */
    private String type;
    private String nom;
    private String descriptif;
    private String choix;


    /*
     * METHODES
     */
    public static CarteDTO creerCarteDTO(Carte carte){

        CarteDTO carteDTO = new CarteDTO();
        carteDTO.setNom(carte.getNom());
        carteDTO.setType(carte.getType());
        carteDTO.setDescriptif(carte.getDescriptif());
        return carteDTO;
    }


    public static Carte creerCarteFromDTO(CarteDTO carteDTO){

        Carte carte = new Carte();
        carte.setNom(carteDTO.getNom());
        carte.setType(carteDTO.getType());
        carte.setDescriptif(carteDTO.getDescriptif());
        return carte;
    }

    public static CarteDTO creerCarteDTOFromCarte(Carte carte){

        CarteDTO carteDTO = new CarteDTO();
        carteDTO.setNom(carte.getNom());
        carteDTO.setType(carte.getType());
        carteDTO.setDescriptif(carte.getDescriptif());
        return carteDTO;
    }


    public static List<CarteDTO> creerListeCarteDTO(List<Carte> liste){

        List<CarteDTO> listeCarteDTO = new ArrayList<>();

        for(Carte carte: liste){
            listeCarteDTO.add(CarteDTO.creerCarteDTO(carte));
        }

        return listeCarteDTO;
    }


    public static List<Carte> listeDTOtoCarte(List<CarteDTO> listeDTO){
        List<Carte> listeCarte = new ArrayList<>();

        for(CarteDTO carteDTO: listeDTO){
            listeCarte.add(creerCarteFromDTO(carteDTO));
        }

        return listeCarte;
    }

    public static List<CarteDTO> listetoCarte(List<Carte> liste){
        List<CarteDTO> listeCarte = new ArrayList<>();

        for(Carte carte: liste){
            listeCarte.add(creerCarteDTOFromCarte(carte));
        }

        return listeCarte;
    }

    public static List<List<Carte>> listeListeDTOtoCarte(List<List<CarteDTO>> listedeListesDTO){

        List<List<Carte>> listeDeListes = new ArrayList<>();

        for(List<CarteDTO> liste: listedeListesDTO){
            listeDeListes.add(listeDTOtoCarte(liste));
        }

        return listeDeListes;
    }

    public static List<List<CarteDTO>> listeListetoCarte(List<List<Carte>> listedeListes){

        List<List<CarteDTO>> listeDeListes = new ArrayList<>();

        for(List<Carte> liste: listedeListes){
            listeDeListes.add(listetoCarte(liste));
        }

        return listeDeListes;
    }


    public static HashMap<CarteDTO, String> carteToHashmapDTO(Map<Carte, String> map){

        HashMap<CarteDTO, String> mapDTO = new HashMap<>();

        for(Map.Entry<Carte, String> entry : map.entrySet()){
            CarteDTO carteDTO = CarteDTO.creerCarteDTO(entry.getKey());
            mapDTO.put(carteDTO, entry.getValue());
        }

        return mapDTO;
    }

    /*
     * GETTERS / SETTERS
     */
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescriptif() { return descriptif; }
    public void setDescriptif(String descriptif) { this.descriptif = descriptif; }

    public String getChoix() { return choix; }
    public void setChoix(String choix) { this.choix = choix; }
}
