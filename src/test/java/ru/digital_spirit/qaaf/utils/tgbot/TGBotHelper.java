package ru.digital_spirit.qaaf.utils.tgbot;

import org.aeonbits.owner.ConfigFactory;

import static ru.digital_spirit.qaaf.utils.vars.LocalVariablesManager.setVar;

public class TGBotHelper {

    private static boolean botIsActive = false;

    public static TGBotConfiguration activateTGBot() {
        TGBotConfiguration botConfig = ConfigFactory.create(TGBotConfiguration.class);
        botIsActive = true;
        setVar("tg-bot-name", botConfig.getName());
        setVar("tg-bot-token", botConfig.getToken());
        setVar("tg-bot-chat", botConfig.getChatID());
        setVar("tg-bot-text", botConfig.getText());
        setVar("tg-tg-uri", botConfig.getURI());
        setVar("tg-header-ct", botConfig.getContentType());
        setVar("json-content-path", botConfig.getJsonContent());
        return botConfig;
    }




}
