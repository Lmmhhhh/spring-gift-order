package gift.dto.request;

import gift.service.KakaoLoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class KakaoTokenRequest {

    private static final Logger log = LoggerFactory.getLogger(KakaoLoginService.class);

    private final String grant_type = "authorization_code";
    private final String client_id;
    private final String redirect_uri;
    private final String authorizationCode;
    private final String client_secret;

    public KakaoTokenRequest(String client_id, String redirect_uri, String authorizationCode, String client_secret) {
        this.client_id = client_id;
        this.redirect_uri = redirect_uri;
        this.authorizationCode = authorizationCode;
        this.client_secret = client_secret;
    }

    public MultiValueMap<String, String> toForm() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", grant_type);
        body.add("client_id", client_id);
        body.add("redirect_uri", redirect_uri);
        body.add("code", authorizationCode);
        if (client_secret != null) {
            body.add("client_secret", client_secret);
        }
        log.debug("KakaoTokenRequest redirect_uri={}", redirect_uri);
        return body;
    }
}
