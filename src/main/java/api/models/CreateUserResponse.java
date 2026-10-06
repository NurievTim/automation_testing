package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateUserResponse extends BaseModel {
    private long id;
    private String username;
    private String name;
    private String role;
    // приходит только в GET /admin/users, в ответе на создание пользователя поля нет
    private List<Accounts> accounts;
}
