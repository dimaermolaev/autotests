package ru.digital_spirit.qaaf.utils.mqconnections;

import org.aeonbits.owner.ConfigFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс-утилита для создания подключения к системе очередей
 * На вход принимает название очереди (префикс параметров из .config-файла)
 */
public class MQConnectionManager {
    private static Map<String, MQConnectionParameters> mqConnectionParametersCollection;

    public static MQConnectionParameters getConnectionParameters(String mqName) {
        if (mqConnectionParametersCollection == null) {
            mqConnectionParametersCollection = new HashMap<>();
        }
        if (!mqConnectionParametersCollection.containsKey(mqName)) {
            MQConnectionParameters mqConnectionParameters = ConfigFactory.create(
                    MQConnectionParameters.class,
                    Map.of("mq", mqName));
            mqConnectionParametersCollection.put(mqName, mqConnectionParameters);
        }
        return mqConnectionParametersCollection.get(mqName);
    }

}
