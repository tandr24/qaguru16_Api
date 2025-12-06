import dto.GetCartAnswerDTO;
import dto.OrderBookAnswerDTO;
import dto.IsbnDTO;
import dto.LoginDTO;

import org.openqa.selenium.Cookie;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static helpers.CustomApiListener.withCustomTemplates;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

public class DemoQaBookShopTests extends TestBase {

    @Test
    public void deleteItemFromCart() {
        LoginDTO login = new LoginDTO();
        login.setUserName("egoand");
        login.setPassword("qweQWE123!@#");

        Response responseLogin = step("Log in with username and password", () ->
                given()
                        .filter(withCustomTemplates())
                        .log().all()

                        .contentType(JSON)
                        .body(login)
                        .when()
                        .post("/Account/v1/Login")
                        .then()
                        .log().all()
                        .statusCode(200))
                .extract().response();


        IsbnDTO isbn = new IsbnDTO();
        isbn.setIsbn("9781449325862");

        OrderBookAnswerDTO newBook = new OrderBookAnswerDTO();
        newBook.setUserId(responseLogin.path("userId"));

        List<IsbnDTO> book = new ArrayList<>();
        book.add(isbn);

        newBook.setCollectionOfIsbns(book);


        step("Add book to cart", () ->
                given()
                        .filter(withCustomTemplates())
                        .log().all()
                        .header("Authorization", "Bearer " + responseLogin.path("token"))
                        .contentType(JSON)
                        .body(newBook)
                        .when()
                        .post("/BookStore/v1/Books")
                        .then()
                        .log().all()
                        .statusCode(201))
                .extract().response();

        System.out.println(responseLogin.path("userId").toString());

        GetCartAnswerDTO responseGetUserByID = step("Get Cart info", () ->
                given()
                        .filter(withCustomTemplates())
                        .log().all()
                        .header("Authorization", "Bearer " + responseLogin.path("token"))
                        .contentType(JSON)
                        .when()
                        .get("/Account/v1/User/" + responseLogin.path("userId"))
                        .then()
                        .log().all()
                        .statusCode(200))
                .extract().as(GetCartAnswerDTO.class);

        Assertions.assertEquals(1, responseGetUserByID.getBooks().size());


        open("/favicon.ico");
        getWebDriver().manage().addCookie(new Cookie("userID", responseLogin.path("userId")));
        getWebDriver().manage().addCookie(new Cookie("userID", responseLogin.path("userId")));
        getWebDriver().manage().addCookie(new Cookie("expires", responseLogin.path("expires")));
        getWebDriver().manage().addCookie(new Cookie("token", responseLogin.path("token")));

        open("/profile");

        $(byText("Delete All Books")).scrollTo().click();
        $("#closeSmallModal-ok").click();
        $("#app").shouldHave(text("No rows found"));



        GetCartAnswerDTO responseGetUserByIDAfterDeletion = step("Get Cart info", () ->
                given()
                        .filter(withCustomTemplates())
                        .log().all()
                        .header("Authorization", "Bearer " + responseLogin.path("token"))
                        .contentType(JSON)
                        .when()
                        .get("/Account/v1/User/" + responseLogin.path("userId"))
                        .then()
                        .log().all()
                        .statusCode(200))
                .extract().as(GetCartAnswerDTO.class);

        Assertions.assertEquals(0, responseGetUserByIDAfterDeletion.getBooks().size());
    }


}



