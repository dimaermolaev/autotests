package ru.digital_spirit.qaaf.utils.restapi;

import com.google.gson.JsonElement;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.w3c.dom.Document;
import ru.digital_spirit.qaaf.utils.XMLManager;

import java.util.Map;

public class BodyManager {

    /**
     * Класс для создания тела REST-запроса и применения параметров
     * Для использования создается объект класса,
     * используя сеттеры добавляются параметры и данные в виде json/xml,
     * в метод setBodyParameters передается объект типа RequestSpecification,
     * возвращает объект типа RequestSpecification с телом
     */
    private Map<String, String> parameters;
    private JsonElement json;
    private Document xml;

    /**
     * Метод для добавления тела запроса
     * Добавляет к объекту RequestSpecification параметры и тело JSON/XML
     * @param request - объект RequestSpecification, к которому добавляются параметры и тело
     * @return - возвращает измененный объект RequestSpecification
     */
    public RequestSpecification setBodyParameters(RequestSpecification request) {
        if (!(parameters == null || parameters.isEmpty())) {
            parameters.forEach(request::param);
        }
        if (!(json == null) && json.isJsonObject()) {
            request.contentType(ContentType.JSON).body(json.toString());
        }
        if (!(xml == null) && xml.hasChildNodes()) {
            request.contentType(ContentType.XML).body(XMLManager.convertXMLToString(xml));
        }
        return request;
    }

    /**
     * Набор геттеров и сеттеров для работы с полями класса
     */

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public JsonElement getJson() {
        return json;
    }

    public void setJson(JsonElement json) {
        this.json = json;
    }

    public Document getXml() {
        return xml;
    }

    public void setXml(Document xml) {
        this.xml = xml;
    }
}
