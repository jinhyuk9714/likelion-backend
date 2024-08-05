package backend.backend.domain.dto.memberDto;

import lombok.Builder;

public class MemberResponseDto {

    @Builder
    public record InfoDto(String email,
                          String nickName,
                          String emoji) {
    }
}
