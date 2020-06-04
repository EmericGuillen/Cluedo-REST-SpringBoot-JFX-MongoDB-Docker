package cluedo.repository;

import cluedo.modele.Carte;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CarteRepository extends MongoRepository<Carte,String> {
}
