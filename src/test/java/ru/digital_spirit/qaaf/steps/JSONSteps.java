package ru.digital_spirit.qaaf.steps;

import com.google.gson.JsonElement;
import io.cucumber.java.ru.Дано;
import ru.digital_spirit.qaaf.utils.JsonHelper;
import ru.digital_spirit.qaaf.utils.LogHelper;
import ru.digital_spirit.qaaf.utils.SQLHelper;

import java.util.Map;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.digital_spirit.qaaf.utils.JsonHelper.getSavedJson;
import static ru.digital_spirit.qaaf.utils.JsonManager.*;
import static ru.digital_spirit.qaaf.utils.LogHelper.*;
import static ru.digital_spirit.qaaf.utils.RESTHelper.getResponse;
import static ru.digital_spirit.qaaf.utils.vars.LocalVariablesManager.*;

public class JSONSteps {
    @Дано("Получить JSON из переменной {string} и сохранить в переменной {string}")
    public void getJSONFromVar(String varResponse, String nameJsonVar) {
        // Получаем сохраненную строку с json и создаем JsonElement
        JsonElement json = null;
        if (!(varExists(varResponse))) {
            LogHelper.failed(varResponse + ": В переменной ничего не сохранено");
        } else if (extractValue(varResponse).equalsIgnoreCase("httpResponse")) {
            json = getResponse(varResponse).getBody().as(JsonElement.class);
        } else if (extractValue(varResponse).equalsIgnoreCase("sqlResults")) {
            json = parseJSONString(SQLHelper.getResults(varResponse).get(0).get("JSON"));
        } else {
            json = parseJSONString(extractValue(varResponse));
        }

        // Сохраняем JsonElement в хранилище
        JsonHelper.saveJson(nameJsonVar, json);
        addAttachmentTxt("jsonContent", printJSON(json));
    }

    @Дано("Проверить, что в переменной из JSON-объекта {string} значения совпадают:")
    public void compareJSONVarValues(String varName, Map<String, String> values) {
        values = evalVarCollection(values);
        JsonElement json = getSavedJson(varName);

        values.forEach((key, value) -> {
            info("Проверка значения: " + key + ": " + value);
            assertEquals(value, getFieldValue(json, key),
                    "Значение поля " + key + " не совпало с ожидаемым");
            info("OK");
            statusReporter(key + " = " + value, true);
        });
    }

    @Дано("Проверить, что json в переменной {string} совпадает с {string}")
    public void compareJSONSFromVars(String jsonVar1, String jsonVar2) {

        String jsonSting1 = JsonHelper.hasSavedJson(jsonVar1) ?
                JsonHelper.getSavedJson(jsonVar1).toString() :
                extractValue(jsonVar1);

        String jsonSting2 = JsonHelper.hasSavedJson(jsonVar2) ?
                JsonHelper.getSavedJson(jsonVar2).toString() :
                extractValue(jsonVar2);

        info(String.format("Сравнение JSON-объектов в переменных %s и %s", jsonVar1, jsonVar2));
        addAttachmentTxt("json1", jsonSting1);
        addAttachmentTxt("json2", jsonSting2);
        assertThatJson(jsonSting1).isEqualTo(jsonSting2);
        info("OK");
    }
}
