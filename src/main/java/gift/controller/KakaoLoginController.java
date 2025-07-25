package gift.controller;

import gift.service.KakaoLoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/login/oauth/kakao")
public class KakaoLoginController {

    private static final Logger log = LoggerFactory.getLogger(KakaoLoginController.class);
    private final KakaoLoginService kakaoLoginService;

    public KakaoLoginController(KakaoLoginService kakaoLoginService){
        this.kakaoLoginService = kakaoLoginService;
    }

    @GetMapping("/callback")
    public ResponseEntity<String> callback(@RequestParam("code") String code){
        log.info("인가 코드 수신: {}", code);
        kakaoLoginService.requestToken(code);
        return ResponseEntity.ok("카카오 로그인 완료");
    }

}
