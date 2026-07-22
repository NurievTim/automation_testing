package iteration_2.ui;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import models.CreateUserRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import requests.skeleton.Endpoint;
import requests.skeleton.requests.CrudRequester;
import requests.steps.AdminSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.Map;

import static com.codeborne.selenide.Selenide.executeJavaScript;

public class UserChangeNameTest {
    @BeforeAll
    public static void setupSelenoid() {
        Configuration.remote = "http://localhost:4444/wd/hub";
        Configuration.baseUrl = "http://192.168.0.102:4444";
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
        executeJavaScript("LocalStorage.setItem('authToken', arguments[0]);", userAuthHeader);
        Selenide.open("/dashboard");

        // ШАГИ ТЕСТА ЧЕРЕЗ UI

        // ШАГ 3: Юзер находится на странице /dashboard и нажимает на свое имя в правом верхнем углу экрана
        // ШАГ 4: Юзер вводит новое валидное имя в окне "Enter new name"
        // ШАГ 5: Юзер нажимает на кнопку Save Changes
        // ШАГ 6: Проверить, что имя сохранилось
    }

    @Test
    public void userCannotChangeName() {
        // ПРЕДУСЛОВИЯ ЧЕРЕЗ API

        // ШАГ 1: Админ создает юзера
        // ШАГ 2: Юзер логинется

        // ШАГИ ТЕСТА ЧЕРЕЗ UI

        // ШАГ 3: Юзер находится на странице /dashboard и нажимает на свое имя в правом верхнем углу экрана
        // ШАГ 4: Юзер вводит новое невалидное имя в окне "Enter new name"
        // ШАГ 5: Юзер нажимает на кнопку Save Changes
        // ШАГ 6: Проверить, что имя не сохранилось
    }
}
