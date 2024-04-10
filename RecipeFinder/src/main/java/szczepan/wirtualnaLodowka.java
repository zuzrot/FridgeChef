package szczepan;

import java.util.HashSet;
import java.util.Set;

public class wirtualnaLodowka {
    private Set<String> dostepneSkladniki;

    public wirtualnaLodowka() {
        this.dostepneSkladniki = new HashSet<>();
    }

    public void dodajSkladnik(String nazwaSkladnika) {
        dostepneSkladniki.add(nazwaSkladnika.toLowerCase());
    }

    public boolean czySkladnikDostepny(String nazwaSkladnika) {
        return dostepneSkladniki.contains(nazwaSkladnika.toLowerCase());
    }
}