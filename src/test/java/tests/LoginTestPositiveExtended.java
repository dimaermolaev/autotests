package tests;

import model.LoginBodyModel;
import model.LoginResponseModel;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

public class LoginTestPositiveExtended {
    private static final String URL = "https://reqres.in/api/login";
    private static String expectedToken = "QpwL5tke4Pnpja7X4";
    @Test
    public void loginTest(){
        LoginBodyModel loginBody = new LoginBodyModel();
        loginBody.setEmail("eve.holt@reqres.in");
        loginBody.setPassword("cityslicka");

        LoginResponseModel loginResponse = given()
                .log().uri()
                .contentType(JSON)
                .body(loginBody)
                .when()
                .post(URL)
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("token", is(expectedToken))
                .extract().as(LoginResponseModel.class);
        assertThat(loginResponse.getToken()).isEqualTo(expectedToken);
    }
}
