package gift.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserResponse(
        @JsonProperty("id") Long id
) {}