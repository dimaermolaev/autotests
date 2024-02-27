package ru.digital_spirit.qaaf.utils.mq;

import com.ibm.mq.*;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.MQConstants;
import org.apache.commons.codec.binary.Hex;
import ru.digital_spirit.qaaf.utils.DocHelper;
import ru.digital_spirit.qaaf.utils.JsonManager;
import ru.digital_spirit.qaaf.utils.XMLManager;
import ru.digital_spirit.qaaf.utils.mqconnections.MQConnectionManager;
import ru.digital_spirit.qaaf.utils.mqconnections.MQConnectionParameters;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static com.ibm.mq.MQEnvironment.*;
import static ru.digital_spirit.qaaf.utils.LogHelper.failed;

public class IBMMQManager implements MQManager {

    private final String mqName;
    private MQQueueManager mqQueueManager;
    private MQConnectionParameters mqConnectionParameters;


    public IBMMQManager(String mqName) {
        this.mqName = mqName;
    }

    @Override
    public MQManager getMQManager() {
        mqConnectionParameters = MQConnectionManager.getConnectionParameters(mqName);

        userID = mqConnectionParameters.userid();
        password = mqConnectionParameters.password();
        hostname = mqConnectionParameters.hostname();
        port = mqConnectionParameters.port();
        channel = mqConnectionParameters.channel();

        Hashtable<String, Object> properties = new Hashtable<>();
        properties.put(MQConstants.USER_ID_PROPERTY, userID);
        properties.put(MQConstants.PASSWORD_PROPERTY, password);
        properties.put(MQConstants.HOST_NAME_PROPERTY, hostname);
        properties.put(MQConstants.PORT_PROPERTY, port);
        properties.put(MQConstants.CHANNEL_PROPERTY, channel);
        properties.put(MQConstants.USE_MQCSP_AUTHENTICATION_PROPERTY, Boolean.FALSE);
        properties.put(MQConstants.TRANSPORT_PROPERTY, MQConstants.TRANSPORT_MQSERIES_CLIENT);

        try {
            mqQueueManager = new MQQueueManager(mqConnectionParameters.mqmanager(), properties);
        } catch (MQException e) {
            System.out.println("Не удалось установить подключение к менеджеру очередей.");
            throw new RuntimeException(e);
        }

        return this;
    }

