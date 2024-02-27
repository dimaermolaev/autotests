package ru.digital_spirit.qaaf.utils.mq;

import com.ibm.mq.MQMessage;

import java.util.List;
import java.util.Map;

public interface MQManager {
    MQManager getMQManager();
    void closeConnection();

    String sendMessage(String message);
    String getMessageByMessageID(String messageID);

    void printAllMessages();
    List<MQMessage> getAllMessages();

    Map<String, String> getMessageByParameters(Map<String, String> parameters);
}
