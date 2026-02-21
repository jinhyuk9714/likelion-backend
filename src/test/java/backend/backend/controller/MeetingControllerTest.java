package backend.backend.controller;

import backend.backend.domain.dto.meetingDto.MeetingResponseDTO;
import backend.backend.domain.enums.MeetingCategory;
import backend.backend.domain.enums.Week;
import backend.backend.global.util.security.SecurityUtil;
import backend.backend.service.MeetingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class MeetingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MeetingService meetingService;

    @Test
    @DisplayName("모임 목록 조회 성공")
    void getMeetingsList_success() throws Exception {
        MeetingResponseDTO.getListDTO dto = new MeetingResponseDTO.getListDTO(
                1L, "스터디 모임", MeetingCategory.Study, Week.Mon,
                LocalTime.of(10, 0), 10, 3);
        Page<MeetingResponseDTO.getListDTO> page = new PageImpl<>(List.of(dto));

        when(meetingService.getList(any(Pageable.class), isNull())).thenReturn(page);

        mockMvc.perform(get("/api/meeting").param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].title").value("스터디 모임"));
    }

    @Test
    @DisplayName("모임 생성 성공")
    void postMeeting_success() throws Exception {
        try (MockedStatic<SecurityUtil> util = mockStatic(SecurityUtil.class)) {
            util.when(SecurityUtil::getLoginEmail).thenReturn("test@test.com");
            doNothing().when(meetingService).post(any());

            String body = objectMapper.writeValueAsString(
                    new java.util.LinkedHashMap<>() {{
                        put("title", "스터디 모임");
                        put("category", "Study");
                        put("week", "Mon");
                        put("time", "10:00");
                        put("limitNumberOfPeople", 10);
                        put("description", "설명");
                    }});

            mockMvc.perform(post("/api/meeting")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk());
        }
    }

    @Test
    @DisplayName("모임 참여 성공")
    void joinMeeting_success() throws Exception {
        try (MockedStatic<SecurityUtil> util = mockStatic(SecurityUtil.class)) {
            util.when(SecurityUtil::getLoginEmail).thenReturn("test@test.com");
            doNothing().when(meetingService).joinMeeting(1L);

            mockMvc.perform(post("/api/meeting/1"))
                    .andExpect(status().isOk());
        }
    }

    @Test
    @DisplayName("모임 상세 조회 성공")
    void getMeetingInfo_success() throws Exception {
        MeetingResponseDTO.getOneDTO dto = MeetingResponseDTO.getOneDTO.builder()
                .title("스터디 모임")
                .category(MeetingCategory.Study)
                .week(Week.Mon)
                .time(LocalTime.of(10, 0))
                .limitNumberOfPeople(10)
                .numberOfParticipants(3)
                .description("설명")
                .ownerId(1L)
                .ownerName("테스터")
                .build();

        when(meetingService.getOne(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/meeting/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("스터디 모임"))
                .andExpect(jsonPath("$.data.ownerName").value("테스터"));
    }
}
