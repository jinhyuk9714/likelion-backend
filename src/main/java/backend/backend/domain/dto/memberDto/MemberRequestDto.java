package backend.backend.domain.dto.memberDto;

import backend.backend.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberRequestDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
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
