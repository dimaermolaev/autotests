package ru.digital_spirit.qaaf.utils.mq;

import com.ibm.mq.MQMessage;

import java.util.List;
import java.util.Map;

public class KafkaMQManager implements MQManager {
    public KafkaMQManager(String mqName) {
    }

    @Override
    public MQManager getMQManager() {
        return null;
    }

    @Override
    public void closeConnection() {

    }

    @Override
    public String sendMessage(String message) {
        return null;
    }

    @Override
    public String getMessageByMessageID(String messageID) {
        return null;
    }

    @Override
    public void printAllMessages() {

    }

    @Override
    public List<MQMessage> getAllMessages() {
        return null;
    }

    @Override
    public Map<String, String> getMessageByParameters(Map<String, String> parameters) {
        return null;
    }
}
