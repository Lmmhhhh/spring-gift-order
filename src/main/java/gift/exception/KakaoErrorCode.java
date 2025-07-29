package gift.exception;

import org.springframework.http.HttpStatus;

public enum KakaoErrorCode {

    INVALID_CLIENT("invalid_client", HttpStatus.UNAUTHORIZED, "잘못된 앱 키 또는 client_secret 입니다."),
    MISCONFIGURED("misconfigured", HttpStatus.BAD_REQUEST, "등록되지 않은 플랫폼 요청입니다."),
    UNSUPPORTED_GRANT("unsupported_grant_type", HttpStatus.BAD_REQUEST, "지원하지 않는 grant_type 입니다."),
    INVALID_REQUEST("invalid_request", HttpStatus.BAD_REQUEST, "요청 파라미터가 잘못되었습니다."),
    INVALID_GRANT("invalid_grant", HttpStatus.BAD_REQUEST, "인가 코드가 유효하지 않거나 만료되었습니다."),
    INVALID_TOKEN("invalid_token", HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않거나 만료되었습니다."),

    MSG_SCOPE_DISABLED("-402", HttpStatus.FORBIDDEN, "카카오 메시지 전송 권한이 비활성화되어 있습니다."),
    RATE_LIMIT_EXCEEDED("-10", HttpStatus.TOO_MANY_REQUESTS, "카카오 API 호출 한도를 초과했습니다."),
    INTERNAL_ERROR("-1", HttpStatus.BAD_GATEWAY, "카카오 내부 오류가 발생했습니다."),

    UNKNOWN("unknown", HttpStatus.BAD_GATEWAY, "카카오 API 오류가 발생했습니다.");

    private final String kakaoRes;
    private final HttpStatus httpStatus;
    private final String userMessage;

    KakaoErrorCode(String kakaoValue, HttpStatus httpStatus, String userMessage) {
        this.kakaoRes = kakaoValue;
        this.httpStatus = httpStatus;
        this.userMessage = userMessage;
    }

    public HttpStatus httpStatus() {
        return httpStatus;
    }

    public String userMessage() {
        return userMessage;
    }

    public static KakaoErrorCode from(gift.dto.response.KakaoErrorResponse err) {
        if (err.error() != null) {
            for (KakaoErrorCode c : values()) {
                if (c.kakaoRes.equals(err.error())) return c;
            }
        }
        if (err.code() != null) {
            for (KakaoErrorCode c : values()) {
                if (c.kakaoRes.equals(String.valueOf(err.code()))) return c;
            }
        }
        return UNKNOWN;
    }
}
