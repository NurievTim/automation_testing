package iteration_2.api;

import api.generators.RandomData;
import api.models.CustomerResponse;
import api.models.TransferRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requests.CrudRequester;
import api.requests.skeleton.requests.ValidatedCrudRequester;
import api.requests.steps.ProfileSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserTransferTest {

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 10000, 9999.99})
    public void userCanTransferBetweenTheirAccounts(double amount) {

        CustomerResponse customerResponse = new ValidatedCrudRequester<CustomerResponse>(
                RequestSpecs.userSpec(),
                Endpoint.GET_PROFILE,
                ResponseSpecs.requestReturnsOK())
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
                ResponseSpecs.requestReturnsOK(ResponseSpecs.SUCCESS_TRANSFER))
                .post(transferRequest);

        double balanceAfter = ProfileSteps.userGetBalance(receiverAccountId);

        assertTrue(balanceBefore < balanceAfter);
    }

    public static Stream<Arguments> invalidAmount() {
        return Stream.of(
                Arguments.of(0, ResponseSpecs.TRANSFER_MIN_LIMIT),
                Arguments.of(10000.01, ResponseSpecs.TRANSFER_MAX_LIMIT)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidAmount")
    public void userCannotTransferInadmissibleAmountBetweenTheirAccounts(double amount, String errorMessage) {
        CustomerResponse customerResponse = new ValidatedCrudRequester<CustomerResponse>(
                RequestSpecs.userSpec(),
                Endpoint.GET_PROFILE,
                ResponseSpecs.requestReturnsOK())
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
                ResponseSpecs.requestReturnsBadRequest(errorMessage))
                .post(transferRequest);

        double balanceAfter = ProfileSteps.userGetBalance(secondId);

        assertEquals(balanceBefore, balanceAfter);
    }

    @Test
    public void userHasNotEnoughAmountToTransfer() {
        CustomerResponse firstCustomerResponse = new ValidatedCrudRequester<CustomerResponse>(
                RequestSpecs.userSpec(),
                Endpoint.GET_PROFILE,
                ResponseSpecs.requestReturnsOK())
                .get();

        int receiverAccountId = firstCustomerResponse.getAccounts().getFirst().getId();

        CustomerResponse secondCustomerResponse = new ValidatedCrudRequester<CustomerResponse>(
                RequestSpecs.userEmptyBalanceSpec(),
                Endpoint.GET_PROFILE,
                ResponseSpecs.requestReturnsOK())
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
                ResponseSpecs.requestReturnsBadRequest(ResponseSpecs.ERROR_TRANSFER))
                .post(transferRequest);

        double balanceAfter = ProfileSteps.userGetBalance(receiverAccountId);

        assertEquals(balanceBefore, balanceAfter);
    }

    @Test
    public void userCanTransferToAnotherUser() {

        CustomerResponse firstCustomerResponse = new ValidatedCrudRequester<CustomerResponse>(
                RequestSpecs.userSpec(),
                Endpoint.GET_PROFILE,
                ResponseSpecs.requestReturnsOK())
                .get();

        int senderAccountId = firstCustomerResponse.getAccounts().getFirst().getId();

        CustomerResponse secondCustomerResponse = new ValidatedCrudRequester<CustomerResponse>(
                RequestSpecs.secondUserSpec(),
                Endpoint.GET_PROFILE,
                ResponseSpecs.requestReturnsOK())
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
                ResponseSpecs.requestReturnsOK(ResponseSpecs.SUCCESS_TRANSFER))
                .post(transferRequest);

        CustomerResponse secondCustomerResponseAfter = new ValidatedCrudRequester<CustomerResponse>(
                RequestSpecs.secondUserSpec(),
                Endpoint.GET_PROFILE,
                ResponseSpecs.requestReturnsOK())
                .get();

        double balanceAfter = secondCustomerResponseAfter.getAccounts().getFirst().getBalance();

        assertTrue(balanceBefore < balanceAfter);
    }
}
