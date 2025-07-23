package gift.dto.response;

public record WishAddResponse(
        String message,
        WishResponse wish
) {}