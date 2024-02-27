package ru.digital_spirit.qaaf.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.digital_spirit.qaaf.steps.StepDefinitions;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Класс-утилита для логгирования
 * addAttachment - прикрепление файла к шагу в отчете
 * info - лог в консоль уровня INFO
 */
public class LogHelper {

    private static final Logger logger = LoggerFactory.getLogger(StepDefinitions.class);

    /**
     * Метод для прикрепления к отчету текстового файла с выводом большого объема текста
     * Принимает на вход название файла и текст вывода.
     * @param name - имя файла в отчете
     * @param log  - текст вывода для сохранения
     */
    public static void addAttachmentTxt(String name, String log) {
        Allure.addAttachment(name, "plaint/text", log);
    }

    /**
     * Метод для прикрепления к отчету текстового файла с выводом большого объема текста
     * Принимает на вход название файла, текст вывода и расширение файла.
     * @param name - имя файла в отчете
     * @param log  - текст вывода для сохранения
     * @param extension - расширения файла
     */
    public static void addAttachmentTxt(String name, String log, String extension) {
        Allure.addAttachment(name, "plaint/text", log, extension);
    }

    /**
     * Метод для прикрепления к отчету раскрываемого блока, который можно развернуть прямо в отчете
     * Принимает на вход название файла и текст вывода.
     * @param name - имя файла в отчете
     * @param log  - текст вывода для сохранения
     */
    public static void addAttachmentJson(String name, String log) {
        Allure.addAttachment(name, "application/json", log);
    }


    /**
     * Метод для вывода в консоль логов уровня INFO
     * @param log - строка вывода
     */
    public static void info(String log) {
        logger.info(log);
    }

    /**
     * Метод для отображения статуса шага в отчете (галочка, крестик)
     * @param log    - Текст проверки
     * @param status - статус шага
     */
    public static void statusReporter(String log, Boolean status) {
        Allure.step(log, status
                ? Status.PASSED
                : Status.FAILED);
    }

    /**
     * Метод для вывода ошибки теста
     * @param failureString - строка с текстом ошибки
     */
    public static void failed(String failureString) {
        throw new AssertionError(failureString);
    }

    /**
     * Метод для изменения статуса шага
     * При этом завершается работа шага (использовать в конце метода)
     * @param status - иодин из enum io.qameta.allure.model.Status
     *               FAILED, BROKEN, PASSED, SKIPPED;
     */
    public static void stopStepAndSetStatus(Status status) {
        Allure.getLifecycle().updateStep(step -> step.setStatus(status));
        Allure.getLifecycle().stopStep();
    }

    /**
     * Метод для логирования параметров HTTP-запросов
     *
     * @param params   - параметры HTTP-запроса
     * @param response - ответ в виде объекта Response
     * @return - возвращает стринг с логом
     */
    public static String logHTTPRequest(Map<String, String> params, Response response) {
        StringBuilder sb = new StringBuilder();
        params.forEach((k, v) -> {
            if (k.equalsIgnoreCase("type")) {
                sb.append(k.toUpperCase(Locale.ROOT)).append(": ")
                        .append(v.toUpperCase(Locale.ROOT))
                        .append(System.lineSeparator());
            } else if (Stream.of("uri", "url", "address", "path", "endpoint")
                    .anyMatch(k::equalsIgnoreCase)) {
                sb.append(k.toUpperCase(Locale.ROOT)).append(": ")
                        .append(v)
                        .append(System.lineSeparator());
            } else if (k.equalsIgnoreCase("json")) {
                Gson gsonBuilder = new GsonBuilder().setPrettyPrinting().create();
                JsonElement jsonElement = JsonManager.parseJSONString(v);
                sb.append(k.toUpperCase(Locale.ROOT)).append(": \n")
                        .append(gsonBuilder.toJson(jsonElement))
                        .append(System.lineSeparator());
            } else {
                sb.append(k).append(": ").append(v)
                        .append(System.lineSeparator());
            }

            if (response == null) {
                sb.append("Ответ от сервера не был получен");
            } else {
                sb.append(System.lineSeparator());
            }
        });

        return sb.toString();
    }

}
