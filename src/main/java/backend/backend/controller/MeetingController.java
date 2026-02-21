package backend.backend.controller;


import backend.backend.domain.dto.Response;
import backend.backend.domain.dto.meetingDto.MeetingRequestDTO;
import backend.backend.domain.dto.meetingDto.MeetingResponseDTO;
import backend.backend.domain.enums.MeetingCategory;
import backend.backend.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/meeting")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @GetMapping
    public ResponseEntity<Response<Page<MeetingResponseDTO.getListDTO>>> getMeetingsList(
            @PageableDefault(page = 1) Pageable pageable,
            @RequestParam(required = false) MeetingCategory category) {
        Page<MeetingResponseDTO.getListDTO> meetings = meetingService.getList(pageable, category);
        return ResponseEntity.ok(Response.ok(meetings));
    }

    @PostMapping
    public ResponseEntity<Response<Void>> postMeeting(@RequestBody MeetingRequestDTO.MeetingPostDto meetingPostDto) {
        meetingService.post(meetingPostDto);
        return ResponseEntity.ok(Response.ok("모임이 생성되었습니다."));
    }

    @PostMapping("/{meetingId}")
    public ResponseEntity<Response<Void>> joinMeeting(@PathVariable Long meetingId) {
        meetingService.joinMeeting(meetingId);
        return ResponseEntity.ok(Response.ok("모임에 참여했습니다."));
    }

    @GetMapping("/{meetingId}")
    public ResponseEntity<Response<MeetingResponseDTO.getOneDTO>> getMeetingInfo(@PathVariable Long meetingId) {
        MeetingResponseDTO.getOneDTO responseDto = meetingService.getOne(meetingId);
        return ResponseEntity.ok(Response.ok(responseDto));
    }
}
