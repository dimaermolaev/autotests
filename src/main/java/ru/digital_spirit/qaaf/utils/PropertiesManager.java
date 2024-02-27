package ru.digital_spirit.qaaf.utils;

import ru.digital_spirit.qaaf.utils.dbconnections.DBConnectionManager;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Класс для управления переменными окружения.
 * При создании инстанса считывает все переменные из файла local.properties в корневом каталоге проекта и сохраняет все переменные в объекте класса Properties
 * метод getPropertiesFromDB(Connection dbConnection) сохраняет в памяти все переменные или имеющие конкретный признак в поле scope
 */
public class PropertiesManager {

    private final Properties properties;
    private final SQLRequestManager sqlrm;

    public PropertiesManager() {
        sqlrm = SQLRequestManager.getInstance(
                DBConnectionManager.createConnection("qa"));
        properties = new Properties();
    }

    public void readPropertiesFromDB() {
        List<Map<String, String>> props = sqlrm.executeQuery(
                "select var_name, var_value from properties where scope = 'qa_props'"
        );
        props.forEach(entry -> properties.setProperty(entry.get("VAR_NAME"), entry.get("VAR_VALUE")));
    }

    public void readPropertiesFromDB(String scope) {
        List<Map<String, String>> props = sqlrm.executeQuery(
                "select * from properties where scope = '" + scope + "';"
        );
        props.forEach(entry -> properties.setProperty(entry.get("VAR_NAME"), entry.get("VAR_VALUE")));
    }

    public void setPropertyInDB (String var, String value) {
        int el = sqlrm.updateExecute("update properties " +
                "set var_value = '" + value + "' " +
                "where var_name = '" + var + "';");
        if (el == 0) System.out.println("В таблице нет переменной " + var);
        else {
            properties.setProperty(var, value);
            System.out.println("Изменена переменная " + var + " на значение " + value);
        }
    }

    public String getPropertyFromDB (String var) {
        List<Map<String, String>> rs = sqlrm.executeQuery(
                "select var_value " +
                        "from properties " +
                        "where var_name = '" + var + "';");
        if (rs == null || rs.isEmpty()) {
            System.out.println("В таблице отсутствует переменная " + var);
            return null;
        }
        return rs.get(0).get("var_value");
    }

    public void addPropertyToDB(String var, String value, String scope, String env) {
        sqlrm.updateExecute("insert into properties (var_name, var_value, scope, environment) " +
                "values ('" + var + "', '" + value + "', '" + scope + "', '" + env +"');");
        properties.setProperty(var, value);
        System.out.println("Добавлена переменная " + var + " со значением " + value + ", набор: " + scope + ", окружение: " + env);
    }

    public Properties getProperties() {
        return properties;
    }

}