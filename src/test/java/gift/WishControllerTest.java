package gift;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.auth.JwtProvider;
import gift.auth.LoginMemberArgumentResolver;
import gift.controller.WishController;
import gift.dto.LoginMemberDto;
import gift.dto.request.WishRequest;
import gift.dto.response.WishAddResponse;
import gift.dto.response.WishMsgResponse;
import gift.dto.response.WishResponse;
import gift.exception.WishNotFoundException;
import gift.service.MemberService;
import gift.service.WishService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WishController.class)
@AutoConfigureMockMvc(addFilters = true)
@Import(WishControllerTest.TestConfig.class)
public class WishControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WishService wishService;

    @MockitoBean
    private MemberService memberService;

    @MockitoBean
    private LoginMemberArgumentResolver loginMemberArgumentResolver;

    @MockitoBean
    private JwtProvider jwtProvider;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Object UNRESOLVED = new Object();

    @BeforeEach
    void setup() throws Exception {
        given(loginMemberArgumentResolver.supportsParameter(any())).willReturn(true);
        willAnswer(invocation -> {
            MethodParameter parameter = invocation.getArgument(0);
            if (parameter.getParameterType().equals(LoginMemberDto.class)) {
                return new LoginMemberDto(1L, "test@email.com");
            }
            return UNRESOLVED; // 또는 static import
        }).given(loginMemberArgumentResolver)
                .resolveArgument(any(), any(), any(), any());
    }

    @DisplayName("위시리스트 상품 추가에 성공한다")
    @Test
    void 위시리스트_추가_성공() throws Exception {

        String token = jwtProvider.createToken(1L, "user");

        given(wishService.add(any(Long.class), any(WishRequest.class)))
                .willReturn(new WishAddResponse(
                        "위시리스트에 추가되었습니다.",
                        new WishResponse(
                                1L,
                                1L,
                                "aaaaa",
                                15000,
                                "https://image.png"
                        )
                ));

        mockMvc.perform(post("/api/wishes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "productId": 1
                            }
                        """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/wishes/1"))
                .andExpect(jsonPath("$.message").value("위시리스트에 추가되었습니다."))
                .andExpect(jsonPath("$.wish.id").value(1))
                .andExpect(jsonPath("$.wish.productId").value(1))
                .andExpect(jsonPath("$.wish.productName").value("aaaaa"))
                .andExpect(jsonPath("$.wish.price").value(15000))
                .andExpect(jsonPath("$.wish.imageUrl").value("https://image.png"));
    }

    @DisplayName("위시리스트 상품 삭제에 성공한다")
    @Test
    void 위시리스트_삭제_성공() throws Exception {

        String token = jwtProvider.createToken(1L, "user");

        given(wishService.deleteByProductId(any(Long.class), any(Long.class)))
                .willReturn(new WishMsgResponse("위시리스트에서 삭제되었습니다."));

        mockMvc.perform(delete("/api/wishes/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("위시리스트에서 삭제되었습니다."));
    }

    @DisplayName("존재하지 않는 상품 ID로 삭제 시도 시 404 NotFound 에러가 발생한다")
    @Test
    void 위시리스트_삭제_실패_존재하지_않음() throws Exception {

        String token = jwtProvider.createToken(1L, "user");

        given(wishService.deleteByProductId(any(Long.class), any(Long.class)))
                .willThrow(new WishNotFoundException(2L));

        mockMvc.perform(delete("/api/wishes/2")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("위시리스트에 존재하지 않는 상품입니다."));
    }

    @DisplayName("위시리스트 페이지네이션 + 정렬 응답 성공")
    @Test
    void wishPaginationAndSort() throws Exception {
        String token = jwtProvider.createToken(1L, "user");

        List<WishResponse> content = List.of(
                new WishResponse(1L, 101L, "aaa", 30000, "img1"),
                new WishResponse(2L, 102L, "bbb", 10000, "img2")
        );

        PageImpl<WishResponse> page = new PageImpl<>(
                content,
                PageRequest.of(0, 2, Sort.by("product.price").descending()),
                4
        );

        given(wishService.getWishList(eq(1L), any(Pageable.class)))
                .willReturn(page);

        mockMvc.perform(get("/api/wishes")
                        .header("Authorization", "Bearer " + token)
                        .param("page", "0")
                        .param("size", "2")
                        .param("sort", "product.price,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].price").value(30000))
                .andExpect(jsonPath("$.content[1].price").value(10000))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.totalElements").value(4))
                .andExpect(jsonPath("$.hasNext").value(true))
                .andExpect(jsonPath("$.hasPrevious").value(false));
    }

    @TestConfiguration
    static class TestConfig implements WebMvcConfigurer {
        @Autowired
        private LoginMemberArgumentResolver loginMemberArgumentResolver;

        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
            resolvers.add(new PageableHandlerMethodArgumentResolver());
            resolvers.add(loginMemberArgumentResolver);
        }
    }
}
