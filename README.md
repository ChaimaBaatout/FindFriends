# Find Friends

Application Android (Java) pour partager sa position avec ses amis par SMS et la voir sur une carte Google Maps.

## Fonctionnalités

- Liste des amis, récupérée depuis un service web PHP
- Envoi de sa position GPS par SMS
- Réception des SMS de position, avec une notification
- Affichage de la position reçue sur Google Maps

## Technologies

- Java, Android SDK (minSdk 21, targetSdk 32)
- Google Maps et services de localisation Google Play
- Navigation Android et View Binding

## Lancer le projet

1. Ouvre le projet dans Android Studio.
2. Crée une clé [Google Maps](https://developers.google.com/maps/documentation/android-sdk/get-api-key).
3. Ajoute-la dans `local.properties`, qui n'est pas versionné :

   ```properties
   MAPS_API_KEY=ta_cle
   ```

4. Renseigne l'adresse de ton serveur dans `app/src/main/java/itbs/chaima/findfriends/Config.java`.
5. Lance l'application sur un téléphone ou un émulateur.

Le service web PHP (`servicephp`) ne fait pas partie de ce dépôt.