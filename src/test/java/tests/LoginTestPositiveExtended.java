package tests;

import model.LoginModel;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.is;

public class LoginTestPositiveExtended {
    private static final String URL = "https://reqres.in/api/login";
    private static String expectedToken = "QpwL5tke4Pnpja7X4";
    @Test
    public void loginTest(){
        LoginModel loginModel = new LoginModel();
        loginModel.setEmail("eve.holt@reqres.in");
        loginModel.setPassword("cityslicka");
        given()
                .log().uri()
                .contentType(JSON)
                .body(loginModel)
                .when()
                .post(URL)
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("token", is(expectedToken));
    }
}
