package gift.exception;

public class UnauthorizedException extends RuntimeException {
  public UnauthorizedException() {super("인증 정보가 유효하지 않습니다.");
  }
}
