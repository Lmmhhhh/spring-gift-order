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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OptionRepository optionRepository;
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

    @Transactional
    protected Order createOrderLogic(Long memberId, OrderRequest request) {
        Option option = optionRepository.findById(request.optionId())
                .orElseThrow(() -> new OptionNotFoundException(request.optionId()));

        option.substract(request.quantity());

        Order order = new Order(
                memberId,
                option,
                request.quantity(),
                LocalDateTime.now(),
                request.message()
        );
        return orderRepository.save(order);
    }

    @Override
    public OrderResponse createOrder(Long memberId, OrderRequest request) {
        Order order = createOrderLogic(memberId, request);

        try {
            wishRepository.deleteByMemberIdAndProductId(memberId, order.getOption().getProduct().getId());
        } catch (Exception e) {
            log.warn("[주문 후] 위시리스트 삭제 실패 - memberId={}, productId={}",
                    memberId, order.getOption().getProduct().getId(), e);
        }

        try {
            kakaoMessageService.sendOrderMsg(memberId, order);
        } catch (Exception e) {
            log.warn("[주문 후] 카카오 메시지 전송 실패 - memberId={}, orderId={}",
                    memberId, order.getId(), e);
        }

        return new OrderResponse(
                order.getId(),
                order.getOption().getId(),
                order.getQuantity(),
                order.getOrderDateTime(),
                order.getMessage()
        );
    }

}