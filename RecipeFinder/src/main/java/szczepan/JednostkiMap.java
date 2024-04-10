package szczepan;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class JednostkiMap {

    private static final Logger logger = LogManager.getLogger(JednostkiMap.class);

    public static HashMap<String, List<String>> getJednostkiMap() {
        HashMap<String, List<String>> jednostki = new HashMap<>();

        jednostki.put("gram", Arrays.asList( "gram", "gramy", "gramów"));
        jednostki.put("dekagram", Arrays.asList("dag", "dekagram", "dekagramy", "dkg", "dekagramów"));
        jednostki.put("kilogram", Arrays.asList("kg", "kilogram", "kilogramy", "kilogramu", "kilogramów", "kilograma"));
        jednostki.put("łyżka", Arrays.asList("łyżka", "łyżki", "łyżeczka", "łyżeczek", "łyżeczki", "łyżek"));
        jednostki.put("szklanka", Arrays.asList("szklanka", "szklanki"));
        jednostki.put("sztuka", Arrays.asList("sztuka", "sztuki", "sztuk"));
        jednostki.put("puszka", Arrays.asList("puszka", "puszki"));
        jednostki.put("kromka", Arrays.asList("kromka", "kromki"));
        jednostki.put("listek", Arrays.asList("listek", "listki", "liść", "liście", "listków"));
        jednostki.put("ząbek", Arrays.asList("ząbek", "ząbki"));
        jednostki.put("opakowanie", Arrays.asList("opakowanie", "opakowania"));
        jednostki.put("litry", Arrays.asList( "litry", "litrow", "litra"));
        jednostki.put("mililitry", Arrays.asList("ml", "mililitry", "mililitrow"));
        jednostki.put("mały słoik", Arrays.asList("mały słoik", "małe słoiki", "słoiczek"));
        jednostki.put("słoik", Arrays.asList("słoik", "słoiki"));
        jednostki.put("strąk", Arrays.asList("strąk", "strąki", "strąka"));
        jednostki.put("pęczek", Arrays.asList("pęczek", "pęczki", "pęczka"));
        jednostki.put("natka", Arrays.asList("natka", "natki"));
        jednostki.put("gałązka", Arrays.asList("gałązka", "gałązki", "gałązek"));
        jednostki.put("plaster", Arrays.asList("plaster", "plastry", "plastrów", "plasterki", "plasterków"));
        jednostki.put("łodyga", Arrays.asList("łodyga", "łodyg", "łodygi"));
        jednostki.put("kropla", Arrays.asList("kropla", "kropel"));
        jednostki.put("główka", Arrays.asList("główka", "główki"));
        jednostki.put("kostka", Arrays.asList("kostka", "kostki"));
        jednostki.put("torebka", Arrays.asList("torebka", "torebki", "woreczek", "woreczki"));
        jednostki.put("szczypta", Arrays.asList("szczypta", "szczypty"));
        jednostki.put("ziarna", Arrays.asList("ziarna", "ziarenka", "ziaren"));
        jednostki.put("kawałek", Arrays.asList("kawałek", "kawałków", "kawałki"));
        jednostki.put("różyczka", Arrays.asList("różyczka", "różyczek"));
        jednostki.put("pojemnik", Arrays.asList("pojemnik", "pojemniki", "pojemniczki"));
        jednostki.put("garść", Arrays.asList("garść" ,"garście", "garści"));
        jednostki.put("kieliszek", Arrays.asList("kieliszek", "kieliszki"));
        jednostki.put("laska", Arrays.asList("laska", "laski", "lasek"));
        jednostki.put("kubek", Arrays.asList("kubek", "kubki", "kubeczek", "kubeczki", "kubeczka", "kubka"));
        jednostki.put("filiżanka", Arrays.asList("filiżanka", "filiżanki"));
        return jednostki;
    }

    public static String znajdzJednostkeWSkladniku(String skladnik) {
        for (Entry<String, List<String>> entry : getJednostkiMap().entrySet()) {
            for (String synonim : entry.getValue()) {
                if (skladnik.contains(synonim)) {
                    return entry.getKey();
                }
            }
        }
        return "sztuka"; // Możesz zwrócić specjalny ciąg znaków, jeśli jednostka nie zostanie znaleziona
    }



    public static double znajdzIloscWSkladniku(String skladnik) {
        String pierwszyWyraz = skladnik.split("\\s+")[0]; // Pobieranie pierwszego wyrazu

        // Sprawdzenie zakresów i obliczenie średniej
        if (pierwszyWyraz.matches("\\d+-\\d+")) {
            String[] zakres = pierwszyWyraz.split("-");
            double liczba1 = Double.parseDouble(zakres[0]);
            double liczba2 = Double.parseDouble(zakres[1]);
            return (liczba1 + liczba2) / 2.0;
        }

        // Sprawdzenie wyrazów specjalnych
        switch (pierwszyWyraz.toLowerCase()) {
            case "pół":
                return 0.5;
            case "ćwierć":
                return 0.25;
            case "półtora":
                return 1.5;
            default:
                // Konwersja na double lub zwrócenie wartości 1 jako domyślnej
                try {
                    return Double.parseDouble(pierwszyWyraz);
                } catch (NumberFormatException e) {
                    return 1; // Zwracanie 1 jako wartości domyślnej
                }
        }
    }
}
