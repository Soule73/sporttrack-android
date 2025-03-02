# Application de suivi de données sportives avec API Ktor

## Description
### Objectif :
Créer une application mobile qui enregistre les séances sportives des utilisateurs
(running, vélo, fitness, etc.) et offre une visualisation de l’historique via un tableau de bord.

### Fonctionnalités :
- Enregistrement de données (distance, temps, calories brûlées, GPS).
- Statistiques globales (graphes, meilleure performance, progression).
- Partage ou export des données.
- Intégration de Google Maps ou Mapbox pour afficher les parcours.
### Points techniques
#### Backend :
- Ktor pour exposer des endpoints permettant d’envoyer/récupérer les données  d’activité.
- Stockage des données et agrégation (par exemple via PostgreSQL).
- Possibilité de faire des traitements statistiques côté serveur (calcul de moyenne, distance totale, etc.).
#### Frontend Android :
- Accès au GPS et aux capteurs (cas possible de Health API sur Android).
- Utilisation des coroutines pour gérer l’envoi asynchrone des données.
- Gestion d’une carte interactive (Google Maps ou Mapbox SDK).


## Configuration

#### Frontend Android
- Cloner le projet avec git
- Renommer le fichier `keystore.properties.example` en `keystore.properties`

#### Backend
- [Cloner le backend](https://github.com/Soule73/sporttrack-api-ktor).
- Assurer vous de créer une base de données nommer `sport_track_db`.
- 