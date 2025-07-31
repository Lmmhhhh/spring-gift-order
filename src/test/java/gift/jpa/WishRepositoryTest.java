package gift.jpa;

import gift.domain.Member;
import gift.domain.Option;
import gift.domain.Product;
import gift.domain.Wish;
import gift.repository.MemberRepository;
import gift.repository.ProductRepository;
import gift.repository.WishRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class WishRepositoryTest {

    @Autowired
    WishRepository wishRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ProductRepository productRepository;

    @Test
    @DisplayName("위시리스트 추가 테스트")
    void save(){
        Member member = memberRepository.save(new Member("abc@gmail.com", "1234"));
        Product product = productRepository.save(new Product("A", 1000, "a",List.of(new Option("화이트/S", 10))));

        Wish saved = wishRepository.save(new Wish(member, product));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getMemberId()).isEqualTo(member.id());
        assertThat(saved.getProductId()).isEqualTo(product.getId());
    }

    @Test
    @DisplayName("위시리스트 조회 테스트")
    void findByMember(){
        Member member = memberRepository.save(new Member("abc@gmail.com", "1234"));
        Product product1 = productRepository.save(new Product("A", 1000, "a",List.of(new Option("화이트/S", 10))));
        Product product2 = productRepository.save(new Product("B", 1000, "b",List.of(new Option("화이트/S", 10))));

       wishRepository.save(new Wish(member, product1));
       wishRepository.save(new Wish(member, product2));

        List<Wish> wishes = wishRepository.findByMember(member);
        assertThat(wishes).hasSize(2);
        assertThat(wishes)
                .extracting(Wish::getProductId)
                .containsExactlyInAnyOrder(product1.getId(), product2.getId());
    }

    @Test
    @DisplayName("위시리스트 삭제 테스트")
    void delete(){
        Member member = memberRepository.save(new Member("abc@gmail.com", "1234"));
        Product product = productRepository.save(new Product("삭제대상", 1500, "image-del",List.of(new Option("화이트/S", 10))));

        Wish wish = wishRepository.save(new Wish(member, product));
        Long wishId = wish.getId();

        wishRepository.deleteById(wishId);

        boolean exists = wishRepository.findById(wishId).isPresent();
        assertThat(exists).isFalse();
    }
}