package gift.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "wish")
public class Wish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_wish_member_id_ref_member_id"))
    private Member member;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_wish_product_id_ref_product_id"))
    private Product product;

    protected Wish(){

    }

    public Wish(Member member, Product product) {
        this.member  = member;
        this.product = product;
    }

    public Long getId() {return id;}

    public Long getMemberId() {return member.getId();}

    public Long getProductId() {return product.getId();}

    public Product getProduct(){
        return product;
    }
}
