package iteration_2;

import models.DepositRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import requests.DepositRequester;
import specs.RequestSpecs;
import specs.ResponseSpec;

import java.util.stream.Stream;

public class UserDepositTest {

    @ParameterizedTest
    @ValueSource(doubles = {5000, 200, 0.01})
    public void userCanDepositToSelfAccount(double amount) {
        DepositRequest depositRequest = DepositRequest.builder()
                .id(1)
                .balance(amount)
                .build();

        new DepositRequester(
                RequestSpecs.userSpec(),
                ResponseSpec.requestReturnsOK())
                .post(depositRequest);
    }

    public static Stream<Arguments> invalidBalance() {
        return Stream.of(
                Arguments.of(5000.1, "Deposit amount cannot exceed 5000"),
                Arguments.of(0, "Deposit amount must be at least 0.01")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidBalance")
    public void userCannotDepositInadmissibleAmountToSelfAccount(double amount, String errorMessage) {
        DepositRequest depositRequest = DepositRequest.builder()
                .id(1)
                .balance(amount)
                .build();

        new DepositRequester(
                RequestSpecs.userSpec(),
                ResponseSpec.requestReturnsBadRequest(errorMessage))
                .post(depositRequest);
    }

    @Test
    public void userCannotDepositToNonExistentAccount() {
        DepositRequest depositRequest = DepositRequest.builder()
                .id(10)
                .balance(100)
                .build();

        new DepositRequester(
                RequestSpecs.userSpec(),
                ResponseSpec.requestReturnsForbidden("Unauthorized access to account"))
                .post(depositRequest);
    }
}
