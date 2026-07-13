package models;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class DepositRequest extends BaseModel {
    private int id;
    private double balance;
}
