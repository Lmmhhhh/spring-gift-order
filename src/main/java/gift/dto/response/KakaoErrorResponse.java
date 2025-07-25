package gift.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoErrorResponse(
        String error,

        @JsonProperty("error_description")
        String errorDescription,

        @JsonProperty("error_code")
        String errorCode,

        String msg,
        Integer code
) {}
