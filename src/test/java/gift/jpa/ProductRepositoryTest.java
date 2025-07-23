package gift.jpa;

import gift.domain.Option;
import gift.domain.Product;
import gift.dto.response.OptionResponse;
import gift.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("상품 등록 테스트")
    void save(){
        Product product = new Product("디퓨저", 19000,"https://image.com",
                List.of(new Option("화이트/S", 10)));

        Product saved = productRepository.save(product);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("디퓨저");
    }

    @Test
    @DisplayName("상품 전체 조회 테스트")
    void findAll(){
        productRepository.save(new Product("A", 1000, "a",List.of(new Option("화이트/S", 10))));
        productRepository.save(new Product("B", 2000, "b",List.of(new Option("화이트/S", 10))));

        var products = productRepository.findAll();

        assertThat(products).hasSize(2);
        assertThat(products)
                .extracting(Product::getName)
                .containsExactlyInAnyOrder("A","B");
    }

    @Test
    @DisplayName("상품명 키워드 포함 검색 테스트")
    void findByNameContaining(){
        productRepository.save(new Product("배민2만원상품권", 20000, "a",List.of(new Option("화이트/S", 10))));
        productRepository.save(new Product("배민5만원상품권", 50000, "a",List.of(new Option("화이트/S", 10))));
        productRepository.save(new Product("스타벅스2만원상품권", 20000, "b",List.of(new Option("화이트/S", 10))));

        Pageable pageable = Pageable.ofSize(10); // 기본 page=0, size=10
        Page<Product> result = productRepository.findByNameContaining("배민", pageable);

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent())
                .extracting(Product::getName)
                .containsExactlyInAnyOrder("배민2만원상품권", "배민5만원상품권");
    }

    @Test
    @DisplayName("상품 삭제 테스트")
    void delete() {
        Product product = productRepository.save(new Product("배민2만원상품권", 20000, "a",List.of(new Option("화이트/S", 10))));

        productRepository.delete(product);

        boolean exists = productRepository.existsById(product.getId());
        assertThat(exists).isFalse();
    }
}
