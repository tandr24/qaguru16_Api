package api;

import dto.GetCartAnswerDTO;
import dto.IsbnDTO;
import dto.OrderBookAnswerDTO;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static specs.DemoQASpecs.loginRequestSpec;
import static specs.DemoQASpecs.responseSpec;

public class BookApi {

    public void deleteBooksInCart(Response responseLogin) {
        step("Delete all books in cart via API", () ->
                given()
                        .spec(loginRequestSpec)
                        .header("Authorization", "Bearer " + responseLogin.path("token"))
                        .when()
                        .delete("/BookStore/v1/Books/?UserId=" + responseLogin.path("userId"))
                        .then()
                        .spec(responseSpec(204)));
    }

    public void addBookToCart(Response responseLogin) {
        IsbnDTO isbn = new IsbnDTO();
        isbn.setIsbn("9781449325862");

        List<IsbnDTO> book = new ArrayList<>();
        book.add(isbn);

        OrderBookAnswerDTO newBook = new OrderBookAnswerDTO();
        newBook.setUserId(responseLogin.path("userId"));
        newBook.setCollectionOfIsbns(book);

        step("Add book to cart", () ->
                given()
                        .spec(loginRequestSpec)
                        .header("Authorization", "Bearer " + responseLogin.path("token"))
                        .body(newBook)
                        .when()
                        .post("/BookStore/v1/Books")
                        .then()
                        .spec(responseSpec(201)));
    }

    public void verifyCartContainsItem(Response responseLogin) {
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

    }

    public void verifyCartIsEmpty(Response responseLogin) {
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