    @Override
    public void closeConnection() {
        try {
            mqQueueManager.close();
        } catch (MQException e) {
            failed("Не удалось закрыть соединение с менеджером очередей\n" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public String sendMessage(String message) {
        MQQueue queue;
        String msgID = null;
        try {
            System.out.println(mqConnectionParameters.queue());
            System.out.println(message = new String(message.replaceAll("^\\s+|\\n", "").getBytes(), StandardCharsets.UTF_8));
            queue = mqQueueManager.accessQueue(mqConnectionParameters.queue(), CMQC.MQOO_OUTPUT);
            MQMessage mqMsg = new MQMessage();
            mqMsg.characterSet = CMQC.MQCCSI_Q_MGR;
            mqMsg.writeUTF(message);
            MQPutMessageOptions pmo = new MQPutMessageOptions();
            queue.put(mqMsg, pmo);
            msgID = Hex.encodeHexString(mqMsg.messageId);
            queue.close();
        } catch (MQException | IOException e) {
            failed("Ошибка при отправке сообщения: \n"
                    + e.getMessage());
        }
        return msgID;
    }

    public String getMessageByMessageID(String messageID) {
        MQQueue queue;
        MQMessage msg = null;
        try {
            queue = mqQueueManager.accessQueue(mqConnectionParameters.queue(),
                    CMQC.MQOO_INQUIRE |
                                    CMQC.MQGMO_ALL_MSGS_AVAILABLE |
                                        CMQC.MQOO_BROWSE);

            MQGetMessageOptions gmo = new MQGetMessageOptions();
            gmo.options = CMQC.MQGMO_NO_WAIT | CMQC.MQGMO_BROWSE_NEXT;

            for (int i = 0; i < queue.getCurrentDepth(); i++) {
                msg = new MQMessage();
                queue.get(msg, gmo);
                if (Hex.encodeHexString(msg.messageId).equals(messageID)) {
                    break;
                }
                msg = null;
            }

            queue.close();
        } catch (Exception e) {
            failed("Не удалось прочитать сообщение из очереди " + mqConnectionParameters.queue() +
                                "\n" + e.getMessage());
        }
        String content = "";
        try {
            content = msg.readUTF();
        } catch (NullPointerException | IOException e) {
            failed("Не удалось сохранить контент сообщения с messageID = " + messageID +
                    "\n" + e.getMessage());
        }
        return content;
    }

    public void printAllMessages() {

        List<MQMessage> messages = getAllMessages();

                messages.forEach(msg -> {
                    try {
                        System.out.println("MessageID: " + Hex.encodeHexString(msg.messageId));
                        System.out.println("CorrelationID: " + Hex.encodeHexString(msg.correlationId));
                        System.out.println("UserID: " + msg.userId);
                        System.out.println(msg.readUTF());
                        System.out.println(System.lineSeparator());
                    } catch (IOException e) {
                        failed("Не удалось прочитать сообщение из очереди " + mqConnectionParameters.queue() +
                                "\n" + e.getMessage());
                    }
                });

    }

    public List<MQMessage> getAllMessages() {
        List<MQMessage> messages = new ArrayList<>();
        MQQueue queue;
        MQMessage msg;
        try {
            queue = mqQueueManager.accessQueue(mqConnectionParameters.queue(),
                    CMQC.MQOO_INQUIRE |
                            CMQC.MQGMO_ALL_MSGS_AVAILABLE |
                            CMQC.MQOO_BROWSE);

            MQGetMessageOptions gmo = new MQGetMessageOptions();
            gmo.options = CMQC.MQGMO_NO_WAIT | CMQC.MQGMO_BROWSE_NEXT;

            for (int i = 0; i < queue.getCurrentDepth(); i++) {
                msg = new MQMessage();
                queue.get(msg, gmo);
                messages.add(msg);
            }

            queue.close();
        } catch (Exception e) {
            failed("Не удалось прочитать сообщение из очереди " + mqConnectionParameters.queue() +
                    "\n" + e.getMessage());
        }
        return messages;
    }

    @Override
    public Map<String, String> getMessageByParameters(Map<String, String> parameters) {
        StringBuilder message = new StringBuilder();
        StringBuilder messageID = new StringBuilder();

        if (parameters.containsKey("messageID")) {
            message.append(getMessageByMessageID(parameters.get("messageID")));
            messageID.append(parameters.get("messageID"));
        } else if (parameters.containsKey("text")) {
            getAllMessages().forEach(msg -> {
                try {
                    if (msg.readUTF().contains(parameters.get("text"))) {
                        message.append(msg.readUTF());
                        messageID.append(Hex.encodeHexString(msg.messageId));
                    }
                } catch (IOException e) {
                    failed("Не удалось прочитать сообщение из очереди " + mqConnectionParameters.queue() +
                            "\n" + e.getMessage());
                }
            });
        } else if (parameters.containsKey("json") || parameters.containsKey("xml")) {
            for (MQMessage msg : getAllMessages()) {
                try {
                    String msgID = Hex.encodeHexString(msg.messageId);
                    String msgString = msg.readUTF().replaceAll("^\\s+|\\n", "");

                    DocHelper doc;
                    try {
                        doc = parameters.containsKey("json") ?
                                new DocHelper(JsonManager.parseJSONString(msgString)) :
                                new DocHelper(XMLManager.parseXMLString(msgString));
                    } catch (RuntimeException e) {
                        continue;
                    }
                    String string = parameters.containsKey("json") ?
                            parameters.get("json") :
                            parameters.get("xml");
                    String pathString = string.split("=")[0];
                    String pathValue = string.split("=")[1];
                    if (doc.getFieldValue(pathString).equals(pathValue)){
                        message.append(msgString);
                        messageID.append(msgID);
                        // Находит первое сообщение и останавливает поиск
                        // Чтобы найти все сообщения, убрать break и результат сохранять в коллекции,
                        // а не в стрингбилдер
                        break;
                    }
                } catch (IOException e) {
                    failed("Не удалось прочитать сообщение из очереди " + mqConnectionParameters.queue() +
                            "\n" + e.getMessage());
                }
            }
        }else {
            failed("Ошибка!\n" +
                    "Неправильно переданы параметры поиска сообщения");
        }
        return Map.of(messageID.toString(),message.toString());
    }
}
