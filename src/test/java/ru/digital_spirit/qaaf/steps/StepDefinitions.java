package ru.digital_spirit.qaaf.steps;

import groovy.util.logging.Slf4j;
import io.cucumber.java.ru.Дано;
import ru.digital_spirit.qaaf.utils.JsonHelper;
import ru.digital_spirit.qaaf.utils.SQLHelper;
import ru.digital_spirit.qaaf.utils.XMLHelper;
import ru.digital_spirit.qaaf.utils.files.FileManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.digital_spirit.qaaf.utils.JsonHelper.getSavedJson;
import static ru.digital_spirit.qaaf.utils.JsonHelper.printJSON;
import static ru.digital_spirit.qaaf.utils.LogHelper.*;
import static ru.digital_spirit.qaaf.utils.RESTHelper.printResponse;
import static ru.digital_spirit.qaaf.utils.XMLHelper.getPrettyXMLString;
import static ru.digital_spirit.qaaf.utils.vars.LocalVariablesManager.*;

@Slf4j
public class StepDefinitions {

    @Дано("Вывести на экран значение переменной {string}")
    public void printVar(String var) {
        String value = evalVarString(extractValue(var));
        switch (value) {
            case "httpResponse" ->
                    info(printResponse(var));
            case "xmlDocument" ->
                    info(getPrettyXMLString(XMLHelper.getSavedXML(var)));
            case "sqlResults" ->
                    SQLHelper.getResults(var).forEach(entry ->
                    entry.forEach((field, val) -> info(field + ": " + val)));
            case "jsonVar" -> {
                if (JsonHelper.hasSavedJson(var)) {
                    info(printJSON(getSavedJson(var)));
                } else {
                    failed("Невозможно получить сохраненный JSON");
                }
            }
            default -> info(value);
        }
        addAttachmentJson("variable", var + ": " + value);
    }

    @Дано("Сохранить в переменной {string} содержимое файла {string}")
    public void saveFileContentToVar(String var, String path) {
        setVar(var, FileManager.getFileContent(path));
        info("Создана переменная " + var);
        addAttachmentTxt("fileContent", extractValue(var));
    }

    @Дано("Проверить, что переменная {string} содержит значения:")
    public void checkVariableContains(String varName, List<String> listValues) {
        listValues = evalVarCollection(listValues);
        String varContent = extractValue(varName);

        listValues.forEach(value -> {
            info("Проверка наличия значения в переменной: " + value);
            assertTrue(varContent.contains(value),
                    "Значение " + value + " не содержится в переменной " + varName);
            info("OK");
            statusReporter(value, true);
        });
    }

    @Дано("Ожидать {string} сек.")
    public void waitNSec(String sec) {
        System.out.println("Ожидание (сек.): " + sec);
        try {
            Thread.sleep(Long.parseLong(sec) * 1000);
            info(String.format("Окончено ожидание: %s секунд", sec));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }//todo Добавить умные ожидалки Awaitility
}