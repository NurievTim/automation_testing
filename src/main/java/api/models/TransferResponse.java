package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransferResponse extends BaseModel {
    private double fraudRiskScore;
    private int receiverAccountId;
    private boolean requiresVerification;
    private String message;
    private double amount;
    private TransferStatus status;
    private int senderAccountId;
    private String fraudReason;
    private int transactionId;
    private boolean requiresManualReview;
}
