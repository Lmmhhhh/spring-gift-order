package gift.repository;

import gift.domain.Member;
import gift.domain.Product;
import gift.domain.Wish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishRepository extends JpaRepository<Wish, Long> {

    List<Wish> findByMember(Member member);

    Optional<Wish> findByMemberAndProduct(Member member, Product product);

    Page<Wish> findByMemberId(Long memberId, Pageable pageable);

    void deleteByMemberIdAndProductId(Long memberId, Long productId);
}
