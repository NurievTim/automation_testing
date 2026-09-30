package api.models;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class DepositRequest extends BaseModel {
    private long accountId;
    private double amount;
}
