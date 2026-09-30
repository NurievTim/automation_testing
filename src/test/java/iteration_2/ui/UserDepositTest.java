package iteration_2.ui;

import api.configs.SessionStorage;
import api.generators.RandomData;
import api.models.CreateAccountResponse;
import api.requests.steps.UserSteps;
import common.annotations.UserSession;
import org.junit.jupiter.api.Test;
import ui.pages.Alerts;
import ui.pages.UserDashboardPage;

import static org.assertj.core.api.Assertions.assertThat;

public class UserDepositTest extends BaseUiTest{

    @Test
    @UserSession
    public void userCanMakeDeposit() {
        CreateAccountResponse userAccount = UserSteps.createUserAccount(SessionStorage.getUser());
        int depositAmount = RandomData.generateDepositAmount();
        new UserDashboardPage()
                .open()
                .goToDepositPage()
                .depositMoney(depositAmount, userAccount.getId())
                .checkAlertMessageAndAccept(
                        Alerts.SUCCESS_DEPOSIT.getMessage(depositAmount, userAccount.getAccountNumber())
                );

        double accountBalance = UserSteps.getUserAccounts(SessionStorage.getUser()).getFirst().getBalance();

        assertThat(accountBalance).isEqualTo(depositAmount);
    }

    @Test
    @UserSession
    public void userCannotMakeDeposit() {
        CreateAccountResponse userAccount = UserSteps.createUserAccount(SessionStorage.getUser());
        int invalidDepositAmount = RandomData.generateInvalidDepositAmount();
        new UserDashboardPage()
                .open()
                .goToDepositPage()
                .depositMoney(invalidDepositAmount, userAccount.getId())
                .checkAlertMessageAndAccept(
                        Alerts.DEPOSIT_LESS_OR_EQUAL_5000.getMessage()
                );

        double accountBalance = UserSteps.getUserAccounts(SessionStorage.getUser()).getFirst().getBalance();

        assertThat(accountBalance).isZero();
    }
}
