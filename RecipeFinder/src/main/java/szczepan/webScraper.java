package szczepan;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class webScraper {
    private static final Logger logger = LogManager.getLogger(webScraper.class);

    public static void scrape(zarzadzanieMySql zarzadzanie) {
        long startTime = System.currentTimeMillis();

        try {
            String url = "https://przepisy.fandom.com/wiki/Ksi%C4%85%C5%BCka_kucharska";
            Document doc = Jsoup.connect(url).get();
            Elements categories = doc.select("span.mw-headline");

            for (Element category : categories) {
                String categoryName = category.text();
                Elements linksInCategory = category.parent().nextElementSibling().select("p > a[href]");

                for (Element link : linksInCategory) {
                    String recipeName = link.text();

                    // Sprawdź, czy przepis już istnieje w bazie danych
                    if (!zarzadzanie.czyPrzepisIstnieje(recipeName)) {
                        String recipeUrl = link.attr("abs:href");
                        Przepis przepis = new Przepis(recipeName, categoryName);

                        try {
                            Document recipeDoc = Jsoup.connect(recipeUrl).get();
                            Elements ingredients = recipeDoc.select("i");

                            if (!ingredients.isEmpty()) {
                                for (Element ingredientElement : ingredients) {
                                    String ingredientText = ingredientElement.text();
                                    String nazwa = SkladnikiMap.znajdzNazweSkladnika(ingredientText);
                                    String jednostka = JednostkiMap.znajdzJednostkeWSkladniku(ingredientText);
                                    Double ilosc = JednostkiMap.znajdzIloscWSkladniku(ingredientText);
                                    przepis.dodajSkladnik(new Skladnik(ilosc, jednostka, nazwa));
                                }
                                zarzadzanie.dodajPrzepis(przepis);
                            } else {
                                logger.error("Brak składników w: " + recipeUrl + " - skip");
                            }
                        } catch (Exception e) {
                            logger.error("Błąd podczas przetwarzania przepisu: " + recipeUrl, e);
                        }
                    } else {
                        logger.info("Przepis '" + recipeName + "' już istnieje w bazie danych - skip");
                    }
                }
            }
        } catch (Exception e) {
            logger.fatal("Nie udało się połączyć ze stroną główną", e);
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.debug("Czas zescrapowania wszystkich przepisów: " + duration + " ms");
    }
    public static String pobierzLinkDoPrzepisu(String nazwaPrzepisu) {
        String url = "https://przepisy.fandom.com/wiki/Ksi%C4%85%C5%BCka_kucharska";
        try {
            Document doc = Jsoup.connect(url).get();
            Element linkElement = doc.select("a[title=\"" + nazwaPrzepisu + "\"]").first();
            if (linkElement != null) {
                return linkElement.attr("abs:href");
            }
        } catch (Exception e) {
            logger.error("Błąd podczas pobierania linku do przepisu: " + nazwaPrzepisu, e);
        }
        return null;
    }
}
