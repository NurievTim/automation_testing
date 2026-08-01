package iteration_2.ui;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverConditions;
import api.generators.RandomData;
import api.models.CreateAccountResponse;
import api.models.CreateUserRequest;
import api.models.CustomerResponse;
import api.models.DepositRequest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requests.CrudRequester;
import api.requests.skeleton.requests.ValidatedCrudRequester;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;


import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTransferTest {

    @Test
    public void userCanMakeTransfer() {
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

        // ШАГ 3: Юзер создает 2 аккаунта
        CreateAccountResponse firstAccountResponse = new ValidatedCrudRequester<CreateAccountResponse>(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated()
        ).post(null);
        CreateAccountResponse secondAccountResponse = new ValidatedCrudRequester<CreateAccountResponse>(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated()
        ).post(null);

        int transferAmount = RandomData.generateDepositAmount();
        new CrudRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsOK()
        ).post(DepositRequest.builder()
                .id((int) firstAccountResponse.getId())
                .balance(transferAmount)
                .build());

        // ШАГИ ТЕСТА ЧЕРЕЗ UI

        // ШАГ 4: Юзер находится на странице /dashboard и нажимает на кнопку "Make a transfer"
        $(Selectors.byTagAndText("button","\uD83D\uDD04 Make a Transfer")).click();
        webdriver().shouldHave(WebDriverConditions.urlContaining("/transfer"));

        // ШАГ 5: Юзер нажимает на окно выбора аккаунта "Select your account" и выбирает в дроп меню аккаунт
        $(Selectors.byClassName("account-selector")).selectOptionByValue(String.valueOf(firstAccountResponse.getId()));

        // ШАГ 6: Юзер вводит аккаут получателя "Recipient Account Number"
        $(Selectors.byPlaceholder("Enter recipient account number")).sendKeys(secondAccountResponse.getAccountNumber());

        // ШАГ 7: Юзер вводит валидную сумму перевода
        $(Selectors.byPlaceholder("Enter amount")).sendKeys(String.valueOf(transferAmount));

        // ШАГ 8: Юзер нажимает чек бокс Confirm details are correct
        $(Selectors.byAttribute("id", "confirmCheck")).click();

        // ШАГ 9: Юзер нажимает кнопку Send Transfer
        $(Selectors.byTagAndText("button", "\uD83D\uDE80 Send Transfer")).click();
        Alert alert = switchTo().alert();
        assertEquals( "✅ Successfully transferred $" + transferAmount + " to account " + secondAccountResponse.getAccountNumber() + "!", alert.getText());
        alert.accept();

        // ШАГ 10: Проверить, что перевод произошел
        CustomerResponse customerResponse = new ValidatedCrudRequester<CustomerResponse>(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.GET_PROFILE,
                ResponseSpecs.requestReturnsOK()
        ).get();

        double secondAccountBalance = customerResponse.getAccounts().stream()
                .filter(acc -> acc.getId() == secondAccountResponse.getId())
                .findFirst()
                .orElseThrow()
                .getBalance();
        assertEquals(transferAmount, secondAccountBalance);
    }

    @Test
    public void userCannotMakeTransferWithInvalidAmount() {
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

        // ШАГ 3: Юзер создает 2 аккаунта
        CreateAccountResponse firstAccountResponse = new ValidatedCrudRequester<CreateAccountResponse>(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated()
        ).post(null);
        CreateAccountResponse secondAccountResponse = new ValidatedCrudRequester<CreateAccountResponse>(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated()
        ).post(null);

        // ШАГИ ТЕСТА ЧЕРЕЗ UI

        // ШАГ 4: Юзер находится на странице /dashboard и нажимает на кнопку "Make a transfer"
        $(Selectors.byTagAndText("button","\uD83D\uDD04 Make a Transfer")).click();
        webdriver().shouldHave(WebDriverConditions.urlContaining("/transfer"));

        // ШАГ 5: Юзер нажимает на окно выбора аккаунта "Select your account" и выбирает в дроп меню аккаунт
        $(Selectors.byClassName("account-selector")).selectOptionByValue(String.valueOf(firstAccountResponse.getId()));

        // ШАГ 6: Юзер вводит аккаут получателя "Recipient Account Number"
        $(Selectors.byPlaceholder("Enter recipient account number")).sendKeys(secondAccountResponse.getAccountNumber());

        // ШАГ 7: Юзер вводит невалидную сумму перевода
        $(Selectors.byPlaceholder("Enter amount")).sendKeys(String.valueOf(10001));

        // ШАГ 8: Юзер нажимает чек бокс Confirm details are correct
        $(Selectors.byAttribute("id", "confirmCheck")).click();

        // ШАГ 9: Юзер нажимает кнопку Send Transfer
        $(Selectors.byTagAndText("button", "\uD83D\uDE80 Send Transfer")).click();
        Alert alert = switchTo().alert();
        assertEquals( "❌ Error: Transfer amount cannot exceed 10000", alert.getText());
        alert.accept();

        // ШАГ 10: Проверить, что перевод не прошел
        CustomerResponse customerResponse = new ValidatedCrudRequester<CustomerResponse>(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.GET_PROFILE,
                ResponseSpecs.requestReturnsOK()
        ).get();

        double secondAccountBalance = customerResponse.getAccounts().stream()
                .filter(acc -> acc.getId() == secondAccountResponse.getId())
                .findFirst()
                .orElseThrow()
                .getBalance();
        assertEquals(0, secondAccountBalance);
    }

    @Test
    public void userCannotMakeTransferNotConfirmed() {
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

        // ШАГ 3: Юзер создает 2 аккаунта
        CreateAccountResponse firstAccountResponse = new ValidatedCrudRequester<CreateAccountResponse>(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated()
        ).post(null);
        CreateAccountResponse secondAccountResponse = new ValidatedCrudRequester<CreateAccountResponse>(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated()
        ).post(null);

        int transferAmount = RandomData.generateDepositAmount();
        new CrudRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsOK()
        ).post(DepositRequest.builder()
                .id((int) firstAccountResponse.getId())
                .balance(transferAmount)
                .build());

        // ШАГИ ТЕСТА ЧЕРЕЗ UI

        // ШАГ 4: Юзер находится на странице /dashboard и нажимает на кнопку "Make a transfer"
        $(Selectors.byTagAndText("button","\uD83D\uDD04 Make a Transfer")).click();
        webdriver().shouldHave(WebDriverConditions.urlContaining("/transfer"));

        // ШАГ 5: Юзер нажимает на окно выбора аккаунта "Select your account" и выбирает в дроп меню аккаунт
        $(Selectors.byClassName("account-selector")).selectOptionByValue(String.valueOf(firstAccountResponse.getId()));

        // ШАГ 6: Юзер вводит аккаут получателя "Recipient Account Number"
        $(Selectors.byPlaceholder("Enter recipient account number")).sendKeys(secondAccountResponse.getAccountNumber());

        // ШАГ 7: Юзер вводит валидную сумму перевода
        $(Selectors.byPlaceholder("Enter amount")).sendKeys(String.valueOf(transferAmount));

        // ШАГ 9: Юзер нажимает кнопку Send Transfer
        $(Selectors.byTagAndText("button", "\uD83D\uDE80 Send Transfer")).click();
        Alert alert = switchTo().alert();
        assertEquals( "❌ Please fill all fields and confirm.", alert.getText());
        alert.accept();

        // ШАГ 10: Проверить, что перевод не прошел
        CustomerResponse customerResponse = new ValidatedCrudRequester<CustomerResponse>(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.GET_PROFILE,
                ResponseSpecs.requestReturnsOK()
        ).get();

        double secondAccountBalance = customerResponse.getAccounts().stream()
                .filter(acc -> acc.getId() == secondAccountResponse.getId())
                .findFirst()
                .orElseThrow()
                .getBalance();
        assertEquals(0, secondAccountBalance);
    }
}
