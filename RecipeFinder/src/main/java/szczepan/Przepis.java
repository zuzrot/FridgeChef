package szczepan;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Przepis {

    private static final Logger logger = LogManager.getLogger(Przepis.class);
    private int id;

    private String nazwa;
    private List<Skladnik> skladniki;
    private String kategoria;

    public Przepis(String nazwa, String kategoria) {
        this.nazwa = nazwa;
        this.kategoria = kategoria;
        this.skladniki = new ArrayList<>();
        logger.info("Utworzono nowy przepis: " + nazwa);
    }

    public void dodajSkladnik(Skladnik skladnik) {
        skladniki.add(skladnik);
//        logger.info("Do przepisu '" + nazwa + "' dodano składnik: "
//                + skladnik.getNazwa() + ", Ilość: " + skladnik.getIlosc()
//                + ", Jednostka: " + skladnik.getJednostka());
    }

    public void wyswietlSkladniki() {
        for (Skladnik s : skladniki) {
            s.wyswietl();
        }
    }

    // Gettery i settery
    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public List<Skladnik> getSkladniki() {
        return skladniki;
    }

    public String getKategoria() {
        return kategoria;
    }

    public void setKategoria(String kategoria) {

        this.kategoria = kategoria;
    }
    // Metoda setId
    public void setId(int id) {
        this.id = id;
    }

    // Metoda getId (może być potrzebna w innych częściach Twojego programu)
    public int getId() {
        return id;
    }

    public void wyswietlKategorie() {
        System.out.println("Kategoria przepisu: " + kategoria);
    }

}

class Skladnik {
    private double ilosc;
    private int id;
    private String jednostka;
    private String nazwa;

    public Skladnik(double ilosc, String jednostka, String nazwa) {
        this.ilosc = ilosc;
        this.jednostka = jednostka;
        this.nazwa = nazwa;
    }

    public void wyswietl() {
        System.out.println(nazwa + ": " + ilosc + " " + jednostka);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public double getIlosc() {
        return ilosc;
    }

    public String getJednostka() {
        return jednostka;
    }

    public String getNazwa() {
        return nazwa;
    }

    // Settery
    public void setIlosc(double ilosc) {
        this.ilosc = ilosc;
    }

    public void setJednostka(String jednostka) {
        this.jednostka = jednostka;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }
}

