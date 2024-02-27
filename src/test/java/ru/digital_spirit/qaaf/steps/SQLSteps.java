package ru.digital_spirit.qaaf.steps;

import io.cucumber.docstring.DocString;
import io.cucumber.java.ru.Дано;
import ru.digital_spirit.qaaf.utils.SQLHelper;
import ru.digital_spirit.qaaf.utils.SQLRequestManager;
import ru.digital_spirit.qaaf.utils.dbconnections.DBConnectionManager;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static ru.digital_spirit.qaaf.utils.LogHelper.*;
import static ru.digital_spirit.qaaf.utils.LogHelper.failed;
import static ru.digital_spirit.qaaf.utils.vars.LocalVariablesManager.evalVarCollection;
import static ru.digital_spirit.qaaf.utils.vars.LocalVariablesManager.evalVarString;

public class SQLSteps {
    @Дано("Выполнить SQL запрос в базу {string} и сохранить результат в переменной {string}")
    public void executeSQLRequestAndSaveResultsToVar(String dbName, String varName, DocString sqlString) {
        SQLRequestManager sqlConn = SQLRequestManager.getInstance(DBConnectionManager.getConnection(dbName));
        String sqlRequest = evalVarString(sqlString.getContent().replaceAll("^\\s+|;\\s*$",""));
        assertTrue(sqlRequest.toLowerCase(Locale.ROOT).startsWith("select"),
                "Для SQL-запросов, которые вносят изменения в БД, следует использовать метод updateExecute()");
        List<Map<String, String>> result = sqlConn.executeQuery(sqlRequest);
        addAttachmentJson("SQL Request", sqlRequest);
        addAttachmentTxt("SQL Response", sqlRequest + "\n\nResponse:\n" + result.toString());
        SQLHelper.saveResults(varName, result);
    }

    @Дано("Проверить, что в переменной из SQL-запроса {string} значения совпадают:")
    public void compareSQLResponseValues(String varName, Map<String, String> dataTable) {
        dataTable = evalVarCollection(dataTable);
        if (SQLHelper.collectionHasSingleEntry(varName)) {
            Map<String, String> valuesFromResponse = SQLHelper.getResults(varName).get(0);
            dataTable.forEach((key, value) -> {
                info("Проверка значения: " + key + " = " + value);
                assertEquals(value, valuesFromResponse.get(key),
                        "Значение переменной " + key + " не совпало с ожидаемым");
                info("OK");
                statusReporter(key + " = " + value, true);
            });
        } else {
            failed("Ошибка! Не существует сохраненного SQL-ответа с именем " + varName  );
        }
    }

    @Дано("Выполнить UPDATE SQL запрос в базу {string}")
    public void executeUpdateSQLRequest(String dbName, DocString sqlReq) {
        SQLRequestManager sqlConn = SQLRequestManager.getInstance(DBConnectionManager.getConnection(dbName));
        String sqlRequest = evalVarString(sqlReq.getContent().replaceAll("^\\s+|;\\s*$",""));
        assertFalse(sqlRequest.toLowerCase(Locale.ROOT).startsWith("select"),
                "Для SQL-запросов, которые вносят изменения в БД, следует использовать метод updateExecute()");
        int num = sqlConn.updateExecute(sqlRequest);
        addAttachmentJson("sql log", sqlRequest + "\n\n" +
                "Обработано строк в таблице: " + num);
    }


}
