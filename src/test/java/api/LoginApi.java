package api;

import dto.LoginDTO;
import io.restassured.response.Response;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static specs.DemoQASpecs.loginRequestSpec;
import static specs.DemoQASpecs.responseSpec;

public class LoginApi {

    public static Response responseLoginApi() {
        LoginDTO login = new LoginDTO();
        login.setUserName("egoand");
        login.setPassword("qweQWE123!@#");

        return step("Log in to the site with credentials", () ->
                given()
                        .spec(loginRequestSpec)
                        .body(login)
                        .when()
                        .post("/Account/v1/Login")
                        .then()
                        .spec(responseSpec(200))
                        .extract()
                        .response());
    }
}