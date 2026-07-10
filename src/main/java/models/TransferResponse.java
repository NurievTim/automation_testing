package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransferResponse extends BaseModel {
    private String message;
    private double amount;
    private int receiverAccountId;
    private int senderAccountId;
}
