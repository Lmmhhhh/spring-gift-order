package gift.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private Option option;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "message")
    private String message;

    private LocalDateTime orderDateTime;

    private Order(){

    }

    public Order(Long memberId,
                 Option option,
                 int quantity,
                 LocalDateTime orderDateTime,
                 String message) {
        this.memberId = memberId;
        this.option = option;
        this.quantity = quantity;
        this.orderDateTime = orderDateTime;
        this.message = message;
    }

    public Long getId() { return id; }

    public Long getMemberId() { return memberId; }

    public Option getOption() { return option;}

    public int getQuantity()  { return quantity;}

    public LocalDateTime getOrderDateTime() { return orderDateTime;}

    public String getMessage() { return message;}
}
