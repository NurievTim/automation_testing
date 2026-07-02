package models;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class DepositResponse extends BaseModel{
    private int id;
    private String accountNumber;
    private String balance;

}
