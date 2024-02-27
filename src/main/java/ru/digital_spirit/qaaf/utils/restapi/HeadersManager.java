package ru.digital_spirit.qaaf.utils.restapi;

import io.restassured.http.Header;
import io.restassured.specification.RequestSpecification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс для создания header'а HTTP-запроса
 * Для создания необходима строка из поля header в автотесте.
 * Строка headerString должна состоять из параметров в виде key=value, разделенных точками с запятыми ';'
 *      пример: "key1=value1; key2=value2; key3=value3;" и т.д.
 */
public class HeadersManager {
    private final List<Header> headers;
    private String headerString;

    /**
     * Конструктор, в котором происходит парсинг строки с параметрами хедера из автотеста
     * @param headerString
     */
    public HeadersManager(String headerString) {
        headers = new ArrayList<>();
        headersParser(headerString).forEach((k, v) -> headers.add(new Header(k, v)));
    }

    /**
     * Метод, добавляющий header к HTTP-запросу
     * @param request - объект запроса типа RequestSpecification
     * @return - возвращает объект типа RequestSpecification с хедером
     */
    public RequestSpecification addHeaders(RequestSpecification request) {
        headers.forEach(request::header);
        return request;
    }

    /**
     * Метод для парсинга строки с парметрами хедера
     * @param headerString - строка, содержащая параметры хедера из автотеста
     * @return возвращает коллекцию параметров хедера в виде Map<String, String>
     */
    private Map<String, String> headersParser(String headerString) {
        String[] parameters = headerString.replaceAll("^\\s+|\\B\\s|\\s+$", "").split(";");
        Map<String, String> out = new HashMap<>();
        for (String parameter : parameters) {
            if (parameter.isBlank()) {
                continue;
            }
            if (parameter.contains("=")) {
                out.put(parameter.split("=")[0], parameter.split("=")[1]);
            }
        }
        return out;
    }
}
