package ru.digital_spirit.qaaf.utils.tgbot;

import org.aeonbits.owner.Config;

@Config.Sources({
        "file:config/tgbot/tgbot.properties"
})
public interface TGBotConfiguration extends Config{
    @Config.Key("tg-bot-name")
    String getName();
    @Config.Key("tg-bot-token")
    String getToken();
    @Config.Key("tg-bot-chat")
    String getChatID();
    @Config.Key("tg-bot-text")
    String getText();
    @Config.Key("tg-uri")
    String getURI();
    @Config.Key("tg-header-ct")
    String getContentType();
    @Config.Key("json-content-path")
    String getJsonContent();

}