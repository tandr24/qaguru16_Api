package pages;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static io.qameta.allure.Allure.step;

public class DemoQAPage {
    public void openIconPage() {
        step("Open icon on site", () ->
                open("/favicon.ico"));
    }

    public void openCart() {
        step("Open Cart", () ->
                open("/profile"));
    }

    public void deleteItemsFromCart() {
        step("Delete all items from cart", () ->
        {
            $(byText("Delete All Books")).scrollTo().click();
            $("#closeSmallModal-ok").click();
        });
    }

    public void verifyCart() {
        step("Verify that no items in cart via UI", () ->
                $("#app").shouldHave(text("No rows found")));
    }
}
