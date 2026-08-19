package iteration_2;

import generators.RandomData;
import models.Accounts;
import models.CustomerResponse;
import models.DepositRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import requests.CustomerRequester;
import requests.DepositRequester;
import specs.RequestSpecs;
import specs.ResponseSpec;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserDepositTest {

    @ParameterizedTest
    @ValueSource(doubles = {5000, 4999.99, 0.01})
    public void userCanDepositToSelfAccount(double amount) {

        Accounts account = new CustomerRequester(
                RequestSpecs.userSpec(),
                ResponseSpec.requestReturnsOK())
                .get()
                .extract()
                .as(CustomerResponse.class)
                .getAccounts()
                .getFirst();

        DepositRequest depositRequest = DepositRequest.builder()
                .id(account.getId())
                .balance(amount)
                .build();

        new DepositRequester(
                RequestSpecs.userSpec(),
                ResponseSpec.requestReturnsOK())
                .post(depositRequest);

        double balanceAfter = new CustomerRequester(
                RequestSpecs.userSpec(),
                ResponseSpec.requestReturnsOK())
                .get()
                .extract()
                .as(CustomerResponse.class)
                .getAccounts()
                .getFirst()
                .getBalance();

        assertTrue(account.getBalance() < balanceAfter);
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

        Accounts account = new CustomerRequester(
                RequestSpecs.userSpec(),
                ResponseSpec.requestReturnsOK())
                .get()
                .extract()
                .as(CustomerResponse.class)
                .getAccounts()
                .getFirst();

        DepositRequest depositRequest = DepositRequest.builder()
                .id(account.getId())
                .balance(amount)
                .build();

        new DepositRequester(
                RequestSpecs.userSpec(),
                ResponseSpec.requestReturnsBadRequest(errorMessage))
                .post(depositRequest);

        double balanceAfter = new CustomerRequester(
                RequestSpecs.userSpec(),
                ResponseSpec.requestReturnsOK())
                .get()
                .extract()
                .as(CustomerResponse.class)
                .getAccounts()
                .getFirst()
                .getBalance();

        assertEquals(account.getBalance(), balanceAfter);
    }

    @Test
    public void userCannotDepositToNonExistentAccount() {
        DepositRequest depositRequest = DepositRequest.builder()
                .id(10)
                .balance(RandomData.generateDepositAmount())
                .build();

        new DepositRequester(
                RequestSpecs.userSpec(),
                ResponseSpec.requestReturnsForbidden())
                .post(depositRequest);
    }
}
