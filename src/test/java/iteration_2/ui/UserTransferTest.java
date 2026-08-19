package iteration_2.ui;

import api.configs.SessionStorage;
import api.generators.RandomData;
import api.models.CreateAccountResponse;
import api.models.CreateUserRequest;
import api.models.CustomerResponse;
import api.requests.steps.UserSteps;
import common.annotations.UserSession;
import org.junit.jupiter.api.Test;
import ui.pages.Alerts;
import ui.pages.UserDashboardPage;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTransferTest extends BaseUiTest {

    @Test
    @UserSession
    public void userCanMakeTransfer() {
        CreateUserRequest user = SessionStorage.getUser();
        CreateAccountResponse firstUserAccount = UserSteps.createUserAccount(user);
        CreateAccountResponse secondUserAccount = UserSteps.createUserAccount(user);

        int transferAmount = RandomData.generateDepositAmount();

        UserSteps.makeDeposit(SessionStorage.getUser(), firstUserAccount.getId(), transferAmount);

        new UserDashboardPage()
                .open()
                .goToTransferPage()
                .makeTransfer(firstUserAccount.getId(), secondUserAccount.getAccountNumber(), transferAmount)
                .checkAlertMessageAndAccept(Alerts.SUCCESS_TRANSFER
                        .getMessage(transferAmount, secondUserAccount.getAccountNumber()));

        CustomerResponse userProfile = UserSteps.getUserProfile(user);

        double secondAccountBalance = userProfile.getAccounts().stream()
                .filter(acc -> acc.getId() == secondUserAccount.getId())
                .findFirst()
                .orElseThrow()
                .getBalance();

        assertThat(secondAccountBalance).isEqualTo(transferAmount);
    }

    @Test
    @UserSession
    public void userCannotMakeTransferWithInvalidAmount() {
        CreateUserRequest user = SessionStorage.getUser();
        CreateAccountResponse firstUserAccount = UserSteps.createUserAccount(user);
        CreateAccountResponse secondUserAccount = UserSteps.createUserAccount(user);

        int transferAmount = RandomData.generateInvalidTransferAmount();

        new UserDashboardPage()
                .open()
                .goToTransferPage()
                .makeTransfer(firstUserAccount.getId(), secondUserAccount.getAccountNumber(), transferAmount)
                .checkAlertMessageAndAccept(Alerts.TRANSFER_AMOUNT_CANNOT_EXCEED_10000.getMessage());

        CustomerResponse userProfile = UserSteps.getUserProfile(user);

        double secondAccountBalance = userProfile.getAccounts().stream()
                .filter(acc -> acc.getId() == secondUserAccount.getId())
                .findFirst()
                .orElseThrow()
                .getBalance();

        assertThat(secondAccountBalance).isNotEqualTo(transferAmount);
    }

    @Test
    @UserSession
    public void userCannotMakeTransferNotConfirmed() {
        CreateUserRequest user = SessionStorage.getUser();
        CreateAccountResponse firstUserAccount = UserSteps.createUserAccount(user);
        CreateAccountResponse secondUserAccount = UserSteps.createUserAccount(user);

        int transferAmount = RandomData.generateDepositAmount();

        UserSteps.makeDeposit(user, firstUserAccount.getId(), transferAmount);

        new UserDashboardPage()
                .open()
                .goToTransferPage()
                .fillTransferForm(firstUserAccount.getId(), secondUserAccount.getAccountNumber(), transferAmount)
                .submitTransfer()
                .checkAlertMessageAndAccept(Alerts.FILL_ALL_FIELDS_AND_CONFIRM.getMessage());

        CustomerResponse userProfile = UserSteps.getUserProfile(user);

        double secondAccountBalance = userProfile.getAccounts().stream()
                .filter(acc -> acc.getId() == secondUserAccount.getId())
                .findFirst()
                .orElseThrow()
                .getBalance();

        assertThat(secondAccountBalance).isZero();
    }
}
