package gift.exception;

public class ProductOptionEmptyException extends RuntimeException {
    public ProductOptionEmptyException() {
        super("상품에는 하나 이상의 옵션이 필요합니다.");
    }
}
