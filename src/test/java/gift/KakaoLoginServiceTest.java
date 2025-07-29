package gift;

import gift.config.KakaoOAuthProperties;
import gift.domain.AuthorizationCode;
import gift.dto.response.KakaoTokenResponse;
import gift.exception.KakaoApiException;
import gift.infra.KakaoOAuthClient;
import gift.service.KakaoLoginService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;


import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class KakaoLoginServiceTest {

    @MockitoBean
    KakaoOAuthClient kakaoClient;

    @MockitoBean
    KakaoOAuthProperties props;

    @Autowired
    KakaoLoginService kakaoLoginService;

    @Test
    @DisplayName("정상 응답이면 토큰 DTO 반환")
    void 토큰추출_성공() {
        when(props.clientId()).thenReturn("id");
        when(props.redirectUri()).thenReturn("uri");
        when(props.clientSecret()).thenReturn("sec");

        KakaoTokenResponse kakaoRes = new KakaoTokenResponse(
                "bearer", "access", 3600, "refresh", 999999, "account_email"
        );
        when(kakaoClient.requestToken(any())).thenReturn(kakaoRes);

        KakaoTokenResponse result =
                kakaoLoginService.issueToken(AuthorizationCode.of("abcd1234"));

        assertThat(result.accessToken()).isEqualTo("access");
    }

    @Test
    @DisplayName("invalid_grant 발생 시 KakaoApiException")
    void 인증실패() {
        when(kakaoClient.requestToken(any()))
                .thenThrow(new KakaoApiException(HttpStatus.BAD_REQUEST,
                        "인가 코드가 유효하지 않거나 만료되었습니다."));

        assertThatThrownBy(() ->
                kakaoLoginService.issueToken(AuthorizationCode.of("bad")))
                .isInstanceOf(KakaoApiException.class)
                .hasMessageContaining("인가 코드가 유효");
    }
}