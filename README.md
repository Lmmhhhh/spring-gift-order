# spring-gift-order

# [step1] 카카오 로그인

## 구현 기능 목록
- [x] 카카오 계정 로그인 통한 인증 코드 발급 
- [x] 엑세스 토큰 추출 
- [x] 테스트 코드 작성 

## step1 리뷰 반영 
- [x] 카카오 api 매핑 및 타임 아웃 적용 
  - application.properties
    - kakao.base-url: https://kauth.kakao.com
      kakao.connect-timeout-ms: 2000
      kakao.read-timeout-ms: 3000
- [x] KakaoOAuthClient 분리
- [ ] 단계별 로깅 