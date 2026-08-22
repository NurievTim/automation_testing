package iteration_2.api;

import api.generators.RandomData;
import api.models.CreateUserRequest;
import api.models.TransferRequest;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requests.CrudRequester;
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

public class UserTransferTest extends BaseTest {

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 10000, 9999.99})
    public void userCanTransferBetweenTheirAccounts(double amount) {
        CreateUserRequest userRequest = AdminSteps.createUser();
        int senderAccount = (int) UserSteps.createUserAccount(userRequest).getId();
        int receiverAccount = (int) UserSteps.createUserAccount(userRequest).getId();
        UserSteps.makeDepositEnoughForTransfer(userRequest, senderAccount);

        double balanceBefore = ProfileSteps.userGetBalance(receiverAccount, userRequest);

        TransferRequest transferRequest = TransferRequest.builder()
                .amount(amount)
                .senderAccountId(senderAccount)
                .receiverAccountId(receiverAccount)
                .build();

        new CrudRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsOK(ResponseSpecs.SUCCESS_TRANSFER))
                .post(transferRequest);

        double balanceAfter = ProfileSteps.userGetBalance(receiverAccount, userRequest);

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
        CreateUserRequest userRequest = AdminSteps.createUser();
        int senderAccount = (int) UserSteps.createUserAccount(userRequest).getId();
        int receiverAccount = (int) UserSteps.createUserAccount(userRequest).getId();

        double balanceBefore = ProfileSteps.userGetBalance(receiverAccount, userRequest);

        TransferRequest transferRequest = TransferRequest.builder()
                .amount(amount)
                .senderAccountId(senderAccount)
                .receiverAccountId(receiverAccount)
                .build();

        new CrudRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsBadRequest(errorMessage))
                .post(transferRequest);

        double balanceAfter = ProfileSteps.userGetBalance(receiverAccount, userRequest);

        assertEquals(balanceBefore, balanceAfter);
    }

    @Test
    public void userHasNotEnoughAmountToTransfer() {
        CreateUserRequest firstUserRequest = AdminSteps.createUser();
        CreateUserRequest secondUserRequest = AdminSteps.createUser();
        int senderAccount = (int) UserSteps.createUserAccount(firstUserRequest).getId();
        int receiverAccount = (int) UserSteps.createUserAccount(secondUserRequest).getId();

        double balanceBefore = ProfileSteps.userGetBalance(receiverAccount, secondUserRequest);

        TransferRequest transferRequest = TransferRequest.builder()
                .amount(RandomData.generateTransferAmount())
                .senderAccountId(senderAccount)
                .receiverAccountId(receiverAccount)
                .build();

        new CrudRequester(
                RequestSpecs.authAsUser(firstUserRequest.getUsername(), firstUserRequest.getPassword()),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsBadRequest(ResponseSpecs.ERROR_TRANSFER))
                .post(transferRequest);

        double balanceAfter = ProfileSteps.userGetBalance(receiverAccount, secondUserRequest);

        assertEquals(balanceBefore, balanceAfter);
    }

    @Test
    public void userCanTransferToAnotherUser() {
        CreateUserRequest firstUserRequest = AdminSteps.createUser();
        CreateUserRequest secondUserRequest = AdminSteps.createUser();
        int senderAccount = (int) UserSteps.createUserAccount(firstUserRequest).getId();
        int receiverAccount = (int) UserSteps.createUserAccount(secondUserRequest).getId();
        UserSteps.makeDepositEnoughForTransfer(firstUserRequest, senderAccount);

        double balanceBefore = ProfileSteps.userGetBalance(receiverAccount, secondUserRequest);

        TransferRequest transferRequest = TransferRequest.builder()
                .amount(RandomData.generateTransferAmount())
                .senderAccountId(senderAccount)
                .receiverAccountId(receiverAccount)
                .build();

        new CrudRequester(
                RequestSpecs.authAsUser(firstUserRequest.getUsername(), firstUserRequest.getPassword()),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsOK(ResponseSpecs.SUCCESS_TRANSFER))
                .post(transferRequest);

        double balanceAfter = ProfileSteps.userGetBalance(receiverAccount, secondUserRequest);

        assertTrue(balanceBefore < balanceAfter);
    }
}
