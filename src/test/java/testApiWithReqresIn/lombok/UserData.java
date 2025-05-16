package testApiWithReqresIn.lombok;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserData {
    private Integer id;
    private String email;
    private String first_name;
    private String last_name;
    private String avatar;
    private String password;


    @Override
    public String toString() {
        return "Пользователь: " +
                "ID=" + id +
                ", Имя='" + first_name + '\'' +
                ", Фамилия='" + last_name + '\'' +
                ", Email='" + email + '\'' +
                ", Аватар='" + avatar + '\'';
    }
}
