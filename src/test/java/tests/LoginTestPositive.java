package tests;

import org.junit.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.is;

public class LoginTestPositive {
    private static final String URL = "https://reqres.in/api/login";
    private static String email = "eve.holt@reqres.in";
    private static String password = "cityslicka";
    private static String expectedToken = "QpwL5tke4Pnpja7X4";


    @Test
    public void loginTest() {

        String data = "{ \"email\": \"" + email + "\", \"password\": \"" + password + "\" }";
        given()
                .log().uri()
                .contentType(JSON)
                .body(data)
                .when()
                .post(URL)
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("token", is(expectedToken));
    }
}
