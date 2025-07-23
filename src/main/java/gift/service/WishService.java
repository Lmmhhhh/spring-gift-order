package gift.service;

import gift.dto.request.WishRequest;
import gift.dto.response.WishAddResponse;
import gift.dto.response.WishMsgResponse;
import gift.dto.response.WishResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WishService {
    WishAddResponse add(Long memberId, WishRequest wishRequest);
    Page<WishResponse> getWishList(Long memberId, Pageable pageable);
    WishMsgResponse deleteByProductId(Long memberId, Long productId);
}
