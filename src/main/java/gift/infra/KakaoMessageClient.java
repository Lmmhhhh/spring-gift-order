package gift.infra;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class KakaoMessageClient {

    private final RestTemplate kakaoRestTemplate;

    private static final String DEFAULT_SEND_PATH = "https://kapi.kakao.com/v2/api/talk/memo/default/send";

    public KakaoMessageClient(@Qualifier("kakaoRestTemplate") RestTemplate kakaoRestTemplate) {
        this.kakaoRestTemplate = kakaoRestTemplate;
    }

    public void sendMsg(String accessToken,
                        String path,
                        MultiValueMap<String, String> form) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBearerAuth(accessToken);

        kakaoRestTemplate.postForLocation(
                DEFAULT_SEND_PATH,
                new HttpEntity<>(form, headers)
        );
    }
}
