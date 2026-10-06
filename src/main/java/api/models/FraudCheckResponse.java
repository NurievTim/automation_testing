package api.models;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class FraudCheckResponse extends BaseModel {
    private String note;
    private String status;
    private int transactionId;
}
