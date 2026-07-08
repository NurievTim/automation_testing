package iteration_2;

import generators.RandomData;
import models.CustomerResponse;
import models.TransferRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import requests.CustomerRequester;
import requests.TransferRequester;
import specs.RequestSpecs;
import specs.ResponseSpec;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserTransferTest {

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 10000, 9999.99})
    public void userCanTransferBetweenTheirAccounts(double amount) {

        double balanceBefore = new CustomerRequester(
                RequestSpecs.userSpec(),
                ResponseSpec.requestReturnsOK())
                .get()
                .extract()
                .as(CustomerResponse.class)
                .getAccounts()
                .get(1)
                .getBalance();

        TransferRequest transferRequest = TransferRequest.builder()
                .amount(amount)
                .senderAccountId(1)
                .receiverAccountId(2)
                .build();

        new TransferRequester(
                RequestSpecs.userSpec(),
                ResponseSpec.requestReturnsOK("message", ResponseSpec.SUCCESS_TRANSFER))
                .post(transferRequest);

        double balanceAfter = new CustomerRequester(
                RequestSpecs.userSpec(),
                ResponseSpec.requestReturnsOK())
                .get()
                .extract()
                .as(CustomerResponse.class)
                .getAccounts()
                .get(1)
                .getBalance();

        assertTrue(balanceBefore < balanceAfter);
    }

    public static Stream<Arguments> invalidAmount() {
        return Stream.of(
                Arguments.of(0, ResponseSpec.TRANSFER_MIN_LIMIT),
                Arguments.of(10000.01, ResponseSpec.TRANSFER_MAX_LIMIT)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidAmount")
    public void userCannotTransferInadmissibleAmountBetweenTheirAccounts(double amount, String errorMessage) {

        double balanceBefore = new CustomerRequester(
                RequestSpecs.userSpec(),
                ResponseSpec.requestReturnsOK())
                .get()
                .extract()
                .as(CustomerResponse.class)
                .getAccounts()
                .get(1)
                .getBalance();

        TransferRequest transferRequest = TransferRequest.builder()
                .amount(amount)
                .senderAccountId(1)
                .receiverAccountId(2)
                .build();

        new TransferRequester(
                RequestSpecs.userSpec(),
                ResponseSpec.requestReturnsBadRequest(errorMessage))
                .post(transferRequest);

        double balanceAfter = new CustomerRequester(
                RequestSpecs.userSpec(),
                ResponseSpec.requestReturnsOK())
                .get()
                .extract()
                .as(CustomerResponse.class)
                .getAccounts()
                .get(1)
                .getBalance();

        assertEquals(balanceBefore,  balanceAfter);
    }

    @Test
    public void userHasNotEnoughAmountToTransfer() {
        TransferRequest transferRequest = TransferRequest.builder()
                .amount(RandomData.generateTransferAmount())
                .senderAccountId(4)
                .receiverAccountId(1)
                .build();

        new TransferRequester(
                RequestSpecs.userEmptyBalanceSpec(),
                ResponseSpec.requestReturnsBadRequest(ResponseSpec.ERROR_TRANSFER))
                .post(transferRequest);
    }

    @Test
    public void userCanTransferToAnotherUser() {

        double balanceBefore = new CustomerRequester(
                RequestSpecs.secondUserSpec(),
                ResponseSpec.requestReturnsOK())
                .get()
                .extract()
                .as(CustomerResponse.class)
                .getAccounts()
                .getFirst()
                .getBalance();

        TransferRequest transferRequest = TransferRequest.builder()
                .amount(RandomData.generateTransferAmount())
                .senderAccountId(1)
                .receiverAccountId(3)
                .build();

        new TransferRequester(
                RequestSpecs.userSpec(),
                ResponseSpec.requestReturnsOK("message", ResponseSpec.SUCCESS_TRANSFER))
                .post(transferRequest);

        double balanceAfter = new CustomerRequester(
                RequestSpecs.secondUserSpec(),
                ResponseSpec.requestReturnsOK())
                .get()
                .extract()
                .as(CustomerResponse.class)
                .getAccounts()
                .getFirst()
                .getBalance();

        assertTrue(balanceBefore < balanceAfter);

    }
}
