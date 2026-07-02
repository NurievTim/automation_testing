package iteration_2;

import models.TransferRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import requests.TransferRequester;
import specs.RequestSpecs;
import specs.ResponseSpec;

import java.util.stream.Stream;

public class UserTransferTest {

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 10000, 500})
    public void userCanTransferBetweenTheirAccounts(double amount) {
        TransferRequest transferRequest = TransferRequest.builder()
                .amount(amount)
                .senderAccountId(1)
                .receiverAccountId(2)
                .build();

        new TransferRequester(
                RequestSpecs.userSpec(),
                ResponseSpec.requestReturnsOK("message", "Transfer successful"))
                .post(transferRequest);
    }

    public static Stream<Arguments> invalidAmount() {
        return Stream.of(
                Arguments.of(0, "Transfer amount must be at least 0.01"),
                Arguments.of(10000.1, "Transfer amount cannot exceed 10000")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidAmount")
    public void userCannotTransferInadmissibleAmountBetweenTheirAccounts(double amount, String errorMessage) {
        TransferRequest transferRequest = TransferRequest.builder()
                .amount(amount)
                .senderAccountId(1)
                .receiverAccountId(2)
                .build();

        new TransferRequester(
                RequestSpecs.userSpec(),
                ResponseSpec.requestReturnsBadRequest(errorMessage))
                .post(transferRequest);
    }

    @Test
    public void userHasNotEnoughAmountToTransfer() {
        TransferRequest transferRequest = TransferRequest.builder()
                .amount(5000)
                .senderAccountId(1)
                .receiverAccountId(2)
                .build();

        new TransferRequester(
                RequestSpecs.userSpec(),
                ResponseSpec.requestReturnsBadRequest("Invalid transfer: insufficient funds or invalid accounts"))
                .post(transferRequest);
    }

    @Test
    public void userCanTransferToAnotherUser() {
        TransferRequest transferRequest = TransferRequest.builder()
                .amount(100)
                .senderAccountId(1)
                .receiverAccountId(3)
                .build();

        new TransferRequester(
                RequestSpecs.userSpec(),
                ResponseSpec.requestReturnsOK("message", "Transfer successful"))
                .post(transferRequest);

    }
}
