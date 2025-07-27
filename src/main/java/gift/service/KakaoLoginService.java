package gift.service;

import gift.config.KakaoOAuthProperties;
import gift.dto.request.KakaoTokenRequest;
import gift.dto.response.KakaoTokenResponse;
import gift.infra.KakaoOAuthClient;
import org.springframework.stereotype.Service;

@Service
public class KakaoLoginService {

    private final KakaoOAuthClient client;
    private final KakaoOAuthProperties props;

    public KakaoLoginService(KakaoOAuthClient client, KakaoOAuthProperties props) {
        this.client = client;
        this.props = props;
    }

    public KakaoTokenResponse issueToken(String authCode) {

        KakaoTokenRequest req = new KakaoTokenRequest(
                props.clientId(),
                props.redirectUri(),
                authCode,
                props.clientSecret()
        );
        KakaoTokenResponse res = client.requestToken(req);

        return res;
    }
}