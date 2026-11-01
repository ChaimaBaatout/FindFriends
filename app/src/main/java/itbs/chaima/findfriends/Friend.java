package itbs.chaima.findfriends;

public class Friend {
    String nom,numero,latitude,longitude;

    public Friend(String nom, String numero, String latitude, String longitude) {
        this.nom = nom;
        this.numero = numero;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "nom='" + nom + '\'' +
                ", numero='" + numero + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                '}';
    }
}
