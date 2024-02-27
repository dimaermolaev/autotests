package ru.digital_spirit.qaaf.steps;

import io.cucumber.docstring.DocString;
import io.cucumber.java.ru.Дано;
import io.qameta.allure.model.Status;
import ru.digital_spirit.qaaf.utils.*;
import ru.digital_spirit.qaaf.utils.types.DocType;

import java.util.List;
import java.util.Map;

import static ru.digital_spirit.qaaf.utils.JsonHelper.*;
import static ru.digital_spirit.qaaf.utils.LogHelper.*;
import static ru.digital_spirit.qaaf.utils.RESTHelper.getResponse;
import static ru.digital_spirit.qaaf.utils.XMLManager.getXPathValue;
import static ru.digital_spirit.qaaf.utils.XMLManager.parseXMLString;
import static ru.digital_spirit.qaaf.utils.vars.LocalVariablesManager.*;

public class VarsSteps {

    @Дано("Сравнить значения полей в переменных {string} и {string} по таблице {string}")
    public void matchFieldsByMap(String var1, String var2, String mapVar) {
        // Получаем список ранее сохраненных полей
        Map<String, String> map = JsonHelper.getSavedFields(mapVar);
        if (map == null || map.isEmpty()) {
            failed("В коллекции нет полей для сраввнения: " + mapVar);
        }

        // Выполняем сопоставление значений полей по списку коллекции
        Map<List<String>, Boolean> results = compareFields(var1, var2, map);

        // Добавляем результаты в отчет
        results.forEach((listField, status) ->
                statusReporter(String.join("\n", listField), status));

        if (results.containsValue(Boolean.FALSE)) {
            stopStepAndSetStatus(Status.FAILED);
        }
    }

    @Дано("Убедиться, что в переменной {string} содержатся все поля из таблицы {string} стоблец {int}")
    public void checkFieldsExistsStep(String jsonVar, String mapVar, Integer colNumber) {
        // Извлекаем значения переменных из хранилища
        if (colNumber > 2 || colNumber < 1) {
            failed("Неправильно указан номер столбца (1 или 2)");
        }
        DocHelper doc = new DocHelper(jsonVar);
        Map<String, String> map = getSavedFields(mapVar);

        // Убеждаемся, что коллекция с полями не пустая
        if (map == null || map.isEmpty()) {
            failed("В коллекции нет полей для сраввнения: " + mapVar);
        }

        // Если указан столбец 1, то проверяем по списку ключей, в противном случае берем список значений мапы
        Map<String, Boolean> results = compareFieldsExists(doc, colNumber == 1
                ? map.keySet().stream().toList()
                : map.values().stream().toList());

        // Добавляем в отчет
        results.forEach(LogHelper::statusReporter);
        info(results.containsValue(Boolean.FALSE) ?
                "Не все поля из коллекции " + mapVar + " содержатся в объекте из переменной " + jsonVar :
                "Все поля из коллекции " + mapVar + " содержатся в объекте из переменной " + jsonVar);

        if (results.containsValue(Boolean.FALSE)) {
            stopStepAndSetStatus(Status.FAILED);
        }

    }

    @Дано("Сформировать коллекцию полей для сравнения и сохранить в переменной {string}")
    public void createFieldsMap(String mapVarName, Map<String, String> fieldsMap) {
        fieldsMap = evalVarCollection(fieldsMap);

        saveFields(mapVarName, fieldsMap);
    }

