package gift.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import gift.config.KakaoOAuthProperties;
import gift.dto.request.KakaoTokenRequest;
import gift.dto.response.KakaoErrorResponse;
import gift.dto.response.KakaoTokenResponse;
import gift.exception.KakaoApiException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;

import java.net.URI;

@Service
public class KakaoLoginService {

    private final KakaoOAuthProperties properties;
    private final RestTemplate restTemplate;

    public KakaoLoginService(KakaoOAuthProperties properties, RestTemplate restTemplate) {
        this.properties = properties;
        this.restTemplate = restTemplate;
    }

    public KakaoTokenResponse requestToken(String code) {
        String url = "https://kauth.kakao.com/oauth/token";

        KakaoTokenRequest kakaoTokenRequest = new KakaoTokenRequest(
                properties.getClientId(),
                properties.getRedirectUri(),
                code,
                properties.getClientSecret()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<?> request = new HttpEntity<>(kakaoTokenRequest.toForm(), headers);

        try {
            ResponseEntity<KakaoTokenResponse> response = restTemplate.exchange(
                    URI.create(url),
                    HttpMethod.POST,
                    request,
                    KakaoTokenResponse.class
            );

            KakaoTokenResponse token = response.getBody();
            if (token == null || token.access_token() == null) {
                throw new KakaoApiException(HttpStatus.BAD_REQUEST, "token이 응답에 포함되지 않았습니다.");
            }

            return token;

        } catch (HttpStatusCodeException e) {
            KakaoErrorResponse error = extractKakaoErrorResponse(e.getResponseBodyAsString());
            String msg = extractKakaoErrorMessage(error);
            HttpStatus status = HttpStatus.resolve(e.getRawStatusCode());
            throw new KakaoApiException(status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR, msg);

        } catch (RestClientException e) {
            throw new KakaoApiException(HttpStatus.INTERNAL_SERVER_ERROR, "카카오 API 통신 중 에러 발생");
        }
    }

    private KakaoErrorResponse extractKakaoErrorResponse(String responseBody) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(responseBody, KakaoErrorResponse.class);
        } catch (Exception e) {
            return new KakaoErrorResponse("parse_error", "카카오 응답 파싱 실패", null, null, null);
        }
    }

    private String extractKakaoErrorMessage(KakaoErrorResponse error) {
        if ("invalid_grant".equals(error.error())) {
            return "인가 코드가 유효하지 않거나 이미 사용되었습니다.";
        }
        if ("invalid_token".equals(error.error())) {
            return "토큰이 유효하지 않거나 만료되었습니다. 다시 로그인해주세요.";
        }
        if (error.code() != null) {
            return switch (error.code()) {
                case -402 -> "카카오 메시지 전송 권한이 비활성화되어 있습니다.";
                case -10 -> "카카오 API 호출 한도를 초과했습니다.";
                case -1 -> "카카오 내부 오류가 발생했습니다. 나중에 다시 시도해주세요.";
                default -> "카카오 API 오류가 발생했습니다.";
            };
        }
        return "카카오 API 오류가 발생했습니다.";
    }

}
