package gift;

import gift.domain.Option;
import gift.domain.Order;
import gift.dto.request.OrderRequest;
import gift.dto.response.OrderResponse;
import gift.exception.OptionNotFoundException;
import gift.repository.OptionRepository;
import gift.repository.OrderRepository;
import gift.repository.WishRepository;
import gift.service.KakaoMessageService;
import gift.service.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    OptionRepository optionRepository;

    @Mock
    WishRepository wishRepository;

    @Mock
    OrderRepository orderRepository;

    @Mock
    KakaoMessageService kakaoMessageService;

    @InjectMocks
    OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("주문성공 저장과 메시지 전송")
    void 주문성공_저장과_메시지전송_호출() {
        Long memberId = 1L;
        Option option = mock(Option.class);
        when(optionRepository.findById(10L)).thenReturn(Optional.of(option));
        when(option.getProduct()).thenReturn(mock(gift.domain.Product.class));
        when(option.getProduct().getId()).thenReturn(100L);

        OrderRequest request = new OrderRequest(10L, 2, "메시지");
        Order savedOrder = new Order(memberId, option, 2, LocalDateTime.now(), "메시지");
        when(orderRepository.save(any())).thenReturn(savedOrder);

        OrderResponse response = orderService.createOrder(memberId, request);

        verify(optionRepository).findById(10L);
        verify(option).substract(2);
        verify(wishRepository).deleteByMember_IdAndProduct_Id(memberId, 100L);
        verify(orderRepository).save(any(Order.class));
        verify(kakaoMessageService).sendOrderMsg(memberId, savedOrder);

        assertThat(response.optionId()).isEqualTo(option.getId());
    }

    @Test
    @DisplayName("주문은 성공 메시지 전송은 실패")
    void 메시지전송실패_주문은_성공() {
        Long memberId = 1L;
        Option option = mock(Option.class);
        when(optionRepository.findById(10L)).thenReturn(Optional.of(option));
        when(option.getProduct()).thenReturn(mock(gift.domain.Product.class));
        when(option.getProduct().getId()).thenReturn(100L);

        OrderRequest request = new OrderRequest(10L, 1, "메시지");
        when(orderRepository.save(any())).thenReturn(
                new Order(memberId, option, 1, LocalDateTime.now(), "메시지")
        );

        doThrow(new RuntimeException("카카오 API 실패"))
                .when(kakaoMessageService).sendOrderMsg(any(), any());

        assertThatNoException().isThrownBy(() -> {
            orderService.createOrder(memberId, request);
        });

        verify(orderRepository).save(any(Order.class));
        verify(kakaoMessageService).sendOrderMsg(eq(memberId), any(Order.class));
    }

    @Test
    @DisplayName("존재하지 않는 옵션에 대한 주문 시 예외 발생")
    void 옵션이_존재하지_않으면_예외발생() {
        Long memberId = 1L;
        OrderRequest request = new OrderRequest(999L, 1, "메시지");
        when(optionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(memberId, request))
                .isInstanceOf(OptionNotFoundException.class);

        verify(orderRepository, never()).save(any());
        verify(kakaoMessageService, never()).sendOrderMsg(any(), any());
    }

    @Test
    @DisplayName("재고 부족 예외 발생")
    void 재고수량_부족하면_예외발생() {
        Long memberId = 1L;
        Option option = mock(Option.class);
        when(optionRepository.findById(10L)).thenReturn(Optional.of(option));
        when(option.getProduct()).thenReturn(mock(gift.domain.Product.class));
        when(option.getProduct().getId()).thenReturn(1L);

        OrderRequest request = new OrderRequest(10L, 9999, "메시지");

        doThrow(new IllegalStateException("재고 부족"))
                .when(option).substract(anyInt());

        assertThatThrownBy(() -> orderService.createOrder(memberId, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("재고");

        verify(orderRepository, never()).save(any());
        verify(kakaoMessageService, never()).sendOrderMsg(any(), any());
    }
}