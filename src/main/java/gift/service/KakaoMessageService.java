package gift.service;

import gift.domain.Member;
import gift.domain.Order;
import gift.dto.request.KakaoMessageRequest;
import gift.dto.response.KakaoTokenResponse;
import gift.exception.KakaoApiException;
import gift.exception.MemberNotFoundException;
import gift.infra.KakaoMessageClient;
import gift.infra.KakaoOAuthClient;
import gift.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class KakaoMessageService {
    private static final Logger log = LoggerFactory.getLogger(KakaoMessageService.class);
    private static final String DEFAULT_MSG_PATH = "/v1/api/talk/memo/default/send";

    private final MemberRepository memberRepository;
    private final KakaoMessageClient kakaoMessageClient;
    private final KakaoOAuthClient kakaoOAuthClient;

    public KakaoMessageService(MemberRepository memberRepository,
                               KakaoMessageClient kakaoMessageClient,
                               KakaoOAuthClient kakaoOAuthClient) {
        this.memberRepository = memberRepository;
        this.kakaoMessageClient = kakaoMessageClient;
        this.kakaoOAuthClient = kakaoOAuthClient;
    }


    public void sendOrderMsg(Long memberId, Order order) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException());

        if (member.kakaoExpiresIn() != null &&
                member.kakaoExpiresIn().isBefore(LocalDateTime.now())) {

            KakaoTokenResponse refreshed = kakaoOAuthClient.refreshToken(member.kakaoRefreshToken());
            member.updateKakaoAccessToken(
                    refreshed.accessToken(),
                    refreshed.refreshToken(),
                    refreshed.expiresAt()
            );

            log.info("[Kakao] access token 갱신 완료 - memberId={}", member.id());
        }

        String accessToken = member.kakaoAccessToken();
        if (accessToken == null) {
            throw new KakaoApiException(HttpStatus.UNAUTHORIZED, "카카오 연동이 필요합니다.");
        }

        KakaoMessageRequest request = KakaoMessageRequest.of(order);
        kakaoMessageClient.sendMsg(accessToken, DEFAULT_MSG_PATH, request.toForm());
        log.info("[Kakao] 메시지 전송 완료 member={}", memberId);
    }
}
