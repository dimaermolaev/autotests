package ru.digital_spirit.qaaf;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Раннер-класс для Cucumber
 * В аннотации @CucumberOptions перечисленны параметры:
 *      glue - путь к папке, где хранятся классы с шагами автотестов (@Step)
 *      plugin - плагин для создания отчетов по работе автотестов
 *      features - папка в которой Cucumber будет испкать .feature-файлы для запуска
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        glue = {"ru.digital_spirit.qaaf.steps"},
        plugin = {"io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"},
        features = {"src/test/resources"}
)
public class CucumberRunnerTest {
}
