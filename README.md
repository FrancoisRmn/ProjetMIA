# ProjetMIA
Ce repository reprend le code du projet pour le cours MIA. Le projet a pour but d'utiliser des gestes pour controller une application via le bracelet [kinemic band](https://kinemic.com/en/kinemic-band/).

Le projet est développé en ***Android***.

## Description du projet
Le personna choisi est Dumbeldore et il faut créer une application de Quiddich pour lui.

Pour commencer le jeu il faut mettre debout son balai (face vers le bas en dessous à droite).

Menu de sélection au début pour choisir quel rôle on décide de jouer.

### Premier rôle : Poursuiveur

 - Il faut eviter des cognards (gauche et droite)
 - Attraper le souafle -> Lever le bras au dessus a droite
 - Lancer le souafle -> Diagonale haut droite -> bas gauche

 ### Deuxième rôle : Batteur

 - Il doit taper les cognards -> Mouvement gauche / droite -> Préparation de frappe (mouvement en 2 phases)
 - Il doit se déplacer vers les cognards pour proteger les autres (mvt gauche et droite)

 ### Toisième rôle : Attrapeur

 - Il faut eviter des cognards (gauche et droite)
 - Il faut attraper le vif d'or -> mouvement poignet droit + sur une certaine coord, faut passer par la ([cf 23 sec](https://www.youtube.com/watch?v=zlA1gZAExF0&ab_channel=Kinemic))

### (Quatrième rôle : Gardien)

todo si besoin

## Liens utiles
 - [API Android](https://developer.kinemic.com/docs/android/latest/api/)
 - [Chaine youtube](https://www.youtube.com/channel/UCdbmjtEroZXI3krkUZbwgEg/videos) (Tutoriel des gestes pré-enregistré)
 - [Gestes existants](https://developer.kinemic.com/docs/android/latest/api/de.kinemic.gesture/-gesture/)