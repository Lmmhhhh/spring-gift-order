package gift.domain;

import jakarta.persistence.*;

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

    protected  Member(){

    }

    public Member(String email, String pwd){
        this.email = email;
        this.password = pwd;
    }

    public Long getId() { return id; }

    public String getEmail() { return email; }

    public String getPassword() { return password; }
}
