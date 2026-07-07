package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerResponse extends BaseModel {
    private int id;
    private String username;
    private String password;
    private String name;
    private String role;
    private List<AccountsResponse> accounts;
}
