package helpers.extensions;

import io.restassured.response.Response;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.Cookie;
import pages.DemoQAPage;

import static api.LoginApi.responseLoginApi;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class LoginExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {

        Response authorizationResponse = responseLoginApi();
        DemoQAPage page = new DemoQAPage();
        page.openIconPage();

        getWebDriver().manage().addCookie(new Cookie("token", authorizationResponse.path("token")));
        getWebDriver().manage().addCookie(new Cookie("expires", authorizationResponse.path("expires")));
        getWebDriver().manage().addCookie(new Cookie("userID", authorizationResponse.path("userId")));
    }

}