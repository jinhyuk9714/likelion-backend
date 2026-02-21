package backend.backend.domain.dto.meetingDto;

import backend.backend.domain.enums.MeetingCategory;
import backend.backend.domain.enums.Week;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalTime;

public class MeetingRequestDTO {

    @Builder
    public record MeetingGetDto (@Nullable
                                 MeetingCategory category){
    }

    @Builder
    public record MeetingPostDto (
            @NotBlank String title,
            @NotNull MeetingCategory category,
            @NotNull Week week,
            @NotNull LocalTime time,
            @Min(2) @Max(100) int limitNumberOfPeople,
            String description){
    }
}
