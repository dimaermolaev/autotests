package ru.digital_spirit.qaaf.utils;

import io.restassured.response.Response;
import ru.digital_spirit.qaaf.utils.restapi.BodyManager;
import ru.digital_spirit.qaaf.utils.restapi.HeadersManager;
import ru.digital_spirit.qaaf.utils.restapi.RequestManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.digital_spirit.qaaf.utils.LogHelper.failed;
import static ru.digital_spirit.qaaf.utils.vars.LocalVariablesManager.setVar;

/**
 * Класс для работы с HTTP-запросами и ответами
 * createAPIRequest - простейший метод для отправки HTTP-запроса по типу и адресу
 * executeRequestFromTable - метод для отправки HTTP-запроса на основе параметров из автотеста
 * saveResponse - метод для сохранения в памяти результатов ответа сервера
 * compareValues - метод для проверки значений в ответе
 */
public class RESTHelper {

    private static final Map<String, Response> responses = new HashMap<>();

    /**
     * Метод для отправки HTTP-запроса на основе параметров из автотеста.
     * Если среди параметров запроса есть хедер или тело, то создает их.
     * Выполняет запрос к серверу.
     *
     * @param params - Принимает параметры из автотеста в виде Map<String, String>
     * @return - возвращает объект типа Response, содержащий ответ от сервера
     */
    public static Response sendRequestFromTable(Map<String, String> params) {
        RequestManager requestManager = new RequestManager(params.get("type"), params.get("uri"));
        if (params.containsKey("headers")) {
            HeadersManager headersManager = new HeadersManager(params.get("headers"));
            headersManager.addHeaders(requestManager.getRequestSpecification());
        }
        if (params.containsKey("body")) {
            params.put("body", params.get("body").replaceAll("^\\s+", ""));
            BodyManager bodyManager = new BodyManager();
            if (params.get("body").startsWith("{")) {
                bodyManager.setJson(JsonManager.parseJSONString(params.get("body")));
            } else if (params.get("body").startsWith("<")) {
                bodyManager.setXml(XMLManager.parseXMLString(params.get("body")));
            }
            bodyManager.setBodyParameters(requestManager.getRequestSpecification());
        }
        if (params.containsKey("login") ||
            params.containsKey("username")) {
            String login = params.containsKey("login") ? params.get("login") : params.get("username");
            String password = params.getOrDefault("password", null);
            requestManager.setAuth(login, password);
        }
        return requestManager.sendRequest();
    }

    /**
     * Метод для сохранения в памяти результатов ответа сервера.
     * Используется для того, чтобы можно было хранить и передавать ответы от сервера между шагами автотеста.
     *
     * @param responseName - имя сохраненного ответа в виде строки
     * @param response     - возвращает объект типа Response с ответом
     */
    public static void saveResponse(String responseName, Response response) {
        setVar(responseName, "httpResponse");
        responses.put(responseName, response);
    }

    /**
     * Метод для извлечения из хранилища ответа по заданному имени
     *
     * @param responseName - имя сохраненного ответа в виде строки
     * @return - возвращает объект типа Response с ответом
     */
    public static Response getResponse(String responseName) {
        if (!(hasResponseInCollection(responseName))) {
            failed(
                    responseName + ": В переменной не сохоранено значений");
        }
        return responses.get(responseName);
    }
    public static boolean hasResponseInCollection(String responseName) {
        return responses.containsKey(responseName);
    }

    /**
     * Простейший метод для отправки HTTP-запроса по типу и адресу.
     * Используется в случаях, когда нужно отправить только запрос нужно типа на конкретный адрес,
     * при этом не используя параметры, хедер или тело.
     *
     * @param type - типа HTTP-запроса.
     * @param uri  - адрес запроса
     * @return - возвращает объект типа Response с ответом
     */
    public static Response createAPIRequest(String type, String uri) {
        return new RequestManager(type, uri).sendRequest();
    }

    /**
     * Метод для проверки значений в теле ответа.
     * Проверяет соответствие значений в ответе ожидаемым.
     *
     * @param response - объект с телом ответа от сервера
     * @param key      - название проверяемого поля в ответе.
     * @param value    - значение проверяемого поля в ответе.
     * @return - возвращает true/false
     */
    public static boolean compareValues(Response response, String key, String value) {
        return switch (key.toLowerCase()) {
            case "statuscode" -> response.statusCode() == Integer.parseInt(value);
            case "contenttype" -> response.getContentType().contains(value);
            case "statusline" -> response.getStatusLine().contains(value);
            case "sessionid" -> String.valueOf(response.getSessionId()).equalsIgnoreCase(value);
            case "headers" -> compareHeadersValue(response, value);
            case "body" -> compareBodyValue(response, value);
            default -> false;
        };
    }

    private static boolean compareBodyValue(Response response, String value) {
        if (!(value.contains("=") && value.contains(":"))) {
            failed("""
                    Неправильная запись значения Body.
                    Используйте формат type:key=value
                    type - text, xml(html) или json
                    key=value - xpath(jsonpath)=value с разделителем '='
                    """);
        }
        String bodyContentType = value.split(":")[0];
        String bodyFieldName = "";
        String bodyFieldValue = "";
        if (value.contains("=")) {
            bodyFieldName = value.split(":")[1].split("=")[0];
            bodyFieldValue = value.split(":")[1].split("=")[1];
        } else {
            bodyFieldValue = value.split(":")[1];
        }
        if (bodyContentType.equalsIgnoreCase("json")) {
            return JsonManager.jsonFieldValueAssert(
                    JsonManager.parseJSONString(response.getBody().asString()),
                    bodyFieldName,
                    bodyFieldValue
            );
        } else if (List.of("xml", "html").contains(bodyContentType.toLowerCase())) {
            return  XMLManager.getTagByXpath(response.getBody().asString(), bodyFieldName).equals(bodyFieldValue);
        } else if (bodyContentType.equalsIgnoreCase("text")) {
            return  response.getBody().asString().contains(bodyFieldValue);
        } else {
            failed("""
                    Ошибка!
                    Неправильный формат Body
                    Используйте xml, html, json, text
                    """);
        }
        return false;
    }

    private static boolean compareHeadersValue(Response response, String value) {
        if (!value.contains("=")) {
            failed("Неправильная запись значения Headers.\n" +
                    "Используйте формат key=value с разделителем '='");
        }
        String headerName = value.split("=")[0];
        String headerValue = value.split("=")[1];
        return String.valueOf(response.getHeaders().getValue(headerName))
                .equals(headerValue);
    }

    public static String printResponse(String var) {
        Response r = getResponse(var);

        return System.lineSeparator() +
                "StatusLine = " + r.getStatusLine() + System.lineSeparator() +
                "StatusCode = " + r.getStatusCode() + System.lineSeparator() +
                "SessionID = " + r.getSessionId() + System.lineSeparator() +
                "ContentType = " + r.getContentType() + System.lineSeparator() +
                "Headers = " + r.getHeaders() + System.lineSeparator() +
                "Body = " + r.getBody().asPrettyString() + System.lineSeparator();
    }
}
