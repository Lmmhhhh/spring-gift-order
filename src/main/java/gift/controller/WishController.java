package gift.controller;

import gift.auth.LoginMember;
import gift.dto.LoginMemberDto;
import gift.dto.request.WishRequest;
import gift.dto.response.PageResponse;
import gift.dto.response.WishAddResponse;
import gift.dto.response.WishMsgResponse;
import gift.dto.response.WishResponse;
import gift.service.WishService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/wishes")
public class WishController {

    private final WishService wishService;

    public WishController(WishService wishService){
        this.wishService = wishService;
    }

    @PostMapping
    public ResponseEntity<WishAddResponse> add(@RequestBody WishRequest request,
                                               @LoginMember LoginMemberDto loginMember){
        WishAddResponse response = wishService.add(loginMember.id(),request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{productId}")
                .buildAndExpand(request.productId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public  ResponseEntity<PageResponse<WishResponse>> getWishList(@LoginMember LoginMemberDto loginMember,
                                                                   Pageable pageable) {
        Page<WishResponse> page = wishService.getWishList(loginMember.id(), pageable);
        return ResponseEntity.ok(PageResponse.from(page));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<WishMsgResponse> delete(@PathVariable Long productId,
                                                  @LoginMember LoginMemberDto loginMember) {
        WishMsgResponse response = wishService.deleteByProductId(loginMember.id(), productId);
        return ResponseEntity.ok(response);
    }
}
