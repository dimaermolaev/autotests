package ru.digital_spirit.qaaf.utils;

import com.google.gson.JsonElement;
import org.w3c.dom.Document;

import static ru.digital_spirit.qaaf.utils.LogHelper.failed;
import static ru.digital_spirit.qaaf.utils.vars.LocalVariablesManager.extractValue;

public class DocHelper {

    private JsonElement json;
    private Document xml;

    public DocHelper(JsonElement json) {
        this.json = json;
    }

    public DocHelper(Document xml) {
        this.xml = xml;
    }
    
    public String getDocType() {
        if (json != null && xml == null) {
            return "json document";
        } else if (xml != null && json == null) {
            return "xml document";
        } else return null;
    }

    public JsonElement getJson() {
        return json;
    }

    public Document getXML() {
        return xml;
    }
    public DocHelper(String var) {
        String varType = extractValue(var);
        if (varType.equals("null")) {
            failed("Переменная имеет значение NULL\n" +
                    "Нельзя содать объект DocHelper");
        }
        if (varType.contains("json")) {
            json = JsonHelper.getSavedJson(var);
        } else if (varType.contains("xml")) {
            xml = XMLHelper.getSavedXML(var);
        }  else {
            failed("Не определен тип переменной: " + var);
        }
    }

    public String getFieldValue(String field) {
        if (json == null && xml != null) {
            return XMLManager.getXPathValue(xml, field);
        } else if (json != null && xml == null) {
            return JsonManager.getFieldValue(json, field);
        } else {
            failed("Тип документа не определен.");
            return null;
        }
    }


    public Boolean containsField(String field) {
        if (json == null && xml != null) {
            return XMLManager.xmlContainsTag(xml, field);
        } else if (json != null && xml == null) {
            return JsonManager.jsonContainsField(json, field);
        } else {
            failed("Тип документа не определен.");
            return null;
        }
    }

    public String printDoc() {
        if (json == null && xml != null) {
            return XMLManager.getPrettyXMLString(xml);
        } else if (json != null && xml == null) {
            return JsonManager.printJSON(json);
        } else {
            failed("Тип документа не определен.");
            return null;
        }
    }
}
