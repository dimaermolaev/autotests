package ru.digital_spirit.qaaf.utils.otherUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class DtoManager {

    /**
     * Метод для получения объекта из Json
     * @param variable - имя переменной или индекс массива
     * @param object - имя объекта
     * @return - возвращает значение переменной
     */
    public static Object getObjectFromDto(Object object, String variable){
        if(object.getClass().equals(ArrayList.class)){
            List<Object> list = (List<Object>)object;
            return list.get(Integer.parseInt(variable));
        }
        else if(object.getClass().equals(LinkedHashMap.class)){
            LinkedHashMap<String, Object> linkedHashMap = (LinkedHashMap<String, Object>) object;
            return linkedHashMap.get(variable);
        }
        else if(object.getClass().equals(HashMap.class)){
            HashMap<String, Object> hashMap = (HashMap<String, Object>) object;
            return hashMap.get(variable);
        }
        return null;
    }
    /**
     * Метод для установки значения переменной в Json объекте
     * @param variable - имя переменной или индекс массива
     * @param object - имя объекта
     * @param value - устанавливаемое значение переменной
     */
    public static void setValueInDtoObject(Object object, String variable, String value){
        if(object.getClass().equals(ArrayList.class)){
            List<Object> list = (List<Object>)object;
            list.set(Integer.parseInt(variable), value);
        }
        else if(object.getClass().equals(LinkedHashMap.class)){
            LinkedHashMap<String, Object> linkedHashMap = (LinkedHashMap<String, Object>) object;
            linkedHashMap.replace(variable, value);
        }
        else if(object.getClass().equals(HashMap.class)){
            HashMap<String, Object> hashMap = (HashMap<String, Object>) object;
            hashMap.replace(variable, value);
        }
    }
}
