package ru.digital_spirit.qaaf.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.ru.Дано;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.digital_spirit.qaaf.utils.LogHelper.*;
import static ru.digital_spirit.qaaf.utils.RESTHelper.*;
import static ru.digital_spirit.qaaf.utils.RESTHelper.compareValues;
import static ru.digital_spirit.qaaf.utils.vars.LocalVariablesManager.evalVarCollection;
import static ru.digital_spirit.qaaf.utils.vars.LocalVariablesManager.evalVarString;

public class RESTSteps {
    @Дано("Выполнить HTTP-запрос с параметрами, результат сохранить в переменной {string}:")
    public void createAPIRequestWithParameters(String varName, Map<String, String> dataTable) {

        dataTable = evalVarCollection(dataTable);
        dataTable.forEach((k, v) -> info(k + ": " + v));

        // Выполняем запрос
        Response response = sendRequestFromTable(dataTable);

        // Ответ сохраняем в хранилище
        saveResponse(varName, response);

        // Добавляем логи и выводим статус
        addAttachmentJson("HTTP-Request", logHTTPRequest(dataTable, response));
        addAttachmentTxt("HTTP-Response", response.asPrettyString());
        info(response.getStatusLine());
    }

    @Дано("Проверить, что в переменной из HTTP-ответа {string} значения совпадают:")
    public void compareHTTPResponseValues(String varName, DataTable dataTable) {
        Response response = getResponse(varName);
        List<List<String>> tableContent = dataTable.asLists();

        tableContent.forEach(str -> {
            String key = evalVarString(str.get(0));
            String value = evalVarString(str.get(1));
            info("Проверка значения: " + key + ": " + value);
            assertTrue(compareValues(response, key, value),
                    "Значение поля " + key + " не совпало с ожидаемым");
            info("OK");
            statusReporter(key + " = " + value, true);
        });
    }
}
