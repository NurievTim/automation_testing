package iteration_2.api;

import api.dao.AccountDao;
import api.dao.comparison.DaoAndModelAssertions;
import api.generators.RandomData;
import api.models.Accounts;
import api.models.CreateUserRequest;
import api.models.TransferRequest;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requests.CrudRequester;
import api.requests.steps.AdminSteps;
import api.requests.steps.DataBaseSteps;
import api.requests.steps.UserSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTransferTest extends BaseTest {

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 10000, 9999.99})
    public void userCanTransferBetweenTheirAccounts(double amount) {
        CreateUserRequest userRequest = AdminSteps.createUser();
        int senderAccount = (int) UserSteps.createUserAccount(userRequest).getId();
        int receiverAccount = (int) UserSteps.createUserAccount(userRequest).getId();
        UserSteps.makeDepositEnoughForTransfer(userRequest, senderAccount);

        Accounts balanceBefore = UserSteps.userGetAccountById(receiverAccount, userRequest);

        TransferRequest transferRequest = TransferRequest.builder()
                .amount(amount)
                .senderAccountId(senderAccount)
                .receiverAccountId(receiverAccount)
                .build();

        new CrudRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsOK(ResponseSpecs.SUCCESS_TRANSFER))
                .post(transferRequest);

        Accounts balanceAfter = UserSteps.userGetAccountById(receiverAccount, userRequest);
        AccountDao accountDao = DataBaseSteps.getAccountByAccountId(receiverAccount);

        assertThat(balanceBefore.getBalance()).isLessThan(balanceAfter.getBalance());
        assertThat(accountDao.getBalance()).isEqualTo(balanceAfter.getBalance());
        DaoAndModelAssertions.assertThat(balanceAfter, accountDao).match();
    }

    public static Stream<Arguments> invalidAmount() {
        return Stream.of(
                Arguments.of(0, ResponseSpecs.requestReturnsFieldValidationError(ResponseSpecs.AMOUNT_MUST_BE_POSITIVE)),
                Arguments.of(10000.01, ResponseSpecs.requestReturnsBadRequest(ResponseSpecs.TRANSFER_MAX_LIMIT))
        );
    }

    @ParameterizedTest
    @MethodSource("invalidAmount")
    public void userCannotTransferInadmissibleAmountBetweenTheirAccounts(double amount, ResponseSpecification errorSpec) {
        CreateUserRequest userRequest = AdminSteps.createUser();
        int senderAccount = (int) UserSteps.createUserAccount(userRequest).getId();
        int receiverAccount = (int) UserSteps.createUserAccount(userRequest).getId();

        Accounts balanceBefore = UserSteps.userGetAccountById(receiverAccount, userRequest);

        TransferRequest transferRequest = TransferRequest.builder()
                .amount(amount)
                .senderAccountId(senderAccount)
                .receiverAccountId(receiverAccount)
                .build();

        new CrudRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.TRANSFER,
                errorSpec
        ).post(transferRequest);

        Accounts balanceAfter = UserSteps.userGetAccountById(receiverAccount, userRequest);
        AccountDao accountDao = DataBaseSteps.getAccountByAccountId(receiverAccount);

        assertThat(balanceBefore.getBalance()).isEqualTo(balanceAfter.getBalance());
        assertThat(accountDao.getBalance()).isEqualTo(balanceBefore.getBalance());
    }

    @Test
    public void userHasNotEnoughAmountToTransfer() {
        CreateUserRequest firstUserRequest = AdminSteps.createUser();
        CreateUserRequest secondUserRequest = AdminSteps.createUser();
        int senderAccount = (int) UserSteps.createUserAccount(firstUserRequest).getId();
        int receiverAccount = (int) UserSteps.createUserAccount(secondUserRequest).getId();

        Accounts balanceBefore = UserSteps.userGetAccountById(receiverAccount, secondUserRequest);

        TransferRequest transferRequest = TransferRequest.builder()
                .amount(RandomData.generateTransferAmount())
                .senderAccountId(senderAccount)
                .receiverAccountId(receiverAccount)
                .build();

        new CrudRequester(
                RequestSpecs.authAsUser(firstUserRequest.getUsername(), firstUserRequest.getPassword()),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsBadRequest(ResponseSpecs.ERROR_TRANSFER))
                .post(transferRequest);

        Accounts balanceAfter = UserSteps.userGetAccountById(receiverAccount, secondUserRequest);
        AccountDao accountDao = DataBaseSteps.getAccountByAccountId(receiverAccount);

        assertThat(balanceBefore.getBalance()).isEqualTo(balanceAfter.getBalance());
        assertThat(accountDao.getBalance()).isEqualTo(balanceBefore.getBalance());
    }

    @Test
    public void userCanTransferToAnotherUser() {
        CreateUserRequest firstUserRequest = AdminSteps.createUser();
        CreateUserRequest secondUserRequest = AdminSteps.createUser();
        int senderAccount = (int) UserSteps.createUserAccount(firstUserRequest).getId();
        int receiverAccount = (int) UserSteps.createUserAccount(secondUserRequest).getId();
        UserSteps.makeDepositEnoughForTransfer(firstUserRequest, senderAccount);

        Accounts balanceBefore = UserSteps.userGetAccountById(receiverAccount, secondUserRequest);

        TransferRequest transferRequest = TransferRequest.builder()
                .amount(RandomData.generateTransferAmount())
                .senderAccountId(senderAccount)
                .receiverAccountId(receiverAccount)
                .build();

        new CrudRequester(
                RequestSpecs.authAsUser(firstUserRequest.getUsername(), firstUserRequest.getPassword()),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsOK(ResponseSpecs.SUCCESS_TRANSFER)
        ).post(transferRequest);

        Accounts balanceAfter = UserSteps.userGetAccountById(receiverAccount, secondUserRequest);
        AccountDao accountDao = DataBaseSteps.getAccountByAccountId(receiverAccount);

        assertThat(balanceBefore.getBalance()).isLessThan(balanceAfter.getBalance());
        assertThat(accountDao.getBalance()).isEqualTo(balanceAfter.getBalance());
    }
}
