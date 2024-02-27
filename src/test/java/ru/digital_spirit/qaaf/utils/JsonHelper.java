package ru.digital_spirit.qaaf.utils;

import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static ru.digital_spirit.qaaf.utils.vars.LocalVariablesManager.setVar;

public class JsonHelper extends JsonManager{

    private static final Map<String, JsonElement> jsonCollection = new HashMap<>();

    private static Map<String, Map<String, String>> fieldsCollection;

    public static void saveFields(String varName, Map<String, String> fieldsMap) {
        if (fieldsCollection == null) {
            fieldsCollection = new HashMap<>();
        }
        setVar(varName, "fieldsMap");
        fieldsCollection.put(varName, fieldsMap);
    }

    public static JsonElement getSavedJson(String jsonName) {
        return jsonCollection.get(jsonName);
    }

    public static void saveJson(String jsonName, JsonElement json) {
        setVar(jsonName, "jsonVar");
        jsonCollection.put(jsonName, json);
    }

    public static boolean hasSavedJson(String jsonName) {
        return jsonCollection.containsKey(jsonName);
    }

    /**
     * Метод для сравнения значений полей в двух файлах по списку
     * и получения коллекции с результатами, для удобного отображения в отчете
     * В зависисмости от типа переменной, сравниваться будет JSON или XML
     *
     * @param var1 - JsonElement для проверки
     * @param var2 - XML в формате Document для проверки
     * @param fieldMap - список полей для сравнения в виде Map<String, String>.
     *                 путь в ключе - соответствует jsonPath json- файла
     *                 путь в значении - соответствует xpath xml- файла
     * @return - возвращает объект типа Map<List<String>, Boolean> который
     *                  в ключе содержит список стрингов в виде путь(jsonPath) = значение(value)
     *                  в значении содержится результат сравнения (true/false)
     */
    public static Map<List<String>, Boolean> compareFields(String var1, String var2, Map<String, String> fieldMap) {
        // Коллекция для сбора результатов
        Map<List<String>, Boolean> resultMap = new HashMap<>();

        DocHelper doc1 = new DocHelper(var1);
        DocHelper doc2 = new DocHelper(var2);

        fieldMap.forEach((k, v) -> {
            // Если какое-либо поле содержит '[i]', то меняем его на первый элемент коллекции ('[0]')
            if (k.contains("[") || v.contains("[")) {
                k = k.replaceAll("\\[[1-9a-z]\\]", "\\[0\\]");
                v = v.replaceAll("\\[[1-9a-z]\\]", "\\[0\\]");
            }
            if (k.contains("=")) {
                String field = k.split("=")[0];
                String cnst = k.split("=")[1];

                String field1Content = doc1.getFieldValue(field);
                resultMap.put(List.of(field, " = " + cnst),
                        Objects.equals(field1Content.toUpperCase(), cnst.toUpperCase()));
            } else {
                // При помощи метода getFieldValue() получаем значение,
                // которое хранится по пути соответствующего json'a
                String field1Content = doc1.getFieldValue(k);
                String field2Content = doc2.getFieldValue(v);

                // Готовим для отчета результат в виде
                // путь(jsonPath/xpath) = значение(value)
                String field1 = k + " = " + field1Content;
                String field2 = v + " = " + field2Content;

                // Добавляем в коллекцию для отчета результаты,
                // объединяя строки с результатами в коллекцию.
                if (field1Content.equals("Тег не найден") || field2Content.equals("Тег не найден")) {
                    resultMap.put(List.of(field1, field2),
                            false);
                } else {
                    resultMap.put(List.of(field1, field2),
                            Objects.equals(field1Content,field2Content));
                }
            }
        });
        // Просто вывод результатов на экран.
        // Можно закомментить если мешает.
        resultMap.forEach((fields, result) -> {
            System.out.println(fields.get(0) + " : " + fields.get(1));
            System.out.println(result);
        });
        return resultMap;
    }

    public static Map<String, String> getSavedFields(String mapVar) {
        return fieldsCollection.get(mapVar);
    }

    /**
     * Метод для получения результатов проверки на наличие полей в json'e из списка List<String>
     * @param doc - объект DocHelper для проверки
     * @param fieldsList - список полей для проверки типа List<String>
     * @return - возвращает результаты проверки в виде Map<String, Boolean>
     */
    public static Map<String, Boolean> compareFieldsExists(DocHelper doc, List<String> fieldsList) {
        Map<String, Boolean> resultList = new HashMap<>();
        fieldsList.forEach(field -> {
            if (field.contains("[")) {
                field = field.replaceAll("\\[[1-9a-z]\\]", "\\[0\\]");
            }
            if (field.contains("=")) {
                field = field.split("=")[0];
            }
            resultList.put(field, doc.containsField(field));
        });
        return resultList;
    }
}
