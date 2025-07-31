package gift.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", length = 255 , unique = true, nullable = false)
    private String email;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "kakao_access_token", length = 2000)
    private String kakaoAccessToken;

    @Column(name = "kakao_refresh_token", length = 2000)
    private String kakaoRefreshToken;

    @Column(name = "kakao_expires_at")
    private LocalDateTime kakaoExpiresAt;

    protected  Member(){

    }

    public Member(String email, String pwd){
        this.email = email;
        this.password = pwd;
    }

    public Long id() { return id; }

    public String email() { return email; }

    public String password() { return password; }

    public String kakaoAccessToken(){ return kakaoAccessToken; }

    public String kakaoRefreshToken(){ return kakaoRefreshToken; }

    public LocalDateTime kakaoExpiresAt()  { return kakaoExpiresAt; }
}
