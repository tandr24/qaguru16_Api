import dto.GetCartAnswerDTO;
import dto.OrderBookAnswerDTO;
import dto.IsbnDTO;
import dto.LoginDTO;

import org.junit.jupiter.api.DisplayName;
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
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static specs.DemoQASpecs.*;

public class DemoQaBookShopTests extends TestBase {

    @Test
    @DisplayName("Verify that button \"Delete all books\" deletes all books from cart")
    public void deleteItemFromCart() {
        LoginDTO login = new LoginDTO();
        login.setUserName("egoand");
        login.setPassword("qweQWE123!@#");

        Response responseLogin = step("Log in with username and password", () ->
                given()
                        .spec(loginRequestSpec)
                        .body(login)
                        .when()
                        .post("/Account/v1/Login")
                        .then()
                        .spec(responseSpec(200))
                        .extract()
                        .response());

        IsbnDTO isbn = new IsbnDTO();
        isbn.setIsbn("9781449325862");

        List<IsbnDTO> book = new ArrayList<>();
        book.add(isbn);

        OrderBookAnswerDTO newBook = new OrderBookAnswerDTO();
        newBook.setUserId(responseLogin.path("userId"));
        newBook.setCollectionOfIsbns(book);

        step("Delete all books in cart via API", () ->
                given()
                        .spec(loginRequestSpec)
                        .header("Authorization", "Bearer " + responseLogin.path("token"))
                        .when()
                        .delete("/BookStore/v1/Books/?UserId=" +responseLogin.path("userId"))
                        .then()
                        .spec(responseSpec(204)));

        step("Add book to cart", () ->
                given()
                        .spec(loginRequestSpec)
                        .header("Authorization", "Bearer " + responseLogin.path("token"))
                        .body(newBook)
                        .when()
                        .post("/BookStore/v1/Books")
                        .then()
                        .spec(responseSpec(201)));

        GetCartAnswerDTO responseGetUserByID = step("Get Cart info", () ->
                given()
                        .spec(loginRequestSpec)
                        .header("Authorization", "Bearer " + responseLogin.path("token"))
                        .when()
                        .get("/Account/v1/User/" + responseLogin.path("userId"))
                        .then()
                        .spec(responseSpec(200))
                        .extract()
                        .as(GetCartAnswerDTO.class));


        step("Verify that cart has items to delete", () ->
                Assertions.assertEquals(1, responseGetUserByID.getBooks().size()));

        step("Open icon on site", () ->
                open("/favicon.ico"));

        step("Add Coockies", () -> {
            getWebDriver().manage().addCookie(new Cookie("userID", responseLogin.path("userId")));
            getWebDriver().manage().addCookie(new Cookie("expires", responseLogin.path("expires")));
            getWebDriver().manage().addCookie(new Cookie("token", responseLogin.path("token")));
        });

        step("Open Cart", () ->

                open("/profile"));
        step("Delete all items from cart", () ->
        {
            $(byText("Delete All Books")).scrollTo().click();
            $("#closeSmallModal-ok").click();

        });
        step("Verify that no items in cart via UI", () ->
                $("#app").shouldHave(text("No rows found")));

        GetCartAnswerDTO responseGetUserByIDAfterDeletion = step("Get cart info via API", () ->
                given()
                        .spec(loginRequestSpec)
                        .header("Authorization", "Bearer " + responseLogin.path("token"))
                        .when()
                        .get("/Account/v1/User/" + responseLogin.path("userId"))
                        .then()
                        .spec(responseSpec(200))
                        .extract().as(GetCartAnswerDTO.class));

        step("Verify that no items in cart via API", () ->
                Assertions.assertEquals(0, responseGetUserByIDAfterDeletion.getBooks().size()));
    }
}