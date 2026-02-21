package backend.backend.domain.dto.memberDto;

import lombok.Builder;

public class MemberRequestDto {

    @Builder
    public record SignUpDto (String email,
                             String password,
                             String nickName,
                             String emoji) {
    }
}
