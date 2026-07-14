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

        CustomerResponse customerResponse = new ValidatedCrudRequest<CustomerResponse>(
                RequestSpecs.userSpec(),
                Endpoint.GET_PROFILE,
                ResponseSpec.requestReturnsOK())
                .get();

        int senderAccountId = customerResponse.getAccounts().getFirst().getId();
        int receiverAccountId = customerResponse.getAccounts().get(1).getId();
        double balanceBefore = ProfileSteps.userGetBalance(receiverAccountId);

        TransferRequest transferRequest = TransferRequest.builder()
                .amount(amount)
                .senderAccountId(senderAccountId)
                .receiverAccountId(receiverAccountId)
                .build();

        new CrudRequester(
                RequestSpecs.userSpec(),
                Endpoint.TRANSFER,
                ResponseSpec.requestReturnsOK(ResponseSpec.SUCCESS_TRANSFER))
                .post(transferRequest);

        double balanceAfter = ProfileSteps.userGetBalance(receiverAccountId);

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
        CustomerResponse customerResponse = new ValidatedCrudRequest<CustomerResponse>(
                RequestSpecs.userSpec(),
                Endpoint.GET_PROFILE,
                ResponseSpec.requestReturnsOK())
                .get();

        int firstId = customerResponse.getAccounts().getFirst().getId();
        int secondId = customerResponse.getAccounts().get(1).getId();

        double balanceBefore = ProfileSteps.userGetBalance(secondId);

        TransferRequest transferRequest = TransferRequest.builder()
                .amount(amount)
                .senderAccountId(firstId)
                .receiverAccountId(secondId)
                .build();

        new CrudRequester(
                RequestSpecs.userSpec(),
                Endpoint.TRANSFER,
                ResponseSpec.requestReturnsBadRequest(errorMessage))
                .post(transferRequest);

        double balanceAfter = ProfileSteps.userGetBalance(secondId);

        assertEquals(balanceBefore, balanceAfter);
    }

    @Test
    public void userHasNotEnoughAmountToTransfer() {
        CustomerResponse firstCustomerResponse = new ValidatedCrudRequest<CustomerResponse>(
                RequestSpecs.userSpec(),
                Endpoint.GET_PROFILE,
                ResponseSpec.requestReturnsOK())
                .get();

        int receiverAccountId = firstCustomerResponse.getAccounts().getFirst().getId();

        CustomerResponse secondCustomerResponse = new ValidatedCrudRequest<CustomerResponse>(
                RequestSpecs.userEmptyBalanceSpec(),
                Endpoint.GET_PROFILE,
                ResponseSpec.requestReturnsOK())
                .get();

        int senderAccountId = secondCustomerResponse.getAccounts().getFirst().getId();

        double balanceBefore = ProfileSteps.userGetBalance(receiverAccountId);

        TransferRequest transferRequest = TransferRequest.builder()
                .amount(RandomData.generateTransferAmount())
                .senderAccountId(senderAccountId)
                .receiverAccountId(receiverAccountId)
                .build();

        new CrudRequester(
                RequestSpecs.userEmptyBalanceSpec(),
                Endpoint.TRANSFER,
                ResponseSpec.requestReturnsBadRequest(ResponseSpec.ERROR_TRANSFER))
                .post(transferRequest);

        double balanceAfter = ProfileSteps.userGetBalance(receiverAccountId);

        assertEquals(balanceBefore, balanceAfter);
    }

    @Test
    public void userCanTransferToAnotherUser() {

        CustomerResponse firstCustomerResponse = new ValidatedCrudRequest<CustomerResponse>(
                RequestSpecs.userSpec(),
                Endpoint.GET_PROFILE,
                ResponseSpec.requestReturnsOK())
                .get();

        int senderAccountId = firstCustomerResponse.getAccounts().getFirst().getId();

        CustomerResponse secondCustomerResponse = new ValidatedCrudRequest<CustomerResponse>(
                RequestSpecs.secondUserSpec(),
                Endpoint.GET_PROFILE,
                ResponseSpec.requestReturnsOK())
                .get();

        int receiverAccountId = secondCustomerResponse.getAccounts().getFirst().getId();
        double balanceBefore = secondCustomerResponse.getAccounts().getFirst().getBalance();

        TransferRequest transferRequest = TransferRequest.builder()
                .amount(RandomData.generateTransferAmount())
                .senderAccountId(senderAccountId)
                .receiverAccountId(receiverAccountId)
                .build();

        new CrudRequester(
                RequestSpecs.userSpec(),
                Endpoint.TRANSFER,
                ResponseSpec.requestReturnsOK(ResponseSpec.SUCCESS_TRANSFER))
                .post(transferRequest);

        CustomerResponse secondCustomerResponseAfter = new ValidatedCrudRequest<CustomerResponse>(
                RequestSpecs.secondUserSpec(),
                Endpoint.GET_PROFILE,
                ResponseSpec.requestReturnsOK())
                .get();

        double balanceAfter = secondCustomerResponseAfter.getAccounts().getFirst().getBalance();

        assertTrue(balanceBefore < balanceAfter);
    }
}
