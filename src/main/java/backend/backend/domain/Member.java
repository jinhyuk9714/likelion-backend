package backend.backend.domain;

import backend.backend.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import static jakarta.persistence.GenerationType.IDENTITY;

@Table(name = "MEMBER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "member_id")
    private Long id; //primary Key

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String name;

    private String nickName;

    private String emoji;

    @Column(length = 1000)
    private String refreshToken;//RefreshToken


    //== 정보 수정 ==//

    public void updateRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }
    public void destroyRefreshToken(){
        this.refreshToken = null;
    }

    //== 패스워드 암호화 ==//
    public void encodePassword(PasswordEncoder passwordEncoder){
        this.password = passwordEncoder.encode(password);
    }
}
