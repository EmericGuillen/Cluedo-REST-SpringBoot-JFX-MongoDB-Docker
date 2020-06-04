package cluedo.repository;

import java.util.ArrayList;
import cluedo.modele.Partie;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PartieRepository extends MongoRepository<Partie,String> {
    public ArrayList<Partie> findAll();
    public Partie findByIdPartie(String id);
    public Partie findByIdPartieAndMdj(String id,String mdj);
    public void deleteByIdPartieAndMdj(String id,String mdj);

}
