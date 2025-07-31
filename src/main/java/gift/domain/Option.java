package gift.domain;

import gift.exception.InvalidOptionNameException;
import gift.exception.NotEnoughStockException;
import jakarta.persistence.*;

import java.security.PublicKey;

@Entity
@Table(name = "product_option")
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name ="option_name", length = 50, nullable = false)
    private String name;

    @Column(name ="quantity", nullable = false)
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "product_id",nullable = false,
            foreignKey = @ForeignKey(name = "fk_option_product_id_ref_product_id"))
    private Product product;

    protected Option(

    ){}

    public static Option of(Product product, String name, int quantity) {
        Option option = new Option(name, quantity);
        option.product = product;
        return option;
    }

    public Option(String name, int quantity){
        this.name = name;
        this.quantity = quantity;
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidOptionNameException("옵션 이름은 필수입니다.");
        }
        if (name.length() > 50) {
            throw new InvalidOptionNameException("옵션 이름은 50자 이하만 가능합니다.");
        }
        if (!name.matches("^[a-zA-Z0-9가-힣 ()\\[\\]\\+\\-\\&/_]*$")) {
            throw new InvalidOptionNameException("허용되지 않은 특수문자가 포함되어 있습니다.");
        }
    }

    private void validateQuantity(int quantity) {
        if (quantity < 1 || quantity >= 100_000_000) {
            throw new IllegalArgumentException("수량은 1 이상 1억 미만이어야 합니다.");
        }
    }

    public void substract(int amount){
        if (quantity < amount){
            throw new NotEnoughStockException();
        }
        this.quantity -= amount;
    }

    public Long getId() {return id;}

    public String getName() {return name;}

    public int getQuantity() {return quantity;}

    public Product getProduct() {return product;}

}
