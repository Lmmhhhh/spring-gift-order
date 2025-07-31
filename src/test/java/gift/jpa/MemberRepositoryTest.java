package gift.jpa;

import gift.domain.Member;
import gift.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원 가입 테스트")
    void save() {
        Member saved = memberRepository.save(new Member("abc@gmail.com", "1234"));

        assertThat(saved.id()).isNotNull();
        assertThat(saved.email()).isEqualTo("abc@gmail.com");
    }

    @Test
    @DisplayName("이메일로 회원 조회 테스트")
    void findByEmail() {
        memberRepository.save(new Member("abc@gmail.com", "1234"));
        Member found = memberRepository.findByEmail("abc@gmail.com").orElseThrow();

        assertThat(found.email()).isEqualTo("abc@gmail.com");
    }
}