    //todo Вынести значения констант типов переменных в проперти
    //todo сохранение тела в формате стринг
    @Дано("Из переменной {string} получить {docType} и сохранить в переменной {string}")
    public void getFieldValueFromVar(String varResponse, DocType docType, String nameVar, Map<String, String> table) {
        DocHelper doc = null;
        String varContent = null;
        System.out.println("тип документа: " + docType.toString());
        if (!(varExists(varResponse))) {
            failed("В переменной '" + varResponse + "' ничего не сохранено.");
        } else if (extractValue(varResponse).equalsIgnoreCase("httpResponse")) {
            if (table.get("type").equals("xml")) {
                doc = new DocHelper(parseXMLString(getResponse(varResponse).getBody().asString()));
            } else if (table.get("type").equals("json")) {
                doc = new DocHelper(parseJSONString(getResponse(varResponse).getBody().asString()));
            } else if (table.get("type").equals("string")) {
                varContent = getResponse(varResponse).getBody().asString();
            } else {
                failed("Неверно указан тип документа");
            }
        } else if (extractValue(varResponse).equalsIgnoreCase("sqlResults")) {
            String field = evalVarString(table.get("field"));
            if (SQLHelper.collectionHasSingleEntry(varResponse)) {
                if (table.get("type").equals("json")) {
                    doc = new DocHelper(parseJSONString(SQLHelper.getResults(varResponse).get(0).get(field)));
                } else if (table.get("type").equals("xml")) {
                    doc = new DocHelper(parseXMLString(SQLHelper.getResults(varResponse).get(0).get(field)));
                } else if (table.get("type").equals("string")) {
                    varContent = SQLHelper.getResults(varResponse).get(0).get(field);
                } else {
                    failed("Неверно указан тип документа");
                }
            } else {
                failed("Не удалось получить коллекцию из SQL ответа.");
            }
        } else {
            if (table.get("type").equals("xml")) {
                doc = new DocHelper(parseXMLString(extractValue(varResponse)));
            } else if (table.get("type").equals("json")) {
                doc = new DocHelper(parseJSONString(extractValue(varResponse)));
            }else if (table.get("type").equals("string")) {
                varContent = extractValue(varResponse);
            } else {
                failed("Неверно указан тип документа");
            }
        }

        // Сохраняем документ в хранилище
        if (varContent == null) {
            assert doc != null;
            addAttachmentTxt("docContent", doc.printDoc());
            if (doc.getDocType().equals("json document")) {
                JsonHelper.saveJson(nameVar, doc.getJson());
            } else if (doc.getDocType().equals("xml document")) {
                XMLHelper.saveXML(nameVar, doc.getXML());
            } else {
                failed("Неправильный формат документа");
            }
        } else {
            setVar(nameVar, varContent);
            info("Создана переменная " + nameVar);
            addAttachmentTxt("docContent", varContent);
        }

    }

    @Дано("В переменной {string} сохранено значение {string}")
    public void createVariable(String varNm, String varVl) {
        info("Создана переменная " + varNm);
        setVar(varNm, varVl);
    }

    @Дано("В переменной {string} сохранено значение:")
    public void createStringVariable(String varNm, DocString varVl) {
        info("Создана переменная " + varNm);
        setVar(varNm, varVl.getContent().replaceAll("^\\s+", ""));
        addAttachmentJson("variable", varVl.getContent());
    }

    @Дано("Из переменной {string} сохранить значение {string} в переменную {string}")
    public void getValueFromVar(String varName, String fieldName, String newVarName) {
        try {
            String value = extractValue(varName);
            switch (value) {
                case "httpResponse" ->
                        setVar(newVarName, fieldName.equals("body") ?
                                                getResponse(varName).getBody().asString()
                                                : fieldName.equals("headers") ?
                                                getResponse(varName).getHeaders().toString()
                                                : String.valueOf(getResponse(varName).getStatusCode()));
                case "sqlResults" -> {
                    if (SQLHelper.collectionHasSingleEntry(varName)) {
                        String cellValue = evalVarString(SQLHelper.getCellValue(SQLHelper.getResults(varName).get(0), fieldName));
                        setVar(newVarName, cellValue);
                    }
                }
                case "jsonVar" ->
                        setVar(newVarName, getFieldValue(getSavedJson(varName), fieldName));
                case "xmlDocument" ->
                        setVar(newVarName, getXPathValue(XMLHelper.getSavedXML(varName), fieldName));
                default -> failed("Переменная не является коллекцией");
            }
        } catch (Exception ex) {
            info(ex.toString());
            failed("Не удалось получить значения для переменной");
        }
        info("В переменной " + newVarName + " сохранено значение: " + extractValue(newVarName));
        addAttachmentJson("variable", newVarName + " = " + extractValue(newVarName));
    }
}
