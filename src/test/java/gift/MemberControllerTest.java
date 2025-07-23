package gift;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.auth.JwtProvider;
import gift.config.WebConfig;
import gift.controller.MemberController;
import gift.dto.request.MemberRequest;
import gift.dto.response.MemberResponse;
import gift.exception.DuplicateMemberException;
import gift.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
@Import(MemberControllerTest.TestConfig.class)
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberService memberService;

    @MockitoBean
    private JwtProvider jwtProvider;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @DisplayName("유효한 이메일과 비밀번호로 회원가입에 성공한다")
    @Test
    void 회원가입_성공() throws Exception{

        given(memberService.register(any(MemberRequest.class)))
                .willReturn(new MemberResponse("mock.jwt.token"));

        mockMvc.perform(
                post("/api/members/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "email": "test@email.com",
                                "pwd": "abc1234"
                            }
                        """)
            ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("mock.jwt.token"));
    }

    @DisplayName("이미 가입된 이메일로 회원가입 시도 시 409CONFLICT 에러가 발생한다")
    @Test
    void 회원가입_중복_실패() throws Exception{
        given(memberService.register(any(MemberRequest.class)))
                .willThrow(new DuplicateMemberException());

        mockMvc.perform(post("/api/members/register")
                        .contentType(MediaType.APPLICATION_JSON)
                .content("""
                     {
                        "email": "dup@email.com",
                        "pwd": "abc1234"
                     }
                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("이미 존재하는 이메일입니다."));
    }

    @DisplayName("로그인 성공 시 토큰을 반환한다")
    @Test
    void 로그인_성공() throws Exception{
        MemberRequest request = new MemberRequest("test@email.com", "abc1234");
        when(memberService.login(request))
                .thenReturn(new MemberResponse("fake.jwt.token"));

        mockMvc.perform(post("/api/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake.jwt.token"));
    }

    @DisplayName("잘못된 비밀번호로 로그인 시 403Forbidden 에러가 발생한다")
    @Test
    void 로그인_실패_비밀번호_불일치() throws Exception {
        MemberRequest request = new MemberRequest("test@email.com", "wrong");
        when(memberService.login(request))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "비밀번호가 일치하지 않습니다."));

        mockMvc.perform(post("/api/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("비밀번호가 일치하지 않습니다."));

    }

    @DisplayName("존재하지 않는 계정으로 로그인 시 403Forbidden 에러가 발생한다")
    @Test
    void 로그인_실패_계정없음() throws Exception {
        MemberRequest request = new MemberRequest("nonexistent@email.com", "abc1234");

        when(memberService.login(request))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "회원이 존재하지 않습니다."));

        mockMvc.perform(post("/api/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("회원이 존재하지 않습니다."));
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public JwtProvider jwtProvider() {
            return mock(JwtProvider.class);
        }

        @Bean
        public MemberService memberService() {
            return mock(MemberService.class);
        }

        @Bean
        public WebConfig webConfig(JwtProvider jwtProvider, MemberService memberService) {
            return new WebConfig(jwtProvider, memberService);
        }
    }
}
