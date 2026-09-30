package iteration_2.api;

import api.generators.RandomData;
import api.models.Accounts;
import api.models.CreateUserRequest;
import api.models.TransferResponse;
import api.models.TransferResponse.Fields;
import api.models.TransferStatus;
import api.requests.steps.AdminSteps;
import api.requests.steps.UserSteps;
import common.annotations.FraudCheckMock;
import common.extensions.FraudCheckWireMockExtension;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static api.constans.FraudReasons.*;
import static api.constans.TransferMessages.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@Execution(ExecutionMode.SAME_THREAD)
public class TransferWithFraudCheckTest extends BaseTest {
    @RegisterExtension
    static FraudCheckWireMockExtension fraudCheckMock = new FraudCheckWireMockExtension();

    @Test
    @FraudCheckMock()
    public void userCanTransferWithFraudCheck() {
        CreateUserRequest userRequest = AdminSteps.createUser();
        int senderAccount = (int) UserSteps.createUserAccount(userRequest).getId();
        int receiverAccount = (int) UserSteps.createUserAccount(userRequest).getId();
        UserSteps.makeDepositEnoughForTransfer(userRequest, senderAccount);

        Accounts balanceBefore = UserSteps.userGetAccountById(receiverAccount, userRequest);
        int transferAmount = RandomData.generateDepositAmount();

        TransferResponse response = UserSteps.userTransferWithFraudCheck(
                senderAccount,
                receiverAccount,
                transferAmount,
                userRequest
        );
        Accounts balanceAfter = UserSteps.userGetAccountById(receiverAccount, userRequest);

        TransferResponse expectedResponse = TransferResponse.builder()
                .fraudReason(LOW_RISK_REASON)
                .senderAccountId(senderAccount)
                .status(TransferStatus.APPROVED)
                .amount(transferAmount)
                .message(APPROVED_MSG)
                .requiresVerification(false)
                .receiverAccountId(receiverAccount)
                .fraudRiskScore(LOW_RISK_SCORE)
                .requiresManualReview(false)
                .build();

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields(Fields.transactionId)
                .isEqualTo(expectedResponse);

        assertThat(balanceAfter.getBalance())
                .isCloseTo(balanceBefore.getBalance() + transferAmount, within(0.001));
    }

    private void runTransferFraudValidation(TransferStatus expectedStatus, String expectedMessage) {
        runTransferFraudValidation(expectedStatus, expectedMessage, LOW_RISK_REASON, LOW_RISK_SCORE, false);
    }

    private TransferResponse runTransferFraudValidation(TransferStatus expectedStatus,
                                                        String expectedMessage,
                                                        String expectedReason,
                                                        double expectedRiskScore,
                                                        boolean expectedManualReview) {
        CreateUserRequest userRequest = AdminSteps.createUser();
        int senderAccount = (int) UserSteps.createUserAccount(userRequest).getId();
        int receiverAccount = (int) UserSteps.createUserAccount(userRequest).getId();
        UserSteps.makeDepositEnoughForTransfer(userRequest, senderAccount);

        Accounts balanceBefore = UserSteps.userGetAccountById(receiverAccount, userRequest);
        int transferAmount = RandomData.generateDepositAmount();

        TransferResponse response = UserSteps.userTransferWithFraudCheck(
                senderAccount,
                receiverAccount,
                transferAmount,
                userRequest
        );
        Accounts balanceAfter = UserSteps.userGetAccountById(receiverAccount, userRequest);

        TransferResponse expectedResponse = TransferResponse.builder()
                .fraudReason(expectedReason)
                .senderAccountId(senderAccount)
                .status(expectedStatus)
                .amount(transferAmount)
                .message(expectedMessage)
                .requiresVerification(false)
                .receiverAccountId(receiverAccount)
                .fraudRiskScore(expectedRiskScore)
                .requiresManualReview(expectedManualReview)
                .build();

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields(expectedReason == null
                        ? new String[]{Fields.transactionId, Fields.fraudReason}
                        : new String[]{Fields.transactionId})
                .isEqualTo(expectedResponse);

        assertThat(balanceAfter.getBalance())
                .isEqualTo(balanceBefore.getBalance());

        return response;
    }

    @Test
    @FraudCheckMock(decision = FraudCheckMock.Decision.BLOCKED)
    public void userCannotTransferBlocked() {
        runTransferFraudValidation(TransferStatus.BLOCKED, BLOCKED_TRANSFER);
    }

    @Test
    @FraudCheckMock(decision = FraudCheckMock.Decision.REVIEW_REQUIRED)
    public void userCannotTransferReviewRequired() {
        runTransferFraudValidation(TransferStatus.MANUAL_REVIEW_REQUIRED, MANUAL_REVIEW_MSG);
    }

    @Test
    @FraudCheckMock(decision = FraudCheckMock.Decision.VERIFICATION_REQUIRED)
    public void userCannotTransferVerificationRequired() {
        runTransferFraudValidation(TransferStatus.VERIFICATION_REQUIRED, VERIFICATION_REQUIRED);
    }

    @Test
    @FraudCheckMock(httpStatus = HttpStatus.SC_UNAUTHORIZED)
    public void userCannotTransferUnauthorized() {
        TransferResponse response = runTransferFraudValidation(
                TransferStatus.MANUAL_REVIEW_REQUIRED,
                MANUAL_REVIEW_MSG,
                null,
                FALLBACK_RISK_SCORE,
                true
        );

        assertThat(response.getFraudReason())
                .startsWith(FRAUD_CHECK_ERROR_REASON_PREFIX)
                .contains(String.valueOf(HttpStatus.SC_UNAUTHORIZED));

        fraudCheckMock.verifyFraudCheckCalled();
    }
}
