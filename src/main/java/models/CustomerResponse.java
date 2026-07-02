package models;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class CustomerResponse extends BaseModel {
    private int id;
    private String username;
    private String name;
}
