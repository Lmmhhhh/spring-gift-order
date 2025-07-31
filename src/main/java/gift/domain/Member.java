package gift.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", length = 255 , unique = true, nullable = true)
    private String email;

    @Column(name = "password", length = 255, nullable = true)
    private String password;

    @Column(name = "kakao_id")
    private Long kakaoId;

    private String kakaoAccessToken;
    private String kakaoRefreshToken;
    private LocalDateTime kakaoExpiresIn;

    protected  Member(){

    }

    public Member(Long kakaoId) {
        this.kakaoId = kakaoId;
    }

    public Member(String email, String pwd){
        this.email = email;
        this.password = pwd;
    }

    public void updateKakaoAccessToken(String accessToken, String refreshToken, LocalDateTime expiresIn) {
        this.kakaoAccessToken = accessToken;
        this.kakaoRefreshToken = refreshToken;
        this.kakaoExpiresIn = expiresIn;
    }
    public Long id() { return id; }

    public String email() { return email; }

    public String password() { return password; }

    public String kakaoAccessToken(){ return kakaoAccessToken; }

    public String kakaoRefreshToken(){ return kakaoRefreshToken; }

    public LocalDateTime kakaoExpiresIn()  { return kakaoExpiresIn; }
}
