package gift.service;

import gift.domain.Member;
import gift.domain.Product;
import gift.domain.Wish;
import gift.dto.request.WishRequest;
import gift.dto.response.WishAddResponse;
import gift.dto.response.WishMsgResponse;
import gift.dto.response.WishResponse;
import gift.exception.DuplicateWishException;
import gift.exception.ProductNotFoundException;
import gift.exception.WishNotFoundException;
import gift.repository.MemberRepository;
import gift.repository.ProductRepository;
import gift.repository.WishRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class WishServiceImpl implements WishService {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final WishRepository wishRepository;

    public WishServiceImpl(MemberRepository memberRepository,
                           ProductRepository productRepository,
                           WishRepository wishRepository){
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
        this.wishRepository = wishRepository;
    }

    @Override
    public WishAddResponse add(Long memberId, WishRequest request) {

        Member  member  = memberRepository.getReferenceById(memberId);

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ProductNotFoundException(request.productId()));

        wishRepository.findByMemberAndProduct(member, product).ifPresent(wish -> {
            throw new DuplicateWishException();
        });

        Wish savedWish = wishRepository.save(new Wish(member, product));

        WishResponse wishResponse = new WishResponse(
                savedWish.getId(),
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getImageUrl()
        );

        return new WishAddResponse("위시리스트에 추가되었습니다.", wishResponse);
    }

    @Override
    public Page<WishResponse> getWishList(Long memberId, Pageable pageable) {

        return wishRepository.findByMember_Id(memberId, pageable)
                .map(WishResponse::from);
    }


    @Override
    public WishMsgResponse deleteByProductId(Long memberId, Long productId) {

        Member  member  = memberRepository.getReferenceById(memberId);
        Product product = productRepository.getReferenceById(productId);

        Wish wish = wishRepository.findByMemberAndProduct(member, product)
                .orElseThrow(() -> new WishNotFoundException(productId));

        wishRepository.deleteById(wish.getId());

        return new WishMsgResponse("위시리스트에서 삭제되었습니다.");
    }
}
