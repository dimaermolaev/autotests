package ru.digital_spirit.qaaf.utils.restapi;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

/**
 * Класс для создания HTTP-запроса.
 * Для создания объекта запроса необходимо указать тип запроса и URI
 * Для отправки запроса используется метод sendRequest()
 * !!! Хедер и тело запроса создаются отдельными классами: HeaderManager и BodyManager
 */
public class RequestManager {

    private final Method method;
    private final RequestSpecification requestSpecification;

    /**
     * Конструктор класса, на вход принимает два стринга:
     * @param type - тип HTTP-запроса
     * @param uri - адрес запроса
     */
    public RequestManager(String type, String uri) {
        requestSpecification = new RequestSpecBuilder().build();
        method = Method.valueOf(type);
        requestSpecification.baseUri(uri);
    }

    /**
     * Метод отправки запроса.
     * Используется после того, как полностью закончено формирование тела запроса
     *                      (если требуется добавляется хедер, тело, параметры через вспомогательные классы)
     * @return - возвращает обхект класса Response с ответом от сервера.
     */
    public Response sendRequest() {
        return given().spec(requestSpecification).
                log().all().
                when().request(method);
    }

    /**
     * Геттер поля requestSpecification
     * @return - возвращает объект типа RequestSpecification созраненного в поле requestSpecification
     * Используется для создания параметров, хедера и тела запроса
     */
    public RequestSpecification getRequestSpecification() {
        return requestSpecification;
    }

    /**
     * Метод для настройки параметров аутентификации
     * @param login - имя пользователя
     * @param password - пароль
     * @return - возвращает объект типа RequestSpecification созраненного в поле requestSpecification
     *                                  с учетом параметров аутентификации
     */
    public RequestSpecification setAuth(String login, String password) {
        return requestSpecification.auth().preemptive().basic(login, password);
    }
}
