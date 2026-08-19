package iteration_2.ui;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverConditions;
import generators.RandomData;
import models.CreateAccountResponse;
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

public class UserDepositTest {
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
    public void userCanMakeDeposit() {
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

        // ШАГ 3: Юзер создает аккаунт
        CreateAccountResponse accountResponse = new ValidatedCrudRequester<CreateAccountResponse>(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated()
        ).post(null);

        // ШАГИ ТЕСТА ЧЕРЕЗ UI

        // ШАГ 4: Юзер находится на странице /dashboard и нажимает на кнопку "Deposit Money"
        $(Selectors.byTagAndText("button","\uD83D\uDCB0 Deposit Money")).click();
        webdriver().shouldHave(WebDriverConditions.urlContaining("/deposit"));

        // ШАГ 5: Юзер нажимает на окно выбора аккаунта "Choose an account" и выбирает в дроп меню аккаунт
        $(Selectors.byClassName("account-selector")).selectOptionByValue(String.valueOf(accountResponse.getId()));

        // ШАГ 6: Юзер нажимает на окно ввода суммы депозита и вводит валидную сумму
        int depositAmount = RandomData.generateDepositAmount();
        $(Selectors.byPlaceholder("Enter amount")).sendKeys(String.valueOf(depositAmount));

        // ШАГ 7: Юзер нажимает на кнопку "Deposit"
        $(Selectors.byTagAndText("button", "\uD83D\uDCB5 Deposit")).click();
        Alert alert = switchTo().alert();
        assertEquals( "✅ Successfully deposited $" + depositAmount + " to account " + accountResponse.getAccountNumber() + "!", alert.getText());
        alert.accept();

        // ШАГ 8: Проверить, что депозит пополнен
        CustomerResponse customerResponse = new ValidatedCrudRequester<CustomerResponse>(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.GET_PROFILE,
                ResponseSpecs.requestReturnsOK()
        ).get();

        assertEquals(depositAmount, customerResponse.getAccounts().getFirst().getBalance());
    }

    @Test
    public void userCannotMakeDeposit() {
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

        // ШАГ 3: Юзер создает аккаунт
        CreateAccountResponse accountResponse = new ValidatedCrudRequester<CreateAccountResponse>(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated()
        ).post(null);

        // ШАГИ ТЕСТА ЧЕРЕЗ UI

        // ШАГ 4: Юзер находится на странице /dashboard и нажимает на кнопку "Deposit Money"
        $(Selectors.byTagAndText("button","\uD83D\uDCB0 Deposit Money")).click();
        webdriver().shouldHave(WebDriverConditions.urlContaining("/deposit"));

        // ШАГ 5: Юзер нажимает на окно выбора аккаунта "Choose an account" и выбирает в дроп меню аккаунт
        $(Selectors.byClassName("account-selector")).selectOptionByValue(String.valueOf(accountResponse.getId()));

        // ШАГ 6: Юзер нажимает на окно ввода суммы депозита и вводит невалидную сумму
        $(Selectors.byPlaceholder("Enter amount")).sendKeys(String.valueOf(5001));

        // ШАГ 7: Юзер нажимает на кнопку "Deposit"
        $(Selectors.byTagAndText("button", "\uD83D\uDCB5 Deposit")).click();
        Alert alert = switchTo().alert();
        assertEquals( "❌ Please deposit less or equal to 5000$.", alert.getText());
        alert.accept();

        // ШАГ 8: Проверить, что транзакции не было
        CustomerResponse customerResponse = new ValidatedCrudRequester<CustomerResponse>(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.GET_PROFILE,
                ResponseSpecs.requestReturnsOK()
        ).get();

        assertEquals(0, customerResponse.getAccounts().getFirst().getBalance());
    }
}
