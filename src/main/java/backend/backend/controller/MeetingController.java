package backend.backend.controller;


import backend.backend.domain.dto.Response;
import backend.backend.domain.dto.meetingDto.MeetingRequestDTO;
import backend.backend.domain.dto.meetingDto.MeetingResponseDTO;
import backend.backend.domain.enums.MeetingCategory;
import backend.backend.service.MeetingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "모임 API", description = "모임 생성, 참여, 조회")
@RestController
@RequestMapping("/api/meeting")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @Operation(summary = "모임 목록 조회", description = "페이지네이션으로 모임 목록 조회 (카테고리 필터 가능)")
    @GetMapping
    public ResponseEntity<Response<Page<MeetingResponseDTO.getListDTO>>> getMeetingsList(
            @PageableDefault(page = 1) Pageable pageable,
            @RequestParam(required = false) MeetingCategory category) {
        Page<MeetingResponseDTO.getListDTO> meetings = meetingService.getList(pageable, category);
        return ResponseEntity.ok(Response.ok(meetings));
    }

    @Operation(summary = "모임 생성", description = "새 모임 생성 (제목, 카테고리, 요일, 시간, 인원, 설명)")
    @PostMapping
    public ResponseEntity<Response<Void>> postMeeting(@Valid @RequestBody MeetingRequestDTO.MeetingPostDto meetingPostDto) {
        meetingService.post(meetingPostDto);
        return ResponseEntity.ok(Response.ok("모임이 생성되었습니다."));
    }

    @Operation(summary = "모임 참여", description = "기존 모임에 참여 (중복/정원 초과 불가)")
    @PostMapping("/{meetingId}")
    public ResponseEntity<Response<Void>> joinMeeting(@PathVariable Long meetingId) {
        meetingService.joinMeeting(meetingId);
        return ResponseEntity.ok(Response.ok("모임에 참여했습니다."));
    }

    @Operation(summary = "모임 상세 조회", description = "모임 상세 정보 및 참여자 수 조회")
    @GetMapping("/{meetingId}")
    public ResponseEntity<Response<MeetingResponseDTO.getOneDTO>> getMeetingInfo(@PathVariable Long meetingId) {
        MeetingResponseDTO.getOneDTO responseDto = meetingService.getOne(meetingId);
        return ResponseEntity.ok(Response.ok(responseDto));
    }
}
