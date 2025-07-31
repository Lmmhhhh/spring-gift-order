package gift.service;

import gift.auth.JwtProvider;
import gift.config.KakaoOAuthProperties;
import gift.domain.AuthorizationCode;
import gift.domain.Member;
import gift.dto.request.KakaoTokenRequest;
import gift.dto.response.KakaoTokenResponse;
import gift.dto.response.KakaoUserResponse;
import gift.exception.KakaoApiException;
import gift.exception.MemberNotFoundException;
import gift.infra.KakaoOAuthClient;
import gift.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDateTime;

@Service
public class KakaoLoginService {

    private static final Logger log = LoggerFactory.getLogger(KakaoLoginService.class);

    private final KakaoOAuthClient client;
    private final KakaoOAuthProperties props;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    public KakaoLoginService(KakaoOAuthClient client,
                             KakaoOAuthProperties props,
                             MemberRepository memberRepository,
                             JwtProvider jwtProvider) {
        this.client = client;
        this.props = props;
        this.memberRepository = memberRepository;
        this.jwtProvider = jwtProvider;
    }


    public KakaoTokenResponse issueToken(AuthorizationCode code) {

        log.info("카카오 토큰 요청 시작  - code: {}", code.mask());

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
            log.error("카카오 API 에러 - code: {}, error: {}", code.mask(), exception.getMessage());
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

    @Transactional
    public String loginWithKakao(String codeRaw) {

        AuthorizationCode code = AuthorizationCode.of(codeRaw);
        KakaoTokenResponse token = issueToken(code);

        KakaoUserResponse user = client.fetchUserInfo(token.accessToken());
        Long kakaoId = user.id();

        Member member = memberRepository.findByKakaoId(kakaoId)
                .orElseGet(() -> memberRepository.save(new Member(kakaoId)));


        member.updateKakaoAccessToken(
                token.accessToken(),
                token.refreshToken(),
                token.expiresAt()
        );

        log.info("[카카오 로그인] memberId={} 토큰 저장 완료", member.id());
        return jwtProvider.createToken(member.id(), member.email());
    }
}