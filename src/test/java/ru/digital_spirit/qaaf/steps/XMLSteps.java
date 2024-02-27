package ru.digital_spirit.qaaf.steps;

import io.cucumber.java.ru.Дано;
import io.restassured.internal.matcher.xml.XmlXsdMatcher;
import org.w3c.dom.Document;
import ru.digital_spirit.qaaf.utils.LogHelper;
import ru.digital_spirit.qaaf.utils.SQLHelper;
import ru.digital_spirit.qaaf.utils.XMLHelper;
import ru.digital_spirit.qaaf.utils.XMLManager;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.digital_spirit.qaaf.utils.LogHelper.*;
import static ru.digital_spirit.qaaf.utils.RESTHelper.getResponse;
import static ru.digital_spirit.qaaf.utils.XMLManager.*;
import static ru.digital_spirit.qaaf.utils.vars.LocalVariablesManager.*;

public class XMLSteps {
    @Дано("Получить XML из переменной {string} и сохранить в переменной {string}")
    public void getXMLFromVar(String varResponse, String nameXMLVar) {
        // Получаем сохраненную строку с XML и создаем Document
        Document xml = null;
        if (!(varExists(varResponse))) {
            LogHelper.failed("В переменной ничего не сохранено");
        } else if (extractValue(varResponse).equalsIgnoreCase("httpResponse")) {
            xml = getResponse(varResponse).getBody().as(Document.class);
        } else if (extractValue(varResponse).equalsIgnoreCase("sqlResults")) {
            xml = parseXMLString(SQLHelper.getResults(varResponse).get(0).get("XML"));
        } else {
            xml = parseXMLString(extractValue(varResponse));
        }

        // Сохраняем XMLDocument в хранилище
        XMLHelper.saveXML(nameXMLVar, xml);
        addAttachmentTxt("xmlContent", getPrettyXMLString(xml));
    }

    @Дано("Проверить, что в переменной из XML-объекта {string} значения совпадают:")
    public void compareXMLVarValues(String varName, Map<String, String> values) {
        values = evalVarCollection(values);
        Document xml = XMLHelper.getSavedXML(varName);

        values.forEach((key, value) -> {
            info("Проверка значения: " + key + ": " + value);
            assertEquals(value, getXPathValue(xml, key),
                    "Значение поля " + key + " не совпало с ожидаемым");
            info("OK");
            statusReporter(key + " = " + value, true);
        });
    }

    @Дано("Проверить, что XML из переменной {string} соответствует схеме {string}")
    public void validateXMLByXSD(String xmlVar, String xsdVar) {
        String xml = extractValue(xmlVar);
        String xsd = extractValue(xsdVar);
        XmlXsdMatcher xsdMatcher = XmlXsdMatcher.matchesXsd(xsd);
        assertTrue(xsdMatcher.matches(
                XMLManager.convertXMLToString(
                        XMLManager.parseXMLString(xml))),
                String.format("XML в переменной %s не соответствует XSD-схеме в переменной %s", xmlVar, xsdVar));
        addAttachmentTxt("XMLContent", xml);
        addAttachmentTxt("XSDContent", xsd);
    }
}
