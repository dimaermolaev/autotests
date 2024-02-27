package ru.digital_spirit.qaaf.utils.mqconnections;

import org.aeonbits.owner.Config;

/**
 * Интерфейс для создания объктов с параметрами подключения к системам очередей
 * mqtype - Имя системы очередей (IBM MQ, Kafka MQ, RabbitMQ и т.д.)
 * hostname - адрес подключения
 * port - порт подключения
 * userid - имя ползователя для подключения
 * password - пароль для подключения
 * mqmanager - название менеджера очередей
 * queue - очередь
 * channel - канал
 */
@Config.Sources({
        "file:config/mq/mq.properties"
})
public interface MQConnectionParameters extends Config{

    @Key("${mq}.mqtype")
    String mqtype();
    @Key("${mq}.hostname")
    String hostname();
    @Key("${mq}.port")
    Integer port();
    @Key("${mq}.userid")
    String userid();
    @Key("${mq}.password")
    String password();
    @Key("${mq}.mqmanager")
    String mqmanager();
    @Key("${mq}.queue")
    String queue();
    @Key("${mq}.channel")
    String channel();
}
