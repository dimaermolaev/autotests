package ru.digital_spirit.qaaf.utils.vars;

import ru.digital_spirit.qaaf.utils.otherUtils.DtoManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Класс-утилита, содержащий методы для работы с локальными переменными проекта
 * setVar - сохранить переменную в памяти
 * varExists - проверить, что переменная существует
 * extractValue - извлечь переменную по имени
 * evalVarString - преобразовать строку, содержащую переменную
 * evalVarCollection - преобразовать элементы коллекции
 */
public class LocalVariablesManager {
    static {
        vars = new HashMap<>();
        objectVars = new HashMap<>();
    }
    private static final Map<String, String> vars;
    private static final Map<String, Object> objectVars;

    private LocalVariablesManager(){}

    /**
     * Метод для сохранения переменной в памяти
     * @param key - название переменной
     * @param value - значение переменной
     */
    public static void setVar(String key, String value) {
        vars.put(key,value);
    }

    /**
     * Метод для сохранения переменной Json в памяти
     * @param key - название переменной
     * @param value - значение переменной
     */
    public static void setObjectVar(String key, Object value) {
        objectVars.put(key,value);
        System.out.println("Создана переменная " + key + " со значением " + value);
    }

    /**
     * Метод для получения переменной Json по имени
     * @param key - имя переменной
     * @return - возвращает значение переменной
     */
    public static Object getObjectValue(String key) {
        return objectVars.get(key);
    }

    /**
     * Метод для получения переменной из Json
     * @param jsonPath - имя Json пути переменной
     * @param object - имя объекта
     * @return - возвращает значение переменной
     */
    public static Object getValueFromObject(Object object, String jsonPath) {
        String[] spitString = jsonPath.split("\\.");
        String lastPath = "";
        String variable = spitString[0];
        for(int i = 1; i < spitString.length; i++){
            lastPath+= spitString[i] + ".";
        }
        if(spitString.length > 1)
            lastPath = lastPath.substring(0,lastPath.length()-1);

        if(variable.contains("[")){
            int index = Integer.parseInt(variable.substring(variable.indexOf("[")+1, variable.indexOf("]")));
            variable = variable.substring(0, variable.indexOf("["));
            List<Object> listObject = (List<Object>) DtoManager.getObjectFromDto(object, variable);
            Object object1 = DtoManager.getObjectFromDto(listObject, String.valueOf(index));
            return getValueFromObject(object1, lastPath);
        }
        else if(variable.length() > 1){
            return getValueFromObject(DtoManager.getObjectFromDto(object, variable), lastPath);
        }
        else return object;
    }

    /**
     * Метод для установки значения переменной в Json
     * @param jsonPath - имя Json переменной
     * @param object - имя объекта
     * @return - возвращает значение переменной
     */
    public static Object setValueInObject(Object object, String jsonPath, String value) {
        String[] splitString = jsonPath.split("\\.");
        String lastPath = "";
        String variable = splitString[0];
        for(int i = 1; i < splitString.length; i++){
            lastPath+= splitString[i] + ".";
        }
        if(splitString.length > 1)
            lastPath = lastPath.substring(0,lastPath.length()-1);

        if(variable.contains("[")){
            int index = Integer.parseInt(variable.substring(variable.indexOf("[")+1, variable.indexOf("]")));
            variable = variable.substring(0, variable.indexOf("["));
            List<Object> listObject = (List<Object>) DtoManager.getObjectFromDto(object, variable);
            Object object1 = DtoManager.getObjectFromDto(listObject, String.valueOf(index));
            if(lastPath.length() < 1){
                if(!object1.getClass().equals(String.class))
                    DtoManager.setValueInDtoObject(object1, variable, value);
                else DtoManager.setValueInDtoObject(listObject, String.valueOf(index), value);
            }
            else return setValueInObject(object1, lastPath, value);
        }
        else if(variable.length() > 1){
            if(lastPath.length() < 1){
                DtoManager.setValueInDtoObject(object, variable, value);
            }
            else return setValueInObject(DtoManager.getObjectFromDto(object, variable), lastPath, value);
        }
        return null;
    }

    /**
     * Метод для проверки, что переменная с именем существует
     * @param varName - название переменной
     * @return - возвращает true/false
     */
    public static boolean varExists(String varName) {
        return vars.containsKey(varName);
    }

    /**
     * Метод для получения переменной по имени
     * @param key - имя переменной
     * @return - возвращает значение переменной
     */
    public static String extractValue(String key) {
        return Optional.ofNullable(vars.get(evalVarString(key)))
                .orElseGet(()->"null");
    }

    /**
     * Метод для преобразования строки содержащей переменные.
     * Находит в строке все переменные заключенные в конструкцию #{  }, и подставляет вместо них значения:
     * Напрмер:
     * Переменная VAR1 имеет значение Var1Value, а VAR2 значение ValueVar2.
     * В таком случае строка: "---------#{VAR1}---------#{VAR2}---------"
     * Будет преобразована в строку: "---------Var1Value---------ValueVar2---------"
     * Используется для работы с переменными в .feature-файлах
     * @param varString - строка, содержащая переменные в формате #{  }
     * @return - преобразованная строка, где переменные заменены значениями
     */
    public static String evalVarString(String varString) {
        if (varString == null) {
            throw new NullPointerException
                    ("Имя переменной имеет значение null. " +
                            "Нельзя получить значение.");
        }
        Pattern regexp = Pattern.compile("#\\{(.+?)\\}");
        Matcher matcher = regexp.matcher(varString);
        while (matcher.find()) {
            varString = varString.replace(matcher.group(), extractValue(matcher.group(1)));
        }
        return varString;
    }

    /**
     * Метод для преобразования переменных внутри коллекции Map
     * Преобразовывает все переменные, содержащиеся как в ключах, так и в значениях.
     * Например, переменная varKey1 = key, а переменная varValue1 = value
     * Тогда пара ключ=значение #{varKey1}=#{varValue1} будет преобразована в пару key=value
     * @param varMap - принимает коллекцию типа Map<String, String>
     * @return - возвращает преобразованную коллекцию типа Map<String, String>
     */
    public static Map<String, String> evalVarCollection(Map<String, String> varMap) {
        return varMap.entrySet().stream()
                .collect(Collectors.toMap(
                        k -> evalVarString(k.getKey()),
                        v -> evalVarString(v.getValue())));
    }

    /**
     * Метод для преобразования переменных внутри коллекции List
     * Преобразовывает все переменные в строках коллекции.
     * Возвращает преобразованную коллекцию.
     * @param varList - принимает коллекцию типа List<String>
     * @return - возвращает преобразованную коллекцию типа List<String>
     */
    public static List<String> evalVarCollection(List<String> varList) {
        return varList.stream()
                .map(LocalVariablesManager::evalVarString)
                .toList();
    }
}
