package iteration_2.api;

import generators.RandomData;
import models.CustomerResponse;
import models.DepositRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import requests.skeleton.Endpoint;
import requests.skeleton.requests.CrudRequester;
import requests.skeleton.requests.ValidatedCrudRequest;
import requests.steps.ProfileSteps;
import specs.RequestSpecs;
import specs.ResponseSpec;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserDepositTest {

    @ParameterizedTest
    @ValueSource(doubles = {5000, 4999.99, 0.01})
    public void userCanDepositToSelfAccount(double amount) {
        CustomerResponse customerResponse = new ValidatedCrudRequest<CustomerResponse>(
                RequestSpecs.userSpec(),
                Endpoint.GET_PROFILE,
                ResponseSpec.requestReturnsOK())
                .get();

        int accountId = customerResponse.getAccounts().getFirst().getId();

        double balanceBefore = ProfileSteps.userGetBalance(accountId);

        DepositRequest depositRequest = DepositRequest.builder()
                .id(accountId)
                .balance(amount)
                .build();

        new CrudRequester(
                RequestSpecs.userSpec(),
                Endpoint.DEPOSIT,
                ResponseSpec.requestReturnsOK())
                .post(depositRequest);

        double balanceAfter = ProfileSteps.userGetBalance(accountId);

        assertTrue(balanceBefore < balanceAfter);

    }

    public static Stream<Arguments> invalidBalance() {
        return Stream.of(
                Arguments.of(1, 5000.01, ResponseSpec.DEPOSIT_MAX_LIMIT),
                Arguments.of(1, 0, ResponseSpec.DEPOSIT_MIN_LIMIT)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidBalance")
    public void userCannotDepositInadmissibleAmountToSelfAccount(int accountId, double amount, String errorMessage) {

        double balanceBefore = ProfileSteps.userGetBalance(accountId);

        DepositRequest depositRequest = DepositRequest.builder()
                .id(accountId)
                .balance(amount)
                .build();

        new CrudRequester(
                RequestSpecs.userSpec(),
                Endpoint.DEPOSIT,
                ResponseSpec.requestReturnsBadRequest(errorMessage))
                .post(depositRequest);

        double balanceAfter = ProfileSteps.userGetBalance(accountId);

        assertEquals(balanceBefore, balanceAfter);
    }

    @Test
    public void userCannotDepositToNonExistentAccount() {

        double balanceBefore = ProfileSteps.userGetBalance(1);

        DepositRequest depositRequest = DepositRequest.builder()
                .id(RandomData.generateNonExistId())
                .balance(RandomData.generateDepositAmount())
                .build();

        new CrudRequester(
                RequestSpecs.userSpec(),
                Endpoint.DEPOSIT,
                ResponseSpec.requestReturnsForbidden())
                .post(depositRequest);

        double balanceAfter = ProfileSteps.userGetBalance(1);

        assertEquals(balanceBefore, balanceAfter);
    }
}
