package cluedo.repository;
import cluedo.modele.Utilisateur;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.ArrayList;

public interface UtilisateurRepository extends MongoRepository<Utilisateur,String> {
    public ArrayList<Utilisateur> findAll();
    public Utilisateur findByNom(String nom);
    public Utilisateur findByIdUtil(String id);
    public Utilisateur findByNomAndPassword(String nom,String password);
    public void deleteByNomAndPassword(String nom,String password);


}
