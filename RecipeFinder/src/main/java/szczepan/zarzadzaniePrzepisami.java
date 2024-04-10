package szczepan;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class zarzadzaniePrzepisami {
    private static final Logger logger = LogManager.getLogger(zarzadzaniePrzepisami.class);
    private static final Set<String> pomijaneSkladniki = new HashSet<>(List.of("sól", "pieprz" , "woda"));

    private List<Przepis> przepisy;

    public zarzadzaniePrzepisami() {
        przepisy = new ArrayList<>();
    }

    public void dodajPrzepis(Przepis przepis) {
        przepisy.add(przepis);
        logger.info("Dodano nowy przepis do listy: " + przepis.getNazwa());

    }

    // Metody do pobierania i wyświetlania przepisów
    public List<Przepis> getPrzepisy() {
        return przepisy;
    }

    public void wyswietlWszystkiePrzepisy() {
        if (przepisy.isEmpty()) {
            logger.fatal("Lista przepisów jest pusta");
            return;
        }
        logger.info("Ilość przepisów w liście: " + przepisy.size());
        for (Przepis przepis : przepisy) {
            System.out.println("---------------------------------------");
            System.out.println(przepis.getNazwa() + " - " + przepis.getKategoria());
            przepis.wyswietlSkladniki();
        }
    }
    public void usunPrzepis(int idPrzepis) {
        if (przepisy.isEmpty()) {
            logger.fatal("Lista przepisów jest pusta");
            return;
        }
        if (idPrzepis >= 0 && idPrzepis < przepisy.size()) {
            Przepis usunietyPrzepis = przepisy.remove(idPrzepis);
            logger.info("Usunięto przepis z listy: " + usunietyPrzepis.getNazwa());
        } else {
            logger.error("Nieprawidłowy idPrzepis: " + idPrzepis);
        }
    }
    public void wyswietlPrzepis(int idPrzepis) {
        if (przepisy.isEmpty()) {
            logger.fatal("Lista przepisów jest pusta");
            return;
        }
        if (idPrzepis >= 0 && idPrzepis < przepisy.size()) {
            Przepis przepis = przepisy.get(idPrzepis);
            System.out.println("Przepis: " + przepis.getNazwa() + ", Kategoria: " + przepis.getKategoria());
            przepis.wyswietlSkladniki();
        } else {
            System.out.println("Nieprawidłowy idPrzepis: " + idPrzepis);
            logger.error("Nieprawidłowy idPrzepis: " + idPrzepis);
        }
    }

    public Przepis wyswietlPrzepisDnia() {
        if (przepisy.isEmpty()) {
            System.out.println("Lista przepisów jest pusta.");
            logger.fatal("Lista przepisów jest pusta.");
            return null;
        }

        Random rand = new Random();
        int index = rand.nextInt(przepisy.size());
        Przepis przepisDnia = przepisy.get(index);

        przepisDnia.wyswietlSkladniki();
        logger.info("Wybrano przepis dnia: " + przepisDnia.getNazwa() + " z kategorii: " + przepisDnia.getKategoria());
        return przepisDnia;
    }

    public Map<String, List<Przepis>> znajdzPrzepisyWedlugKategorii(wirtualnaLodowka lodowka) {
        Map<String, List<Przepis>> kategoriePrzepisow = new HashMap<>();
        kategoriePrzepisow.put("Idealne dla Ciebie", new ArrayList<>());
        kategoriePrzepisow.put("Nasze Propozycje", new ArrayList<>());
        kategoriePrzepisow.put("Pora na zakupy", new ArrayList<>());

        for (Przepis przepis : przepisy) {
            int liczbaDopasowanych = 0;
            int liczbaRelevantnychSkladnikow = 0;

            for (Skladnik skladnik : przepis.getSkladniki()) {
                if (!pomijaneSkladniki.contains(skladnik.getNazwa().toLowerCase())) {
                    liczbaRelevantnychSkladnikow++;
                    if (lodowka.czySkladnikDostepny(skladnik.getNazwa())) {
                        liczbaDopasowanych++;
                    }
                }
            }

            if (liczbaRelevantnychSkladnikow > 0) {
                double procentDopasowania = (double) liczbaDopasowanych / liczbaRelevantnychSkladnikow;
                logger.debug("Przepis: " + przepis.getNazwa() + ", procent dopasowania: " + procentDopasowania);

                if (procentDopasowania >= 0.75) {
                    kategoriePrzepisow.get("Idealne dla Ciebie").add(przepis);
                } else if (procentDopasowania >= 0.50) {
                    kategoriePrzepisow.get("Nasze Propozycje").add(przepis);
                } else if (procentDopasowania >= 0.30) {
                    kategoriePrzepisow.get("Pora na zakupy").add(przepis);
                }
            }
        }
        logger.info("Znaleziono przepisy według kategorii: " + kategoriePrzepisow);
        return kategoriePrzepisow;
    }

}
