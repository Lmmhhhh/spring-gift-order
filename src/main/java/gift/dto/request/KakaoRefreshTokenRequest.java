package gift.dto.request;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class KakaoRefreshTokenRequest {

    private final String clientId;
    private final String refreshToken;
    private final String clientSecret;

    public KakaoRefreshTokenRequest(String clientId, String refreshToken, String clientSecret) {
        this.clientId = clientId;
        this.refreshToken = refreshToken;
        this.clientSecret = clientSecret;
    }

    public MultiValueMap<String, String> toForm() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", clientId);
        body.add("refresh_token", refreshToken);
        if (clientSecret != null) {
            body.add("client_secret", clientSecret);
        }
        return body;
    }
}
