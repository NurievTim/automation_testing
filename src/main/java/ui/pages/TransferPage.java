package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class TransferPage extends BasePage<TransferPage> {
    private final SelenideElement accountSelector = $(Selectors.byClassName("account-selector"));
    private final SelenideElement recipientAccountPlaceholder = $(Selectors.byPlaceholder("Enter recipient account number"));
    private final SelenideElement amountPlaceholder = $(Selectors.byPlaceholder("Enter amount"));
    private final SelenideElement transferButton = $(Selectors.byTagAndText("button", "\uD83D\uDE80 Send Transfer"));
    private final SelenideElement confirmCheckbox = $(Selectors.byAttribute("id", "confirmCheck"));

    @Override
    public String url() {
        return "/transfer";
    }

    public TransferPage fillTransferForm(long fromAccount, String toAccount, int transferAmount) {
        accountSelector.selectOptionByValue(String.valueOf(fromAccount));
        recipientAccountPlaceholder.sendKeys(toAccount);
        amountPlaceholder.sendKeys(String.valueOf(transferAmount));
        return this;
    }

    public TransferPage confirmDetails() {
        confirmCheckbox.click();
        return this;
    }

    public TransferPage submitTransfer() {
        transferButton.click();
        return this;
    }

    public TransferPage makeTransfer(long fromAccount, String toAccount, int transferAmount) {
        return fillTransferForm(fromAccount, toAccount, transferAmount)
                .confirmDetails()
                .submitTransfer();
    }
}
