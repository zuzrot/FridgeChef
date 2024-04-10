package szczepan;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {

    private static final Logger logger = LogManager.getLogger(DatabaseConnector.class);
    private static final String URL = "jdbc:mysql://localhost:3306/Przepisy";
    private static final String USER = "root";
    private static final String PASSWORD = "zsme";

    public static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            logger.info("Połączenie z bazą danych nawiązane pomyślnie");
            return connection;
        } catch (SQLException e) {
            logger.fatal("Błąd połączenia z bazą danych: ", e);
            return null;
        }
    }

}
