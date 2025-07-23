package gift.exception;

public class DuplicateWishException extends RuntimeException {
    public DuplicateWishException() {
        super("이미 위시리스트에 존재하는 상품입니다.");
    }
}
