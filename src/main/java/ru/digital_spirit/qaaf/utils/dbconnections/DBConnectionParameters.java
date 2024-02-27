package ru.digital_spirit.qaaf.utils.dbconnections;

import org.aeonbits.owner.Config;

/**
 * Интерфейс для создания объктов с параметрами подключения к базам данных
 * url - строка подключения к БД через JDBC
 * lоgin - имя ползователя от учетки в БД
 * password - пароль от учетки в БД
 */
@Config.Sources({
        "file:config/db/db.properties"
})
public interface DBConnectionParameters extends Config {

    @Key("${db}.url")
    String url();
    @Key("${db}.login")
    String login();
    @Key("${db}.password")
    String password();

}
