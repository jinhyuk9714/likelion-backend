package backend.backend.controller;


import backend.backend.domain.dto.meetingDto.MeetingRequestDTO;
import backend.backend.domain.dto.meetingDto.MeetingResponseDTO;
import backend.backend.domain.enums.MeetingCategory;
import backend.backend.service.MeetingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/meeting")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @GetMapping
    public ResponseEntity getMeetingsList (@PageableDefault(page = 1) Pageable pageable, @RequestParam(required = false) MeetingCategory category) {

        Page<MeetingResponseDTO.getListDTO> meetings = meetingService.getList(pageable, category);
        return new ResponseEntity(meetings, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity postMeeting (@RequestBody MeetingRequestDTO.MeetingPostDto meetingPostDto) {
        meetingService.post(meetingPostDto);

        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/{meetingId}")
    public ResponseEntity joinMeeting (@Valid @PathVariable Long meetingId) {

        meetingService.joinMeeting(meetingId);


        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/{meetingId}")
    public ResponseEntity getMeetingInfo (@Valid @PathVariable Long meetingId) {

        MeetingResponseDTO.getOneDTO responseDto = meetingService.getOne(meetingId);

        return new ResponseEntity(responseDto, HttpStatus.OK);
    }
}
