package iteration_2.api;

import api.dao.AccountDao;
import api.dao.TransactionDao;
import api.dao.comparison.DaoAndModelAssertions;
import api.generators.RandomData;
import api.models.Accounts;
import api.models.CreateUserRequest;
import api.models.DepositRequest;
import api.models.TransactionDto;
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

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class UserDepositTest extends BaseTest {

    @ParameterizedTest
    @ValueSource(doubles = {5000, 4999.99, 0.01})
    public void userCanDepositToSelfAccount(double amount) {
        CreateUserRequest userRequest = AdminSteps.createUser();
        UserSteps.createUserAccount(userRequest);

        int accountId = UserSteps.getUserAccounts(userRequest).getFirst().getId();

        DepositRequest depositRequest = DepositRequest.builder()
                .accountId(accountId)
                .amount(amount)
                .build();

        new CrudRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsOK()
        ).post(depositRequest);

        List<TransactionDto> userTransactions = UserSteps.getAccountTransactions(userRequest, accountId);
        TransactionDao transactionDao = DataBaseSteps.getTransactionsByAccountId(accountId);

        assertThat(userTransactions.getFirst().getAmount()).isEqualTo(depositRequest.getAmount());
        DaoAndModelAssertions.assertThat(userTransactions.getFirst(), transactionDao).match();
    }

    public static Stream<Arguments> invalidBalance() {
        return Stream.of(
                Arguments.of(5000.01, ResponseSpecs.requestReturnsBadRequest(ResponseSpecs.DEPOSIT_MAX_LIMIT)),
                Arguments.of(0, ResponseSpecs.requestReturnsFieldValidationError(ResponseSpecs.AMOUNT_MUST_BE_POSITIVE))
        );
    }

    @ParameterizedTest
    @MethodSource("invalidBalance")
    public void userCannotDepositInadmissibleAmountToSelfAccount(double amount, ResponseSpecification errorSpec) {
        CreateUserRequest userRequest = AdminSteps.createUser();
        int userAccount = (int) UserSteps.createUserAccount(userRequest).getId();

        Accounts balanceBefore = UserSteps.userGetAccountById(userAccount, userRequest);

        DepositRequest depositRequest = DepositRequest.builder()
                .accountId(userAccount)
                .amount(amount)
                .build();

        new CrudRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.DEPOSIT,
                errorSpec
        ).post(depositRequest);

        Accounts balanceAfter = UserSteps.userGetAccountById(userAccount, userRequest);
        AccountDao accountDao = DataBaseSteps.getAccountByAccountId(userAccount);

        assertThat(balanceBefore.getBalance()).isEqualTo(balanceAfter.getBalance());
        assertThat(accountDao.getBalance()).isEqualTo(balanceBefore.getBalance());
    }

    @Test
    public void userCannotDepositToNonExistentAccount() {
        CreateUserRequest userRequest = AdminSteps.createUser();
        int userAccount = (int) UserSteps.createUserAccount(userRequest).getId();

        Accounts balanceBefore = UserSteps.userGetAccountById(userAccount, userRequest);

        DepositRequest depositRequest = DepositRequest.builder()
                .accountId(RandomData.generateNonExistId())
                .amount(RandomData.generateDepositAmount())
                .build();

        new CrudRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsForbidden())
                .post(depositRequest);

        Accounts balanceAfter = UserSteps.userGetAccountById(userAccount, userRequest);
        AccountDao accountDao = DataBaseSteps.getAccountByAccountId(userAccount);

        assertThat(balanceBefore.getBalance()).isEqualTo(balanceAfter.getBalance());
        assertThat(accountDao.getBalance()).isEqualTo(balanceBefore.getBalance());
    }
}
