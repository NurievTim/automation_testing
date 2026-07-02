package models;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class TransferResponse extends BaseModel {
    private String message;
    private double amount;
    private int receiverAccountId;
    private int senderAccountId;
}
