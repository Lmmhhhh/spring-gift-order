package gift.service;

import gift.dto.request.OptionRequest;
import gift.dto.response.OptionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OptionService {
    Page<OptionResponse> getOptions(Long productId, Pageable pageable);
    void subtractQuantity(Long optionId, int quantityToUse);
}
