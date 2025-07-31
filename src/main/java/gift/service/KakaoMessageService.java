package gift.service;

import gift.domain.Member;
import gift.domain.Order;
import gift.dto.request.KakaoMessageRequest;
import gift.exception.KakaoApiException;
import gift.exception.MemberNotFoundException;
import gift.infra.KakaoMessageClient;
import gift.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class KakaoMessageService {
    private static final Logger log = LoggerFactory.getLogger(KakaoMessageService.class);
    private static final String DEFAULT_MSG_PATH = "/v1/api/talk/memo/default/send";

    private final MemberRepository memberRepository;
    private final KakaoMessageClient kakaoMessageClient;

    public KakaoMessageService(MemberRepository memberRepository,
                               KakaoMessageClient kakaoMessageClient) {
        this.memberRepository = memberRepository;
        this.kakaoMessageClient = kakaoMessageClient;
    }


    public void sendOrderMsg(Long memberId, Order order) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException());

        String accessToken = member.kakaoAccessToken();


        if (accessToken == null) {
            throw new KakaoApiException(HttpStatus.UNAUTHORIZED, "카카오 연동이 필요합니다.");
        }

        KakaoMessageRequest request = KakaoMessageRequest.of(order);
        kakaoMessageClient.sendMsg(accessToken, DEFAULT_MSG_PATH, request.toForm());
        log.info("[Kakao] 메시지 전송 완료 member={}", memberId);
    }
}
