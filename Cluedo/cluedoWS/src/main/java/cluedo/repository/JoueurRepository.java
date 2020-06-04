package cluedo.repository;

import cluedo.modele.Joueur;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface JoueurRepository extends MongoRepository<Joueur,String> {
}
