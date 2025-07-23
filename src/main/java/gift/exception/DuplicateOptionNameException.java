package gift.exception;

public class DuplicateOptionNameException extends RuntimeException {
    public DuplicateOptionNameException(String name) {
        super("중복된 옵션 이름이 존재합니다: " + name);
    }
}
