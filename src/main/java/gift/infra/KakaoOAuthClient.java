package gift.infra;

import gift.config.KakaoRestTemplateConfig;
import gift.dto.request.KakaoTokenRequest;
import gift.dto.response.KakaoTokenResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class KakaoOAuthClient {

    private final RestTemplate kakaoRestTemplate;

    public KakaoOAuthClient(@Qualifier("kakaoRestTemplate") RestTemplate kakaoRestTemplate) {
        this.kakaoRestTemplate = kakaoRestTemplate;
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
}