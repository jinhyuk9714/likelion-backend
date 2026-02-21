package backend.backend.domain.dto.meetingDto;

import backend.backend.domain.enums.MeetingCategory;
import backend.backend.domain.enums.Week;
import lombok.Builder;

import java.time.LocalTime;

public class MeetingResponseDTO {

    @Builder
    public record getListDTO (Long id,
                                     String title,
                                     MeetingCategory category,
                                     Week week,
                                     LocalTime time,
                                     int limitNumberOfPeople,
                                     int numberOfParticipants){
    }

    @Builder
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
