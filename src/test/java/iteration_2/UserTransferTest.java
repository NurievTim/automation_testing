package iteration_2;

import generators.RandomData;
import models.CustomerResponse;
import models.TransferRequest;
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

public class UserTransferTest {

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 10000, 9999.99})
    public void userCanTransferBetweenTheirAccounts(double amount) {

        CustomerResponse customerResponseBefore = new ValidatedCrudRequest<CustomerResponse>(
                RequestSpecs.userSpec(),
                Endpoint.PROFILE,
                ResponseSpec.requestReturnsOK())
                .get();

        double balanceBefore = customerResponseBefore.getAccounts().get(1).getBalance();

        TransferRequest transferRequest = TransferRequest.builder()
                .amount(amount)
                .senderAccountId(1)
                .receiverAccountId(2)
                .build();

        new CrudRequester(
                RequestSpecs.userSpec(),
                Endpoint.TRANSFER,
                ResponseSpec.requestReturnsOK("message", ResponseSpec.SUCCESS_TRANSFER))
                .post(transferRequest);

        CustomerResponse customerResponseAfter = new ValidatedCrudRequest<CustomerResponse>(
                RequestSpecs.userSpec(),
                Endpoint.PROFILE,
                ResponseSpec.requestReturnsOK())
                .get();

        double balanceAfter = customerResponseAfter.getAccounts().get(1).getBalance();

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

        double balanceBefore = ProfileSteps.userGetBalance(2);

        TransferRequest transferRequest = TransferRequest.builder()
                .amount(amount)
                .senderAccountId(1)
                .receiverAccountId(2)
                .build();

        new CrudRequester(
                RequestSpecs.userSpec(),
                Endpoint.TRANSFER,
                ResponseSpec.requestReturnsBadRequest(errorMessage))
                .post(transferRequest);

        double balanceAfter = ProfileSteps.userGetBalance(2);

        assertEquals(balanceBefore,  balanceAfter);
    }

    @Test
    public void userHasNotEnoughAmountToTransfer() {
        TransferRequest transferRequest = TransferRequest.builder()
                .amount(RandomData.generateTransferAmount())
                .senderAccountId(4)
                .receiverAccountId(1)
                .build();

        new CrudRequester(
                RequestSpecs.userEmptyBalanceSpec(),
                Endpoint.TRANSFER,
                ResponseSpec.requestReturnsBadRequest(ResponseSpec.ERROR_TRANSFER))
                .post(transferRequest);
    }

    @Test
    public void userCanTransferToAnotherUser() {

        CustomerResponse customerResponseBefore = new ValidatedCrudRequest<CustomerResponse>(
                RequestSpecs.secondUserSpec(),
                Endpoint.PROFILE,
                ResponseSpec.requestReturnsOK())
                .get();

        double balanceBefore = customerResponseBefore.getAccounts().getFirst().getBalance();

        TransferRequest transferRequest = TransferRequest.builder()
                .amount(RandomData.generateTransferAmount())
                .senderAccountId(1)
                .receiverAccountId(3)
                .build();

        new CrudRequester(
                RequestSpecs.userSpec(),
                Endpoint.TRANSFER,
                ResponseSpec.requestReturnsOK("message", ResponseSpec.SUCCESS_TRANSFER))
                .post(transferRequest);

        CustomerResponse customerResponseAfter = new ValidatedCrudRequest<CustomerResponse>(
                RequestSpecs.secondUserSpec(),
                Endpoint.PROFILE,
                ResponseSpec.requestReturnsOK())
                .get();

        double balanceAfter = customerResponseAfter.getAccounts().getFirst().getBalance();

        assertTrue(balanceBefore < balanceAfter);

    }
}
