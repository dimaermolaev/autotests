package ru.digital_spirit.qaaf.steps;

import io.cucumber.docstring.DocString;
import io.cucumber.java.ru.Дано;
import ru.digital_spirit.qaaf.utils.mq.MQHelper;
import ru.digital_spirit.qaaf.utils.mq.MQManager;

import java.util.Map;

import static ru.digital_spirit.qaaf.utils.LogHelper.*;
import static ru.digital_spirit.qaaf.utils.vars.LocalVariablesManager.*;

public class MQSteps {
    @Дано("Отправить сообщение в очередь {string} и сохранить messageID в переменной {string}:")
    public void sendMessageToMQ(String mqName, String varName, DocString message) {
        String msg = evalVarString(message.getContent().replaceAll("^\\s+|\\n", ""));
        MQManager ibm = new MQHelper().getMQManager(evalVarString(mqName));

        ibm.getMQManager();
        String messageID = ibm.sendMessage(msg);
        setVar(varName, messageID);
        info("Отправлено сообщение в очередь " + mqName);
        addAttachmentJson("messageID", messageID);
        addAttachmentTxt("messageContent", msg);

    }

    @Дано("Сохранить сообщение из очереди {string} в переменной {string}:")
    public void saveMessageFromMQToVar(String mqName, String varName, Map<String, String> table) {
        MQManager ibm = new MQHelper().getMQManager(evalVarString(mqName));

        ibm.getMQManager();
        Map<String, String> mqMessage = ibm.getMessageByParameters(evalVarCollection(table));
        setVar(varName, mqMessage.values().stream().findFirst().orElseThrow());
        info("В переменной " + varName + " сохранено сообщение c messageID:\n" + mqMessage.keySet().stream().findFirst().orElseThrow());

        addAttachmentJson("messageID", mqMessage.keySet().stream().findFirst().orElseThrow());
        addAttachmentTxt("message", mqMessage.values().stream().findFirst().orElseThrow());
    }

    @Дано("Вывести на экран все сообщения из очереди {string}")
    public void printAllMessagesFromMQ(String mqName) {
        MQManager ibm = new MQHelper().getMQManager(evalVarString(mqName));

        ibm.getMQManager();
        info("Все сообщения в очереди " + mqName);
        ibm.printAllMessages();
    }
}
