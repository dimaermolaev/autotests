package ru.digital_spirit.qaaf.steps;

import io.cucumber.java.ru.Дано;
import ru.digital_spirit.qaaf.utils.tgbot.TGBotConfiguration;
import ru.digital_spirit.qaaf.utils.tgbot.TGBotHelper;

import static ru.digital_spirit.qaaf.utils.LogHelper.info;

public class TGBotSteps {
    @Дано("Активировать телеграм-бота")
    public void activateTelegramBot() {
        TGBotConfiguration botConfig = TGBotHelper.activateTGBot();

        info(String.format("Активирован бот для отправки сообщений в чат '%s'", botConfig.getChatID()));
    }
}
