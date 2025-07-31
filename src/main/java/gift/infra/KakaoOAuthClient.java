package gift.infra;

import gift.config.KakaoOAuthProperties;
import gift.config.KakaoRestTemplateConfig;
import gift.dto.request.KakaoRefreshTokenRequest;
import gift.dto.request.KakaoTokenRequest;
import gift.dto.response.KakaoTokenResponse;
import gift.dto.response.KakaoUserResponse;
import gift.exception.KakaoApiException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class KakaoOAuthClient {

    private final RestTemplate kakaoRestTemplate;
    private final KakaoOAuthProperties props;
    private static final String USER_URL  = "https://kapi.kakao.com/v2/user/me";


    public KakaoOAuthClient(@Qualifier("kakaoRestTemplate") RestTemplate kakaoRestTemplate,KakaoOAuthProperties props) {
        this.kakaoRestTemplate = kakaoRestTemplate;
        this.props = props;
    }

    public KakaoTokenResponse requestToken(KakaoTokenRequest kakaoTokenRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        return kakaoRestTemplate.postForObject(
                KakaoRestTemplateConfig.TOKEN_PATH,
                new HttpEntity<>(kakaoTokenRequest.toForm(), headers),
                KakaoTokenResponse.class
        );
    }

    public KakaoUserResponse fetchUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        ResponseEntity<KakaoUserResponse> response =
                kakaoRestTemplate.exchange(
                        USER_URL,
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        KakaoUserResponse.class
                );

        if (response.getBody() == null) {
            throw new KakaoApiException((HttpStatus) response.getStatusCode(), "user null");
        }
        return response.getBody();
    }

    public KakaoTokenResponse refreshToken(String refreshToken) {
        KakaoRefreshTokenRequest request = new KakaoRefreshTokenRequest(
                props.clientId(),
                refreshToken,
                props.clientSecret()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        return kakaoRestTemplate.postForObject(
                KakaoRestTemplateConfig.TOKEN_PATH,
                new HttpEntity<>(request.toForm(), headers),
                KakaoTokenResponse.class
        );
    }

}