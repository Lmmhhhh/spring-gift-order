package gift.controller;

import gift.auth.LoginMember;
import gift.dto.LoginMemberDto;
import gift.dto.request.OrderRequest;
import gift.dto.response.OrderResponse;
import gift.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request,
                                                     @LoginMember LoginMemberDto loginMember){
        OrderResponse response = orderService.createOrder(loginMember.id(), request);
        return ResponseEntity.status(201).body(response);
    }
}
