package backend.backend.domain.dto.memberDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public class MemberRequestDto {

    @Builder
    public record SignUpDto (
            @NotBlank @Email String email,
            @NotBlank @Size(min = 4) String password,
            @NotBlank String nickName,
            String emoji) {
    }
}
