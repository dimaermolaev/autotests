package ru.digital_spirit.qaaf.steps;

import io.cucumber.java.*;
import ru.digital_spirit.qaaf.utils.types.DocType;
import ru.digital_spirit.qaaf.utils.dbconnections.DBConnectionManager;

import java.util.Optional;

public class UtilSteps {
    @ParameterType("документ|XML|JSON|текст")
    public DocType docType(String docType){
        return new DocType(docType);
    }

//    @BeforeAll
//    public static void setParameters() {
//        PropertiesManager.getInstance();
////        PropertiesManager propertiesManager = PropertiesManager.getInstance();
////        Connection techDBConnection = DBConnectionManager.getConnection(new QATechDBConnection());
////        propertiesManager.getPropertiesFromDB(techDBConnection);
//    }
//
//    @Before("@web")
//    public void beforeWeb() {
//
//    }
    @AfterAll
    public static void closing() {
        Optional.ofNullable(
                DBConnectionManager.getConnectionsList()).ifPresent(t -> t
                .forEach(DBConnectionManager::closeConnection));
    }

}
