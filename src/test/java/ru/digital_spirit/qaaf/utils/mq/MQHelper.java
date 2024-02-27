package ru.digital_spirit.qaaf.utils.mq;

import ru.digital_spirit.qaaf.utils.mqconnections.MQConnectionManager;


public class MQHelper {

    MQManager mqManager;

    public MQManager getMQManager(String mqName) {
        return switch (MQConnectionManager.getConnectionParameters(mqName).mqtype()) {
            case "IBM MQ" ->
                new IBMMQManager(mqName);
            case "Kafka" ->
                new KafkaMQManager(mqName);
            default ->
                    null;
        };
    }

    public MQHelper() {
    }

    public void closeConnection() {
            mqManager.closeConnection();
    }
}