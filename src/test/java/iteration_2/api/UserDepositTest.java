package iteration_2.api;

import api.generators.RandomData;
import api.models.CreateUserRequest;
import api.models.CustomerResponse;
import api.models.DepositRequest;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requests.CrudRequester;
import api.requests.skeleton.requests.ValidatedCrudRequester;
import api.requests.steps.AdminSteps;
import api.requests.steps.ProfileSteps;
import api.requests.steps.UserSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserDepositTest {

    @ParameterizedTest
    @ValueSource(doubles = {5000, 4999.99, 0.01})
    public void userCanDepositToSelfAccount(double amount) {
        CreateUserRequest userRequest = AdminSteps.createUser();
        UserSteps.createUserAccount(userRequest);
        CustomerResponse customerResponse = new ValidatedCrudRequester<CustomerResponse>(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.GET_PROFILE,
                ResponseSpecs.requestReturnsOK())
                .get();

        int accountId = customerResponse.getAccounts().getFirst().getId();

        double balanceBefore = ProfileSteps.userGetBalance(accountId, userRequest);

        DepositRequest depositRequest = DepositRequest.builder()
                .id(accountId)
                .balance(amount)
                .build();

        new CrudRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsOK())
                .post(depositRequest);

        double balanceAfter = ProfileSteps.userGetBalance(accountId, userRequest);

        assertTrue(balanceBefore < balanceAfter);

    }

    public static Stream<Arguments> invalidBalance() {
        return Stream.of(
                Arguments.of(1, 5000.01, ResponseSpecs.DEPOSIT_MAX_LIMIT),
                Arguments.of(1, 0, ResponseSpecs.DEPOSIT_MIN_LIMIT)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidBalance")
    public void userCannotDepositInadmissibleAmountToSelfAccount(int accountId, double amount, String errorMessage) {
        CreateUserRequest userRequest = AdminSteps.createUser();
        UserSteps.createUserAccount(userRequest);

        double balanceBefore = ProfileSteps.userGetBalance(accountId, userRequest);

        DepositRequest depositRequest = DepositRequest.builder()
                .id(accountId)
                .balance(amount)
                .build();

        new CrudRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsBadRequest(errorMessage))
                .post(depositRequest);

        double balanceAfter = ProfileSteps.userGetBalance(accountId, userRequest);

        assertEquals(balanceBefore, balanceAfter);
    }

    @Test
    public void userCannotDepositToNonExistentAccount() {
        CreateUserRequest userRequest = AdminSteps.createUser();
        UserSteps.createUserAccount(userRequest);

        double balanceBefore = ProfileSteps.userGetBalance(1, userRequest);

        DepositRequest depositRequest = DepositRequest.builder()
                .id(RandomData.generateNonExistId())
                .balance(RandomData.generateDepositAmount())
                .build();

        new CrudRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsForbidden())
                .post(depositRequest);

        double balanceAfter = ProfileSteps.userGetBalance(1, userRequest);

        assertEquals(balanceBefore, balanceAfter);
    }
}
