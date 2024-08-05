package backend.backend.domain.dto.meetingDto;

import backend.backend.domain.enums.MeetingCategory;
import backend.backend.domain.enums.Week;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

public class MeetingResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public record getListDTO (String title,
                                     MeetingCategory category,
                                     Week week,
                                     LocalTime time,
                                     int limitNumberOfPeople,
                                     int numberOfParticipants){
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public record getOneDTO(String title,
                                   MeetingCategory category,
                                   Week week,
                                   LocalTime time,
                                   int limitNumberOfPeople,
                                   int numberOfParticipants,
                                   String description,
                                   Long ownerId,
                                   String ownerName) {
    }
}
