package backend.backend.domain.dto.memberDto;

import backend.backend.domain.Member;
import lombok.Builder;

public class MemberRequestDto {

    @Builder
    public record SignUpDto (String email,
                             String password,
                             String nickName,
                             String emoji) {

        public Member toEntity() {
            return Member.builder()
                    .email(email)
                    .password(password)
                    .nickName(nickName)
                    .emoji(emoji)
                    .build();
        }
    }
}
