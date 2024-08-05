package backend.backend.domain.dto.commentDto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CommentRequestDto {
    @NotNull
    private String content;
}
