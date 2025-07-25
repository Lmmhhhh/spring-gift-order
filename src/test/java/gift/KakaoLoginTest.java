package gift;

import gift.config.KakaoOAuthProperties;
import gift.dto.response.KakaoTokenResponse;
import gift.exception.KakaoApiException;
import gift.service.KakaoLoginService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(KakaoOAuthProperties.class)
public class KakaoLoginTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KakaoLoginService kakaoLoginService;

    @Test
    @DisplayName("카카오 로그인 성공 시 200 OK 응답을 반환한다")
    void 카카오_로그인_성공() throws Exception {
        String code = "valid-code";
        KakaoTokenResponse response = new KakaoTokenResponse(
                "bearer", "access-token", 3600,
                "refresh-token", 999999, "account_email"
        );

        when(kakaoLoginService.requestToken(code)).thenReturn(response);

        mockMvc.perform(get("/login/oauth/kakao/callback")
                        .param("code", code))
                .andExpect(status().isOk())
                .andExpect(content().string("카카오 로그인 완료"));
    }

    @Test
    @DisplayName("유효하지 않은 인가 코드로 요청 시 400 에러 반환")
    void 유효하지_않은_인가코드() throws Exception {
        String code = "invalid";
        when(kakaoLoginService.requestToken(code))
                .thenThrow(new KakaoApiException(HttpStatus.BAD_REQUEST, "인가 코드가 유효하지 않거나 이미 사용되었습니다."));

        mockMvc.perform(get("/login/oauth/kakao/callback")
                        .param("code", code))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("인가 코드가 유효하지 않거나 이미 사용되었습니다."));
    }
}
