package gift.service;

import gift.dto.request.OrderRequest;
import gift.dto.response.OrderResponse;

public interface OrderService {
    OrderResponse createOrder(Long memerId, OrderRequest request);
}
