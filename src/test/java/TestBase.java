import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import io.restassured.RestAssured;

import static com.codeborne.selenide.Selenide.closeWebDriver;

public class TestBase {
    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://demoqa.com";
        Configuration.baseUrl = "https://demoqa.com";
        Configuration.pageLoadStrategy = "eager";
        Configuration.timeout = 5000;
        Configuration.browserSize = System.getProperty("windowSize", "1920x1080");
        Configuration.remote = System.getProperty("remote");
        Configuration.browser = "chrome";
        Configuration.browserVersion =("128.0");
    }

    @AfterEach
    void shutDown() {
        closeWebDriver();
    }
}
