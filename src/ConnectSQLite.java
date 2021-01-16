import java.sql.*;
import java.util.List;

public class ConnectSQLite {

    static Connection connection = null;

    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        if (connection == null) {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:firstDB.db");
        }
        return connection;
    }

    static void getClients(List<Client> clients) throws ClassNotFoundException, SQLException {
        try (Statement statement = getConnection().createStatement()) {
            String query = "select * from student";
            try (ResultSet resultSet = statement.executeQuery(query)) {
                while (resultSet.next()) {
                    System.out.println("Добавлен пользователь " + resultSet.getString("name"));
                    clients.add(new Client(resultSet.getString("name"),
                            resultSet.getString("login"),
                            resultSet.getString("password")));
                }
            }
        } finally {
            closeDB();
        }
    }

    static void closeDB() throws SQLException {
        connection.close();
        System.out.println("Connection закрыт");
    }
}
