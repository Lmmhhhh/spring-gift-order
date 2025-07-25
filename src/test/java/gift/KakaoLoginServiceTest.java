package gift;

import gift.config.KakaoOAuthProperties;
import gift.dto.response.KakaoTokenResponse;
import gift.exception.KakaoApiException;
import gift.service.KakaoLoginService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KakaoLoginServiceTest {

    @Mock
    KakaoOAuthProperties properties;

    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    KakaoLoginService kakaoLoginService;

    @Test
    @DisplayName("토큰 추출 및 응답 성공")
    void 성공적으로_토큰을_받으면_파싱된_응답을_리턴한다() {
        String code = "code";
        KakaoTokenResponse response = new KakaoTokenResponse(
                "bearer", "access-token", 3600,
                "refresh-token", 999999, "account_email"
        );

        when(properties.getClientId()).thenReturn("test-client-id");
        when(properties.getRedirectUri()).thenReturn("http://localhost:8080");
        when(properties.getClientSecret()).thenReturn("secret");

        when(restTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(KakaoTokenResponse.class)
        )).thenReturn(ResponseEntity.ok(response));

        KakaoTokenResponse result = kakaoLoginService.requestToken(code);

        assertThat(result.access_token()).isEqualTo("access-token");
        assertThat(result.token_type()).isEqualTo("bearer");
    }

    @Test
    @DisplayName("인가코드가 유효하지 않으면 400에러가 발생한다")
    void 인가코드가_잘못되면_에러가_발생한다() {
        // given
        String code = "invalid-code";
        String errorJson = """
                {
                  "error": "invalid_grant",
                  "error_description": "Invalid authorization code"
                }
                """;

        when(properties.getClientId()).thenReturn("test-client-id");
        when(properties.getRedirectUri()).thenReturn("http://localhost:8080");
        when(properties.getClientSecret()).thenReturn("secret");

        when(restTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(KakaoTokenResponse.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request", errorJson.getBytes(), null));

        // when & then
        assertThatThrownBy(() -> kakaoLoginService.requestToken(code))
                .isInstanceOf(KakaoApiException.class)
                .hasMessageContaining("인가 코드가 유효하지 않거나 이미 사용되었습니다.");
    }
}