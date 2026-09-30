package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionDto extends BaseModel {
    private long id;
    private double amount;
    private TransactionType type;
    private String timestamp;
    private Long relatedAccountId;
}
