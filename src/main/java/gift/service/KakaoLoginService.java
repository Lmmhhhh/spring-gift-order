package gift.service;

import gift.config.KakaoOAuthProperties;
import gift.domain.AuthorizationCode;
import gift.dto.request.KakaoTokenRequest;
import gift.dto.response.KakaoTokenResponse;
import gift.exception.KakaoApiException;
import gift.infra.KakaoOAuthClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

@Service
public class KakaoLoginService {

    private static final Logger log = LoggerFactory.getLogger(KakaoLoginService.class);

    private final KakaoOAuthClient client;
    private final KakaoOAuthProperties props;

    public KakaoLoginService(KakaoOAuthClient client, KakaoOAuthProperties props) {
        this.client = client;
        this.props = props;
    }

    public KakaoTokenResponse issueToken(AuthorizationCode code) {

        String masked = code.mask();
        log.info("카카오 토큰 요청 시작  - code: {}", masked);

        KakaoTokenRequest req = new KakaoTokenRequest(
                props.clientId(),
                props.redirectUri(),
                code.raw(),
                props.clientSecret()
        );

        try {
            KakaoTokenResponse res = client.requestToken(req);
            log.info("카카오 토큰 요청 성공 - tokenType: {}", res.tokenType());
            return res;

        } catch (KakaoApiException exception) {
            log.error("카카오 API 에러 - code: {}, error: {}", masked, exception.getMessage());
            throw exception;
        } catch (ResourceAccessException exception) {
            log.error("카카오 API 네트워크 에러", exception);
            throw new KakaoApiException(HttpStatus.SERVICE_UNAVAILABLE,
                    "카카오 서비스 연결에 실패했습니다.");
        } catch (RuntimeException exception) {
            log.error("카카오 API 예상치 못한 에러", exception);
            throw new KakaoApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "카카오 API 처리 중 오류가 발생했습니다.");
        }
    }
}