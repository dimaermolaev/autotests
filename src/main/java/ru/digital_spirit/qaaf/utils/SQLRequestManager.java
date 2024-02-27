package ru.digital_spirit.qaaf.utils;

import java.sql.*;
import java.util.*;

/**
 * Класс, реализующий возможность отправлять запросы в базу данных и получать результате в удобном виде.
 * -------------------------------------------------------------
 * Класс реализован как синглтон, получение инстанса класса через метод getInstance, в который передаем инстанс подключения к нужной базе данных (тип Connection).
 * Для отправки запросов используются два основных метода:
 * executeQuery(String sqlQuery) - для выполнения SELECT'ов и представления результатов в виде List<Map<String, String>> - список строк.
 *     Каждая строка в бд представлена в виде Map<String, String>, где ключ - это название столбца, а в значении - содержимое ячейки.
 * updateExecute(String sqlQuery) - для выполнения запросов, которые вносят изменения в таблицу (INSERT, UPDATE, DELETE и т.д.)
 *      Возвращает количество измененных строк в результате выполнения запроса.
 *
 */
public class SQLRequestManager {
    private static SQLRequestManager instance;
    private static Connection connection;

    private SQLRequestManager(Connection dbConnection) {
        instance = this;
        connection = dbConnection;
    }

    /** Метод для использования класса как синглтона */
    public static SQLRequestManager getInstance(Connection dbConnection) {
        instance = new SQLRequestManager(dbConnection);
        return instance;
    }

    /** Метод, который отправляет в базу данных запрос, а возвращает данные в видел List'a Map'ов <String,String> */
    public List<Map<String, String>> executeQuery(String sqlQuery) {
        Statement stmt = null;
        ResultSet rs = null;
        List<Map<String, String>> results = new ArrayList<>();
        try {
            System.out.println("Выполняется запрос к БД " + connection.getMetaData().getURL()
                                + ":\n===========================\n"
                                + "\n" + sqlQuery + "\n"
                                + "\n===========================\n");
            stmt = connection.createStatement();
            rs = stmt.executeQuery(sqlQuery);
            while (rs.next()) {
                Map<String, String> resultsTmp = new LinkedHashMap<>();
                ResultSetMetaData rsmd = rs.getMetaData();
                int numColumns = rsmd.getColumnCount();
                for (int i = 1; i < numColumns + 1; i++) {
                    try{
                        resultsTmp.put(rsmd.getColumnName(i), rs.getString(rsmd.getColumnLabel(i)));
                    } catch (SQLException e) {
                        resultsTmp.put(rsmd.getColumnName(i), null);
                    }
                }
                results.add(resultsTmp);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally{
            closeResultSet(rs);
            closeStatement(stmt);
        }
//        printer(results);
        return results;
    }

    /** Метод, который отправляет в базу запрос на создание/изменение данных
     * и возвращает кол-во изменных строк */
    public Integer updateExecute(String sqlQuery) {
        Statement stmt = null;
        Integer strNum = null;
        try {
            stmt = connection.createStatement();
            strNum = stmt.executeUpdate(sqlQuery);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeStatement(stmt);
        }
        System.out.println("Кол-во обработаных строк: " + strNum);
        return strNum;
    }

    /** Метод, который выводит на экран результат SQL запроса */
    private void printer(List<Map<String, String>> resultSet) {
        for (Map<String, String> field : resultSet) {
            field.forEach((key, value) -> System.out.print(key + ": " + value + '\t'));
            System.out.println('\n');
        }
    }

    /** Метод, закрывающий Statement */
    private void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    /** Метод, закрывающий ResultSet */
    private void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}