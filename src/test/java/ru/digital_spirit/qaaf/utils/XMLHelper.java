package ru.digital_spirit.qaaf.utils;

import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;

import static ru.digital_spirit.qaaf.utils.vars.LocalVariablesManager.setVar;

public class XMLHelper extends XMLManager{

    private static Map<String, Document> xmlCollection;
    public static void saveXML(String xmlVar, Document xmlDocument) {
        if (xmlCollection == null) {
            xmlCollection = new HashMap<>();
        }
        setVar(xmlVar, "xmlDocument");
        xmlCollection.put(xmlVar, xmlDocument);
    }

    public static Document getSavedXML(String xmlName) {
        return xmlCollection.getOrDefault(xmlName, null);
    }
}
