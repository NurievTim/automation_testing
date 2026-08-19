package ui.pages;

import api.models.CreateUserRequest;
import api.specs.RequestSpecs;
import com.codeborne.selenide.Selenide;
import org.openqa.selenium.Alert;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static com.codeborne.selenide.Selenide.executeJavaScript;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class BasePage<T extends BasePage> {
    public abstract String url();

    public T open() {
        return Selenide.open(url(), (Class<T>) this.getClass());
    }

    public <T extends BasePage> T getPage(Class<T> pageClass) {
        return Selenide.page(pageClass);
    }

    public T checkAlertMessageAndAccept(String alertMessage) {
        assertThat(acceptAlertAndGetText()).isEqualTo(alertMessage);
        return (T) this;
    }

    public T checkAlertMessageAndAccept(Alerts expectedAlert, Object... args) {
        assertThat(acceptAlertAndGetText()).isIn(expectedAlert.getMessages(args));
        return (T) this;
    }

    private String acceptAlertAndGetText() {
        Alert alert = Selenide.Wait().until(ExpectedConditions.alertIsPresent());
        String actualMessage = alert.getText();
        alert.accept();
        return actualMessage;
    }

    public static void authAsUser(String username, String password) {
        Selenide.open("/");
        String userAuthHeader = RequestSpecs.getUserAuthHeader(username, password);
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);
    }

    public static void authAsUser(CreateUserRequest createUserRequest) {
        authAsUser(createUserRequest.getUsername(), createUserRequest.getPassword());
    }
}
