package ru.digital_spirit.qaaf.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.restassured.path.json.JsonPath;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Класс для работы с данными типа JSON
 * parseJSONFile - парсер json-файлов
 * parseJSONString - парсер строки с json
 */
public class JsonManager {

    /**
     * Метод для парсинга файла содержащего JSON
     * @param path - принимает на вход строку, содержащую путь к расположению файла
     * @return - возвращает объект типа JsonElement
     */
    public static JsonElement parseJSONFile(String path) {
        Path path1 = Path.of(path);
        StringBuilder jsonString = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(path1.toString()))) {
            br.lines().forEach(jsonString::append);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return parseJSONString(jsonString.toString());
    }

    /**
     * Метод для проверки, содержится ли поле по jsonPath
     * @param json - объект JsonElement для проверки
     * @param field - путь jsonPath
     * @return - возвращает true/false
     */
    public static boolean jsonContainsField(JsonElement json, String field) {
        boolean contains;
        try {
            contains = JsonPath.from(json.toString()).get(field) != null;
        } catch (IllegalArgumentException ex) {
            return false;
        }
        return contains;
    }

    /**
     * Метод, проверяющий, соответствует ли значение по jsonPath ожидаемому
     * @param json - объект JsonElement, в котором происходит проверка
     * @param field - jsonPath поля, в котором содержится значение
     * @param value - ожидаемое значение, с которым сравнивается
     * @return - true/false
     */
    public static boolean jsonFieldValueAssert(JsonElement json, String field, String value) {
        String val = String.valueOf(Optional.of(
                JsonPath.from(json.toString()).get(field)).orElse("null"));
        return val.equals(value);
    }

    /**
     * Метод для парсинга стоки содержащей JSON
     * @param jsonString - принимает на вход строку, содержащую JSON
     * @return - возвращает объект типа JsonElement
     */
    public static JsonElement parseJSONString(String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            throw new NullPointerException("Передаваемая строка с JSON пуста\n" +
                                            "Убедитесь, что в парсер передаются корректные данные");
        }
        return JsonParser.parseString(jsonString.replaceAll("\\n", "")).deepCopy();
    }

    /**
     * Метод для поиска значения в JSON-файле по имени поля
     * @param json - объект типа JsonElement содержащий JSON
     * @param filedName - имя поля для поиска
     * @return - возвращает String со значением
     */
    public static String getFieldValue(JsonElement json, String filedName) {
        StringBuilder value = new StringBuilder();
        try {
            Optional.ofNullable(json).ifPresent(
                    jsonElement -> value.append(
                            Optional.ofNullable(
                                    JsonPath.from(jsonElement.toString()).get(filedName)
                            ).orElse("null")
                    ));
        } catch (IllegalArgumentException ex) {
            return "Не удалось спарсить jsonPath: " + filedName;
        }
        return value.toString().equals("null") ? "Тег не найден" : value.toString();
    }

    /**
     * Метод для вывода в консоль структуры JSON-объекта
     * @param savedJson - объект типа JsonElement содержащий JSON
     * @return - выводит строки с содрежимым JSON
     */
    public static String printJSON(JsonElement savedJson) {
        Gson prettyJson = new GsonBuilder().setPrettyPrinting().create();
        return prettyJson.toJson(savedJson);
    }
}
