package ru.digital_spirit.qaaf.utils.dbconnections;

import org.aeonbits.owner.ConfigFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Класс, устанавливающий соединение с базой данных.
 * Параметры подключения передаются в виде объекта класса
 * унаследованного от абстрактного класса DBConnectionParameters
 */
public class DBConnectionManager {
    private static Map<String, Connection> connections;

    /**
     * Закрытый конструктор.
     * Создается инстанс класса и коллеуция подключений.
     */
    private DBConnectionManager(){

    }

    /**
     * @param dbConnectionName - название сохраненного объекта с параметрами подключения
     * @return - Возвращает объект класса Connection с инстансом подключения к базе данных
     */
    public static Connection getConnection(String dbConnectionName) {
        if (connections == null) {
            connections = new HashMap<>();
        }
        return hasActiveConnection(dbConnectionName) ?
                        connections.get(dbConnectionName ): createConnection(dbConnectionName);
    }

    /**
     * Метод для создания подключения к БД
     * Создается подключение и выводится на экран информация о подключении.
     * @param dbName - название БД
     */
    public static Connection createConnection(String dbName) {
        DBConnectionParameters dbConnection = ConfigFactory
                .create(
                DBConnectionParameters.class,
                Map.of("db", dbName));
        try {
            Connection connection = DriverManager.getConnection(
                    dbConnection.url(),
                    dbConnection.login(),
                    dbConnection.password());
            System.out.println("Установлено соединение с базой данных: \n" +
                    connection.getMetaData().getDatabaseProductName() + ": " + connection.getMetaData().getDatabaseProductVersion() + "\n" +
                    connection.getMetaData().getUserName() + "\n" +
                    connection.getMetaData().getURL());
            connections.put(dbName, connection);
            return connections.get(dbName);
        } catch (SQLException ex) {
            System.out.println("Не удалось подключиться к базе данных!");
            System.out.println("Проверьте правильность указания имени бд в файле config/db/db.properties");
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Метод для проверки, установленно ли соединение с БД
     * @param dbConnectionName - название БД в виде строки
     * @return - true/false
     */
    public static boolean hasActiveConnection(String dbConnectionName) {
        try {
            return connections.containsKey(dbConnectionName)
                    && connections.get(dbConnectionName) != null
                    && !(connections.get(dbConnectionName).isClosed());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Метод закрывающий соединение с БД
     * @param dbConnectionName - название БД в .config-файле
     */
    public static void closeConnection(String dbConnectionName) {
        if (hasActiveConnection(dbConnectionName)) {
            try {
                connections.get(dbConnectionName).close();
                System.out.println("Closed connection to DB: " + dbConnectionName);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Метод для получения списка всех устанавливаемых подключений к БД
      * @return - возвращает коллекцию строк с названиеями подключений
     */
    public static Set<String> getConnectionsList() {
        return connections == null ? null : connections.keySet();
    }
}
