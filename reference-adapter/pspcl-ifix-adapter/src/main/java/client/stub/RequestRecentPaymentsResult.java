package client.stub;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class RequestRecentPaymentsResult {
    @JsonProperty("userName")
    private String userName;
    @JsonProperty("responseStatus")
    private String responseStatus;
    @JsonProperty("responseData")
    private PaymentsResultData[] responseData;
}