package gift.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.handler.KakaoResponseErrorHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class KakaoRestTemplateConfig {

    public static final String TOKEN_PATH = "/oauth/token";

    @Bean
    @Qualifier("kakaoRestTemplate")
    public RestTemplate kakaoRestTemplate(RestTemplateBuilder builder,
                                          ObjectMapper mapper,
                                          KakaoOAuthProperties props) {

        return builder
                .rootUri(props.baseUrl())
                .requestFactory(() -> {
                    var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
                    factory.setConnectTimeout((int) props.connectTimeoutMs());
                    factory.setReadTimeout((int) props.readTimeoutMs());
                    return factory;
                })
                .errorHandler(new KakaoResponseErrorHandler(mapper))
                .build();
    }
}