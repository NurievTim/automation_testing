package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class DepositPage extends BasePage<DepositPage>{
    @Override
    public String url() {
        return "/deposit";
    }
    private final SelenideElement accountSelector = $(Selectors.byClassName("account-selector"));
    private final SelenideElement amountPlaceholder = $(Selectors.byPlaceholder("Enter amount"));
    private final SelenideElement depositButton = $(Selectors.byTagAndText("button", "\uD83D\uDCB5 Deposit"));

    public DepositPage fillDepositForm(int amount, long accountId) {
        accountSelector.selectOptionByValue(String.valueOf(accountId));
        amountPlaceholder.sendKeys(String.valueOf(amount));
        return this;
    }

    public DepositPage depositMoney(int amount, long accountId) {
        fillDepositForm(amount, accountId);
        depositButton.click();
        return this;
    }
}
