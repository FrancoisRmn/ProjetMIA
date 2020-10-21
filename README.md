# ProjetMIA
Ce repository reprend le code du projet pour le cours MIA. Le projet a pour but d'utiliser des gestes pour controller une application via le bracelet [kinemmic band](https://kinemic.com/en/kinemic-band/).

Le projet est développé en ***Android***.

## Description du projet
Le personna choisi est Dumbeldore et il faut créer une application de Quiddich pour lui.

Différents scénarios : 

 - Il faut eviter des cognards
 - Il faut faire des passes
 - Il faut attraper le vif d'or
 - Attraper / Arretter le souaffle

## Liste des gestes utilisés
Cette partie reprend la liste des gestes utilisés par notre application, comment les réaliser et l'action qui correspond.

### Idées de mouvements / situations
 - Frapper un cognard -> trouver un geste
 - Attraper le vif d'or -> trouver un geste
 - Faire une passe -> Trouver un geste
 - Attraper le souaffle -> Trouver un geste

 Navigation dans un menu / Lancer le jeu avec `Check mark` ?

### Gestes pré-existant
Liste des gestes pré-existant [ici](https://developer.kinemic.com/docs/android/latest/api/de.kinemic.gesture/-gesture/).

 - `Swipe Right` : Le cognard vient de la gauche, il faut l'éviter en allant à droite.
 - `Swipe Left` : Le cognard vient de la droite, il faut l'éviter en allant à gauche.
 - `Swipe Up` : Le cognard vient du bas, il faut l'éviter en allant vers le haut. Possible avec un ballet en main ? `Todo : Test`
 - `Swipe Down` : Le cognard vient du haut, il faut l'éviter en allant vers le bas. Possible avec un ballet en main ? `Todo : Test`

 - `Circle Left / Right` : Le cognard vient de face et il faut faire un cercle pour l'éviter.

 - `Cross Mark` : Le gardien doit arretter le souaffle -> geste de barriere pour pas que le but soit marqué.


### Gestes rajoutés par nos soins
Essayer de jouer avec les positions relatives (monter, descendre, gauche, droite, ...) pour essayer de trouver des situations. Pe plus pour des reglages, un menu ?

 - 

## Liens utiles
 - [API Android](https://developer.kinemic.com/docs/android/latest/api/)
 - [Chaine youtube](https://www.youtube.com/channel/UCdbmjtEroZXI3krkUZbwgEg/videos) (Tutoriel des gestes pré-enregistré)