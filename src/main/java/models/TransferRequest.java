package models;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class TransferRequest extends BaseModel {
    private double amount;
    private int senderAccountId;
    private int receiverAccountId;
}
