package ru.digital_spirit.qaaf.utils;

import ru.digital_spirit.qaaf.utils.vars.LocalVariablesManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.digital_spirit.qaaf.utils.LogHelper.info;

/**
 * Класс для работы с базами данных.
 * getConnection - метод для создания подключения к БД.
 * saveResults - метод для сохранения результатов при выполненнии SQL-запросов.
 * getResults - метод для получения сохраненных ранее результатов SQL-запросов.
 */
public class SQLHelper {

    private static final Map<String, List<Map<String, String>>> results = new HashMap<>();

    /**
     * Метод для создания подключения к БД.
     * @param dbName - название базы данных, к которой производится подключение.
     * @return - возвращает объект типа Connection, хранящий состояние подключения к БД.
     */


    /**
     * Метод для сохранения результатов при выполненнии SQL-запросов.
     * Используется для сохранения результатов SQL-запросов и передачи их между методами.
     * @param resultName - имя сохраненного результата в виде строки.
     * @param result - результат выполнененного SQL-запроса в виде List<Map<String, String>>, который хотим сохранить.
     */
    public static void saveResults(String resultName, List<Map<String, String>> result) {
        LocalVariablesManager.setVar(resultName, "sqlResults");
        results.put(resultName, result);
    }

    /**
     * Метод для получения сохраненных ранее результатов SQL-запросов.
     * @param resultName - имя сохраненного результата в виде строки.
     * @return - результат выполнененного SQL-запроса в виде List<Map<String, String>>.
     */
    public static List<Map<String, String>> getResults(String resultName) {
        if (results.get(resultName) == null) {
            throw new IllegalArgumentException("В коллекции нет сохраннеых переменных с таким именем: " + resultName);
        }
        return results.get(resultName);
    }

    /**
     * Метод для объединения строк в шаге теста в одну строку с запросом
     * @param sqlRequestLines - коллеция строк из шага
     * @return - возвращает единственную строку, объединенную из элементов коллекции
     */
    public static String getStringFromDataTable(List<String> sqlRequestLines) {
        StringBuilder sb = new StringBuilder();
        sqlRequestLines.forEach(line -> sb.append(line).append(' '));
        return sb.toString();
    }

    public static String getCellValue(Map<String, String> sqlCollection, String fieldName) {
        return sqlCollection.get(fieldName);
    }

    public static boolean collectionHasSingleEntry(String collectionName) {
        if (!(getResults(collectionName).size() == 1)) {
            info("В коллекции ответа SQL-запроса более одной записи.\n" +
                    "Скорректируйте запрос, чтобы он выдавал одну строку.");
            return false;
        }
        return getResults(collectionName).size() == 1;
    }
}
