package gift.dto.response;

import gift.domain.Wish;

public record WishResponse(
        Long id,
        Long productId,
        String productName,
        int price,
        String imageUrl
) {
    public static WishResponse from(Wish wish) {
        return new WishResponse(
                wish.getId(),
                wish.getProductId(),
                wish.getProduct().getName(),
                wish.getProduct().getPrice(),
                wish.getProduct().getImageUrl()
        );
    }
}
