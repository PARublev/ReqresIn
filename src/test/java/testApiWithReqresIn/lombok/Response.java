package testApiWithReqresIn.lombok;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Response {
    private Integer page;
    private Integer perPage;
    private Integer total;
    private Integer totalPages;
    @JsonProperty("data")
    private List<UserData> userData = new ArrayList<>();
}
