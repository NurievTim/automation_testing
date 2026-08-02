package iteration_2.ui;

import api.models.*;
import api.requests.steps.UserSteps;
import api.generators.RandomData;
import org.junit.jupiter.api.Test;
import api.requests.steps.AdminSteps;
import ui.pages.Alerts;
import ui.pages.UserDashboardPage;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTransferTest extends BaseUiTest {

    @Test
    public void userCanMakeTransfer() {
        CreateUserRequest userRequest = AdminSteps.createUser();
        authAsUser(userRequest);
        CreateAccountResponse firstUserAccount = UserSteps.createUserAccount(userRequest);
        CreateAccountResponse secondUserAccount = UserSteps.createUserAccount(userRequest);

        int transferAmount = RandomData.generateDepositAmount();

        UserSteps.makeDeposit(userRequest, firstUserAccount.getId(), transferAmount);

        new UserDashboardPage()
                .open()
                .goToTransferPage()
                .makeTransfer(firstUserAccount.getId(), secondUserAccount.getAccountNumber(), transferAmount)
                .checkAlertMessageAndAccept(Alerts.SUCCESS_TRANSFER
                        .getMessage(transferAmount, secondUserAccount.getAccountNumber()));

        CustomerResponse userProfile = UserSteps.getUserProfile(userRequest);

        double secondAccountBalance = userProfile.getAccounts().stream()
                .filter(acc -> acc.getId() == secondUserAccount.getId())
                .findFirst()
                .orElseThrow()
                .getBalance();

        assertThat(secondAccountBalance).isEqualTo(transferAmount);
    }

    @Test
    public void userCannotMakeTransferWithInvalidAmount() {
        CreateUserRequest userRequest = AdminSteps.createUser();
        authAsUser(userRequest);
        CreateAccountResponse firstUserAccount = UserSteps.createUserAccount(userRequest);
        CreateAccountResponse secondUserAccount = UserSteps.createUserAccount(userRequest);

        int transferAmount = RandomData.generateInvalidTransferAmount();

        new UserDashboardPage()
                .open()
                .goToTransferPage()
                .makeTransfer(firstUserAccount.getId(), secondUserAccount.getAccountNumber(), transferAmount)
                .checkAlertMessageAndAccept(Alerts.TRANSFER_AMOUNT_CANNOT_EXCEED_10000.getMessage());

        CustomerResponse userProfile = UserSteps.getUserProfile(userRequest);

        double secondAccountBalance = userProfile.getAccounts().stream()
                .filter(acc -> acc.getId() == secondUserAccount.getId())
                .findFirst()
                .orElseThrow()
                .getBalance();

        assertThat(secondAccountBalance).isNotEqualTo(transferAmount);
    }

    @Test
    public void userCannotMakeTransferNotConfirmed() {
        CreateUserRequest userRequest = AdminSteps.createUser();
        authAsUser(userRequest);
        CreateAccountResponse firstUserAccount = UserSteps.createUserAccount(userRequest);
        CreateAccountResponse secondUserAccount = UserSteps.createUserAccount(userRequest);

        int transferAmount = RandomData.generateDepositAmount();

        UserSteps.makeDeposit(userRequest, firstUserAccount.getId(), transferAmount);

        new UserDashboardPage()
                .open()
                .goToTransferPage()
                .fillTransferForm(firstUserAccount.getId(), secondUserAccount.getAccountNumber(), transferAmount)
                .submitTransfer()
                .checkAlertMessageAndAccept(Alerts.FILL_ALL_FIELDS_AND_CONFIRM.getMessage());

        CustomerResponse userProfile = UserSteps.getUserProfile(userRequest);

        double secondAccountBalance = userProfile.getAccounts().stream()
                .filter(acc -> acc.getId() == secondUserAccount.getId())
                .findFirst()
                .orElseThrow()
                .getBalance();

        assertThat(secondAccountBalance).isZero();
    }
}
