package iteration_2;

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
import specs.RequestSpecs;
import specs.ResponseSpec;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserDepositTest {

    @ParameterizedTest
    @ValueSource(doubles = {5000, 4999.99, 0.01})
    public void userCanDepositToSelfAccount(double amount) {

        CustomerResponse customerResponseBefore = new ValidatedCrudRequest<CustomerResponse>(
                RequestSpecs.userSpec(),
                Endpoint.PROFILE,
                ResponseSpec.requestReturnsOK())
                .get();

        double balanceBefore = customerResponseBefore.getAccounts().getFirst().getBalance();

        DepositRequest depositRequest = DepositRequest.builder()
                .id(1)
                .balance(amount)
                .build();

        new CrudRequester(
                RequestSpecs.userSpec(),
                Endpoint.DEPOSIT,
                ResponseSpec.requestReturnsOK())
                .post(depositRequest);

        CustomerResponse customerResponseAfter = new ValidatedCrudRequest<CustomerResponse>(
                RequestSpecs.userSpec(),
                Endpoint.PROFILE,
                ResponseSpec.requestReturnsOK())
                .get();

        double balanceAfter = customerResponseAfter.getAccounts().getFirst().getBalance();

        assertTrue(balanceBefore < balanceAfter);
    }

    public static Stream<Arguments> invalidBalance() {
        return Stream.of(
                Arguments.of(5000.01, ResponseSpec.DEPOSIT_MAX_LIMIT),
                Arguments.of(0, ResponseSpec.DEPOSIT_MIN_LIMIT)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidBalance")
    public void userCannotDepositInadmissibleAmountToSelfAccount(double amount, String errorMessage) {

        CustomerResponse customerResponseBefore = new ValidatedCrudRequest<CustomerResponse>(
                RequestSpecs.userSpec(),
                Endpoint.PROFILE,
                ResponseSpec.requestReturnsOK())
                .get();

        double balanceBefore = customerResponseBefore.getAccounts().getFirst().getBalance();

        DepositRequest depositRequest = DepositRequest.builder()
                .id(1)
                .balance(amount)
                .build();

        new CrudRequester(
                RequestSpecs.userSpec(),
                Endpoint.DEPOSIT,
                ResponseSpec.requestReturnsBadRequest(errorMessage))
                .post(depositRequest);

        CustomerResponse customerResponseAfter = new ValidatedCrudRequest<CustomerResponse>(
                RequestSpecs.userSpec(),
                Endpoint.PROFILE,
                ResponseSpec.requestReturnsOK())
                .get();

        double balanceAfter = customerResponseAfter.getAccounts().getFirst().getBalance();

        assertEquals(balanceBefore, balanceAfter);
    }

    @Test
    public void userCannotDepositToNonExistentAccount() {
        DepositRequest depositRequest = DepositRequest.builder()
                .id(10)
                .balance(RandomData.generateDepositAmount())
                .build();

        new CrudRequester(
                RequestSpecs.userSpec(),
                Endpoint.DEPOSIT,
                ResponseSpec.requestReturnsForbidden())
                .post(depositRequest);
    }
}
