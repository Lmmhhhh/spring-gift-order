package gift.repository;

import gift.domain.Option;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionRepository extends JpaRepository<Option,Long> {
    Page<Option> findAllByProductId(Long productId, Pageable pageable);
}
