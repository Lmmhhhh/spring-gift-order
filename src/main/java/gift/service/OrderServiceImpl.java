package gift.service;

import gift.domain.Option;
import gift.domain.Order;
import gift.dto.request.OrderRequest;
import gift.dto.response.OrderResponse;
import gift.exception.OptionNotFoundException;
import gift.repository.OptionRepository;
import gift.repository.OrderRepository;
import gift.repository.WishRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderServiceImpl implements OrderService {     // 재고 차감 재사용
    private final OptionRepository optionRepository;    // 옵션 엔티티 조회
    private final WishRepository wishRepository;
    private final OrderRepository orderRepository;
    private final KakaoMessageService kakaoMessageService;

    public OrderServiceImpl(
            OptionRepository optionRepository,
            WishRepository wishRepository,
            OrderRepository orderRepository,
            KakaoMessageService kakaoMessageService
    ) {
        this.optionRepository = optionRepository;
        this.wishRepository = wishRepository;
        this.orderRepository = orderRepository;
        this.kakaoMessageService = kakaoMessageService;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(Long memberId, OrderRequest request){

        Option option = optionRepository.findById(request.optionId())
                .orElseThrow(() -> new OptionNotFoundException(request.optionId()));

        option.substract(request.quantity());

        wishRepository.deleteByMember_IdAndProduct_Id(memberId, option.getProduct().getId());

        Order order = new Order(
                memberId,
                option,
                request.quantity(),
                LocalDateTime.now(),
                request.message()
        );
        orderRepository.save(order);

        return new OrderResponse(
                order.getId(),
                option.getId(),
                order.getQuantity(),
                order.getOrderDateTime(),
                order.getMessage()
        );
    }

}