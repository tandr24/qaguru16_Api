package tests;

import api.BookApi;
import helpers.extensions.WithLogin;
import org.junit.jupiter.api.Test;
import pages.DemoQAPage;

import static api.LoginApi.responseLoginApi;

public class BookShopTest extends TestBase {
    @Test
    @WithLogin
    public void deleteItemsFromCart() {
        BookApi bookApi = new BookApi();
        DemoQAPage demoQAPage = new DemoQAPage();

        bookApi.deleteBooksInCart(responseLoginApi());
        bookApi.addBookToCart(responseLoginApi());
        bookApi.verifyCartContainsItem(responseLoginApi());
        demoQAPage.openCart();
        demoQAPage.deleteItemsFromCart();
        demoQAPage.verifyCart();
        bookApi.verifyCartIsEmpty(responseLoginApi());
    }
}
