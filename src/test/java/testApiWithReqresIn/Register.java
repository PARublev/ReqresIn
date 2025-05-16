package testApiWithReqresIn;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Register {
    private String email;
    private String password;

}
