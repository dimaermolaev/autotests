package tests;

import org.junit.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.is;

public class LoginTestNegative {
    private static final String URL = "https://reqres.in/api/login";
    private static String invalidEmail = "invalid@example.com";
    private static String invalidPassword = "invalidPassword";
    private static String expectedErrorMessage = "user not found";

    @Test
    public void loginNegativeTest() {

        String data = "{ \"email\": \"" + invalidEmail + "\", \"password\": \"" + invalidPassword + "\" }";
        given()
                .log().uri()
                .contentType(JSON)
                .body(data)
                .when()
                .post(URL)
                .then()
                .log().status()
                .log().body()
                .statusCode(400)
                .body("error", is(expectedErrorMessage));
    }
}
