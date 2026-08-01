package iteration_2.ui;

import api.requests.steps.UserSteps;
import api.generators.RandomData;
import api.models.CreateAccountResponse;
import api.models.CreateUserRequest;
import api.models.CustomerResponse;
import org.junit.jupiter.api.Test;
import api.requests.steps.AdminSteps;
import ui.pages.Alerts;
import ui.pages.UserDashboardPage;

import static org.assertj.core.api.Assertions.assertThat;

public class UserDepositTest extends BaseUiTest{

    @Test
    public void userCanMakeDeposit() {
        CreateUserRequest userRequest = AdminSteps.createUser();
        authAsUser(userRequest);
        CreateAccountResponse userAccount = UserSteps.createUserAccount(userRequest);
        int depositAmount = RandomData.generateDepositAmount();
        new UserDashboardPage()
                .open()
                .goToDepositPage()
                .depositMoney(depositAmount, userAccount.getId())
                .checkAlertMessageAndAccept(
                        Alerts.SUCCESS_DEPOSIT.getMessage(depositAmount, userAccount.getAccountNumber())
                );

        CustomerResponse userProfile = UserSteps.getUserProfile(userRequest);

        assertThat(userProfile.getAccounts().getFirst().getBalance()).isEqualTo(depositAmount);
    }

    @Test
    public void userCannotMakeDeposit() {
        CreateUserRequest userRequest = AdminSteps.createUser();
        authAsUser(userRequest);
        CreateAccountResponse userAccount = UserSteps.createUserAccount(userRequest);
        int invalidDepositAmount = RandomData.generateInvalidDepositAmount();
        new UserDashboardPage()
                .open()
                .goToDepositPage()
                .depositMoney(invalidDepositAmount, userAccount.getId())
                .checkAlertMessageAndAccept(
                        Alerts.DEPOSIT_LESS_OR_EQUAL_5000.getMessage()
                );

        CustomerResponse userProfile = UserSteps.getUserProfile(userRequest);

        assertThat(userProfile.getAccounts().getFirst().getBalance()).isZero();
    }
}
