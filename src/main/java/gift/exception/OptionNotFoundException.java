package gift.exception;

public class OptionNotFoundException extends RuntimeException {
  public OptionNotFoundException(Long optionId) {
    super("해당 옵션을 찾을 수 없습니다. optionId=" + optionId);
  }
}
