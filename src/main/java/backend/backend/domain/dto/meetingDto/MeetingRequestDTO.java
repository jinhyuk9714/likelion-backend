package backend.backend.domain.dto.meetingDto;

import backend.backend.domain.Meeting;
import backend.backend.domain.enums.MeetingCategory;
import backend.backend.domain.enums.Week;
import jakarta.annotation.Nullable;
import lombok.Builder;

import java.time.LocalTime;

public class MeetingRequestDTO {

    @Builder
    public record MeetingGetDto (@Nullable
                                 MeetingCategory category){

    }

    @Builder
    public record MeetingPostDto (String title,
                                  MeetingCategory category,
                                  Week week,
                                  LocalTime time,
                                  int limitNumberOfPeople,
                                  String description){
        public Meeting toEntity(){
            return Meeting.builder()
                    .title(this.title)
                    .category(this.category)
                    .week(this.week)
                    .time(this.time)
                    .limitNumberOfPeople(this.limitNumberOfPeople)
                    .description(this.description)
                    .build();
        }
    }
}
