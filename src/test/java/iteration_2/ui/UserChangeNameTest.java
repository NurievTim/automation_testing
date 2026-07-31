package iteration_2.ui;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverConditions;
import generators.RandomData;
import models.CreateUserRequest;
import models.CustomerResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import requests.skeleton.Endpoint;
import requests.skeleton.requests.CrudRequester;
import requests.skeleton.requests.ValidatedCrudRequester;
import requests.steps.AdminSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.Map;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class UserChangeNameTest {
    @BeforeAll
    public static void setupSelenoid() {
        Configuration.remote = "http://localhost:4444/wd/hub";
        Configuration.baseUrl = "http://host.docker.internal:3000";
        Configuration.browserSize = "1920x1080";
        Configuration.browser = "chrome";
        Configuration.browserCapabilities.setCapability(
                "selenoid:options",
                Map.of("enableVNC", true, "enableLog", true)
        );
    }
    @Test
    public void userCanChangeName() {
        // ПРЕДУСЛОВИЯ ЧЕРЕЗ API

        // ШАГ 1: Админ создает юзера
        CreateUserRequest userRequest = AdminSteps.createUser();
        // ШАГ 2: Юзер логинется
        String userAuthHeader = new CrudRequester(
                RequestSpecs.unauthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOK())
                .post(userRequest)
                .extract()
                .header("Authorization");

        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);
        Selenide.open("/dashboard");

        // ШАГИ ТЕСТА ЧЕРЕЗ UI

        // ШАГ 3: Юзер находится на странице /dashboard и нажимает на свое имя в правом верхнем углу экрана
        $(Selectors.byClassName("user-info")).click();
        webdriver().shouldHave(WebDriverConditions.urlContaining("/edit-profile"));

        // ШАГ 4: Юзер вводит новое валидное имя в окне "Enter new name"
        String newName = RandomData.getValidName();
        $(Selectors.byAttribute("placeholder", "Enter new name")).sendKeys(newName);

        // ШАГ 5: Юзер нажимает на кнопку Save Changes
        $(Selectors.byText("\uD83D\uDCBE Save Changes")).click();
        Alert alert = switchTo().alert();
        assertEquals( "✅ Name updated successfully!", alert.getText());
        alert.accept();

        // ШАГ 6: Проверить, что имя сохранилось
        CustomerResponse customerResponse = new ValidatedCrudRequester<CustomerResponse>(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.GET_PROFILE,
                ResponseSpecs.requestReturnsOK())
                .get();

        assertEquals(newName, customerResponse.getName());
    }

    @Test
    public void userCannotChangeName() {
        // ПРЕДУСЛОВИЯ ЧЕРЕЗ API

        // ШАГ 1: Админ создает юзера
        CreateUserRequest userRequest = AdminSteps.createUser();

        // ШАГ 2: Юзер логинется
        String userAuthHeader = new CrudRequester(
                RequestSpecs.unauthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOK())
                .post(userRequest)
                .extract()
                .header("Authorization");

        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);
        Selenide.open("/dashboard");

        // ШАГИ ТЕСТА ЧЕРЕЗ UI

        // ШАГ 3: Юзер находится на странице /dashboard и нажимает на свое имя в правом верхнем углу экрана
        $(Selectors.byClassName("user-info")).click();
        webdriver().shouldHave(WebDriverConditions.urlContaining("/edit-profile"));

        // ШАГ 4: Юзер вводит новое невалидное имя в окне "Enter new name"
        String newName = RandomData.generateNameWithoutSpace();
        $(Selectors.byAttribute("placeholder", "Enter new name")).sendKeys(newName);

        // ШАГ 5: Юзер нажимает на кнопку Save Changes
        $(Selectors.byText("\uD83D\uDCBE Save Changes")).click();
        Alert alert = switchTo().alert();
        assertEquals( "❌ Please enter a valid name.", alert.getText());
        alert.accept();

        // ШАГ 6: Проверить, что имя не сохранилось
        CustomerResponse customerResponse = new ValidatedCrudRequester<CustomerResponse>(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.GET_PROFILE,
                ResponseSpecs.requestReturnsOK())
                .get();

        assertNotEquals(newName, customerResponse.getName());
    }
}
