package gift.service;

import gift.dto.request.OrderRequest;
import gift.dto.response.OrderResponse;

import java.time.LocalDateTime;

public class OrderServiceImpl implements OrderService {

    @Override
    public OrderResponse createOrder(Long memberId, OrderRequest request){
        return new OrderResponse(
                1L,
                request.optionId(),
                request.quantity(),
                LocalDateTime.now(),
                request.message()
        );
    }

}