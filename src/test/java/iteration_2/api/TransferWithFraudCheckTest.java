package iteration_2.api;

import api.generators.RandomData;
import api.models.Accounts;
import api.models.CreateUserRequest;
import api.models.TransferResponse;
import api.models.TransferStatus;
import api.requests.steps.AdminSteps;
import api.requests.steps.UserSteps;
import common.annotations.FraudCheckMock;
import common.extensions.FraudCheckWireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static api.constans.FraudReasons.LOW_RISK_REASON;
import static api.constans.FraudReasons.LOW_RISK_SCORE;
import static api.constans.TransferMessages.APPROVED_MSG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@ExtendWith(FraudCheckWireMockExtension.class)
public class TransferWithFraudCheckTest extends BaseTest {
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
                .ignoringFields("transactionId")
                .isEqualTo(expectedResponse);

        assertThat(balanceAfter.getBalance())
                .isCloseTo(balanceBefore.getBalance() + transferAmount, within(0.001));
    }
}
