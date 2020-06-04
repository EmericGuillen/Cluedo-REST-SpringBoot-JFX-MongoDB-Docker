# Cluedo projet S2

Cette livraison contient une version finale du jeu du CLUEDO pour notre projet du semestre 2 pour le M1 MIAGE. Une documentation ainsi qu'une présentation de notre projet sous forme d'une vidéo.


### Prérequis

Client : javaFX , spring-webflux & Jackson

### Installation & Deploy 

Le déploiement du projet se fait sous Docker

Se placer dans cluedo-7/groupe7/cluedoWS et lancer ces 2 commandes :

```
mvn clean
```

```
mvn install
```

Ensuite creer l'image spring boot avec la commande suivante : 

```
docker build -f Dockerfile -t docker-cluedo .
```

Lancer le docker-compose :

```
docker-compose up
```

Le docker-compose déploie un container mongoDB et notre serveur d’application.



## Auteurs

* **GUILLEN Emeric**
* **LEFEBVRE William**
* **BOURHANE Adjmal**
* **TRINITE Anaïs**
* **SAKHI Chaïma**



## Sources

* https://www.baeldung.com/
* https://spring.io/
* https://www.youtube.com/channel/UCNpQ-r6yDpr4Q5bJ52VoI1g
* https://www.youtube.com/channel/UCBgYKqlyfqxbkGFxdGZ-uJw
* https://www.youtube.com/watch?v=YUgFZ0bHOl4
* https://www.youtube.com/watch?v=NBpu3Fhvkdc

