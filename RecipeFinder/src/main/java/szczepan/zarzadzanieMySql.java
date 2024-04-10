package szczepan;

import java.sql.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class zarzadzanieMySql {

    private static final Logger logger = LogManager.getLogger(zarzadzanieMySql.class);

    public void dodajPrzepis(Przepis przepis) {
        Connection conn = DatabaseConnector.getConnection();
        if (conn != null) {
            try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Przepisy (nazwa, kategoria) VALUES (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, przepis.getNazwa());
                pstmt.setString(2, przepis.getKategoria());
                pstmt.executeUpdate();

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int przepisId = generatedKeys.getInt(1);
                        przepis.setId(przepisId);
                        for (Skladnik skladnik : przepis.getSkladniki()) {
                            dodajSkladnik(skladnik, przepisId);
                        }
                    }
                }
            } catch (SQLException e) {
                logger.error("Błąd podczas dodawania przepisu do bazy danych: ", e);
            } finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Błąd podczas zamykania połączenia: ", e);
                }
            }
        }
    }

    public boolean czyPrzepisIstnieje(String nazwaPrzepisu) {
        Connection conn = DatabaseConnector.getConnection();
        if (conn != null) {
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM Przepisy WHERE nazwa = ?")) {
                pstmt.setString(1, nazwaPrzepisu);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) > 0;
                    }
                }
            } catch (SQLException e) {
                logger.error("Błąd podczas sprawdzania istnienia przepisu: ", e);
            } finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Błąd podczas zamykania połączenia: ", e);
                }
            }
        }
        return false;
    }

    private void dodajSkladnik(Skladnik skladnik, int przepisId) {
        Connection conn = DatabaseConnector.getConnection();
        if (conn != null) {
            try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Skladniki (nazwa, ilosc, jednostka, id_przepisu) VALUES (?, ?, ?, ?)")) {
                pstmt.setString(1, skladnik.getNazwa());
                pstmt.setDouble(2, skladnik.getIlosc());
                pstmt.setString(3, skladnik.getJednostka());
                pstmt.setInt(4, przepisId);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                logger.error("Błąd podczas dodawania składnika do bazy danych: ", e);
            } finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Błąd podczas zamykania połączenia: ", e);
                }
            }
        }
    }

    public List<Przepis> pobierzWszystkiePrzepisy() {
        List<Przepis> przepisy = new ArrayList<>();
        Connection conn = DatabaseConnector.getConnection();
        if (conn != null) {
            try {
                // Pobranie wszystkich przepisów
                PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Przepisy");
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    int idPrzepisu = rs.getInt("id");
                    String nazwa = rs.getString("nazwa");
                    String kategoria = rs.getString("kategoria");

                    Przepis przepis = new Przepis(nazwa, kategoria);
                    przepis.setId(idPrzepisu);

                    // Pobranie składników dla każdego przepisu
                    PreparedStatement pstmtSkladniki = conn.prepareStatement("SELECT * FROM Skladniki WHERE id_przepisu = ?");
                    pstmtSkladniki.setInt(1, idPrzepisu);
                    ResultSet rsSkladniki = pstmtSkladniki.executeQuery();

                    while (rsSkladniki.next()) {
                        String nazwaSkladnika = rsSkladniki.getString("nazwa");
                        double ilosc = rsSkladniki.getDouble("ilosc");
                        String jednostka = rsSkladniki.getString("jednostka");
                        Skladnik skladnik = new Skladnik(ilosc, jednostka, nazwaSkladnika);
                        przepis.dodajSkladnik(skladnik);
                    }

                    przepisy.add(przepis);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return przepisy;
    }

    public List<String> pobierzUnikatoweNazwySkladnikow() {
        List<String> unikatoweNazwy = new ArrayList<>();
        Connection conn = DatabaseConnector.getConnection();
        if (conn != null) {
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT DISTINCT nazwa FROM Skladniki");
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    unikatoweNazwy.add(rs.getString("nazwa"));
                }
            } catch (SQLException e) {
                logger.error("Błąd podczas pobierania unikatowych nazw składników: ", e);
            } finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Błąd podczas zamykania połączenia: ", e);
                }
            }
        }
        return unikatoweNazwy;
    }

    public void dodajDoLodowki(List<String> skladniki) {
        Connection conn = DatabaseConnector.getConnection();
        if (conn != null) {
            try {
                for (String skladnik : skladniki) {
                    // Sprawdzenie, czy składnik już istnieje w lodówce
                    boolean istnieje = false;
                    try (PreparedStatement pstmtCheck = conn.prepareStatement("SELECT COUNT(*) FROM Lodowka WHERE nazwa_skladnika = ?")) {
                        pstmtCheck.setString(1, skladnik);
                        ResultSet rs = pstmtCheck.executeQuery();
                        if (rs.next()) {
                            istnieje = rs.getInt(1) > 0;
                        }
                    }

                    // Dodanie składnika, jeśli nie istnieje
                    if (!istnieje) {
                        try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Lodowka (nazwa_skladnika) VALUES (?)")) {
                            pstmt.setString(1, skladnik);
                            pstmt.executeUpdate();
                        }
                    }
                }
                logger.info("Dodano składniki do lodówki");
            } catch (SQLException e) {
                logger.error("Błąd podczas dodawania składników do lodówki: ", e);
            } finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Błąd podczas zamykania połączenia: ", e);
                }
            }
        }
    }

    public List<String> pobierzSkladnikiZLodowki() {
        List<String> skladniki = new ArrayList<>();
        Connection conn = DatabaseConnector.getConnection();
        if (conn != null) {
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT nazwa_skladnika FROM Lodowka")) {
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    skladniki.add(rs.getString("nazwa_skladnika"));
                }
            } catch (SQLException e) {
                logger.error("Błąd podczas pobierania składników z lodówki: ", e);
            } finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Błąd podczas zamykania połączenia: ", e);
                }
            }
        }
        return skladniki;
    }

    public void wyczyscLodowke() {
        Connection conn = DatabaseConnector.getConnection();
        if (conn != null) {
            try (PreparedStatement pstmt = conn.prepareStatement("TRUNCATE TABLE Lodowka")) {
                pstmt.executeUpdate();
                logger.info("Lodówka została wyczyszczona");
            } catch (SQLException e) {
                logger.error("Błąd podczas czyszczenia lodówki: ", e);
            } finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Błąd podczas zamykania połączenia: ", e);
                }
            }
        }
    }

    public void wyczyscLodowkePrzyUruchomieniu() {
        Connection conn = DatabaseConnector.getConnection();
        if (conn != null) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("TRUNCATE TABLE Lodowka");
                logger.info("Lodówka została wyczyszczona przy uruchomieniu programu");
            } catch (SQLException e) {
                logger.error("Błąd podczas czyszczenia lodówki przy uruchomieniu: ", e);
            } finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Błąd podczas zamykania połączenia: ", e);
                }
            }
        }
    }

    public List<String[]> znajdzPrzepisyWedlugKategorii() {
        List<String[]> przepisy = new ArrayList<>();
        String sql = "SELECT P.nazwa, COUNT(S.id) AS liczbaDopasowanych,\n" +
                "       (SELECT COUNT(*) FROM Skladniki WHERE id_przepisu = P.id) AS liczbaRelevantnychSkladnikow,\n" +
                "       COUNT(S.id) * 1.0 / (SELECT COUNT(*) FROM Skladniki WHERE id_przepisu = P.id) AS proporcja,\n" +
                "       P.kategoria\n" +
                "FROM Przepisy P\n" +
                "JOIN Skladniki S ON P.id = S.id_przepisu\n" +
                "WHERE S.nazwa IN (SELECT nazwa_skladnika FROM Lodowka)\n" +
                "GROUP BY P.id, P.nazwa\n" +
                "HAVING proporcja > 0.30\n" +
                "ORDER BY proporcja DESC, liczbaDopasowanych DESC;";

        Connection conn = DatabaseConnector.getConnection();
        if (conn != null) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    String nazwa = rs.getString("nazwa");
                    String liczbaDopasowanych = rs.getString("liczbaDopasowanych");
                    String liczbaRelevantnychSkladnikow = rs.getString("liczbaRelevantnychSkladnikow");
                    String proporcja = String.format("%.2f", rs.getDouble("proporcja"));
                    String kategoria = rs.getString("kategoria"); // Pobieranie kategorii

                    przepisy.add(new String[]{nazwa, kategoria, liczbaDopasowanych, liczbaRelevantnychSkladnikow, proporcja});
                }
            } catch (SQLException e) {
                logger.error("Błąd podczas wyszukiwania przepisów: ", e);
            } finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Błąd podczas zamykania połączenia: ", e);
                }
            }
        }
        return przepisy;
    }
    public String[] losujPrzepis() {
        String[] przepis = null;
        String sql = "SELECT nazwa FROM Przepisy ORDER BY RAND() LIMIT 1";

        Connection conn = DatabaseConnector.getConnection();
        if (conn != null) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    String nazwaPrzepisu = rs.getString("nazwa");
                    przepis = new String[]{nazwaPrzepisu}; // Możesz dodać więcej informacji o przepisie
                }
            } catch (SQLException e) {
                logger.error("Błąd podczas losowania przepisu: ", e);
            } finally {
                // Zamknięcie połączenia
            }
        }
        return przepis;
    }

    public List<String> pobierzSkladnikiDlaPrzepisu(String nazwaPrzepisu) {
        List<String> skladniki = new ArrayList<>();
        Connection conn = DatabaseConnector.getConnection();

        String sql = "SELECT S.nazwa FROM Skladniki S " +
                "JOIN Przepisy P ON S.id_przepisu = P.id " +
                "WHERE P.nazwa = ?";

        if (conn != null) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, nazwaPrzepisu);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    skladniki.add(rs.getString("nazwa"));
                }
            } catch (SQLException e) {
                // Obsługa błędów
            } finally {
                // Zamknięcie połączenia

            }

        }
        return skladniki;
    }
}
