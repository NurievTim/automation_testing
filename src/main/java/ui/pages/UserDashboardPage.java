package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class UserDashboardPage extends BasePage<UserDashboardPage> {
    private final SelenideElement userInfo = $(Selectors.byClassName("user-info"));
    private final SelenideElement depositMoneyButton = $(Selectors.byTagAndText("button", "\uD83D\uDCB0 Deposit Money"));
    private final SelenideElement transferMoneyButton = $(Selectors.byTagAndText("button", "\uD83D\uDD04 Make a Transfer"));

    @Override
    public String url() {
        return "/dashboard";
    }

    public ProfilePage goToProfilePage() {
        userInfo.click();
        return getPage(ProfilePage.class);
    }

    public DepositPage goToDepositPage() {
        depositMoneyButton.click();
        return getPage(DepositPage.class);
    }

    public TransferPage goToTransferPage() {
        transferMoneyButton.click();
        return getPage(TransferPage.class);
    }


}
