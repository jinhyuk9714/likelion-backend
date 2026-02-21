package backend.backend.service;

import backend.backend.domain.Meeting;
import backend.backend.domain.Member;
import backend.backend.domain.common.BusinessException;
import backend.backend.domain.common.ResponseCode;
import backend.backend.domain.dto.meetingDto.MeetingRequestDTO;
import backend.backend.domain.dto.meetingDto.MeetingResponseDTO;
import backend.backend.domain.enums.MeetingCategory;
import backend.backend.domain.enums.Week;
import backend.backend.global.util.security.SecurityUtil;
import backend.backend.repository.MeetingRepository;
import backend.backend.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeetingServiceTest {

    @InjectMocks
    private MeetingService meetingService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MeetingRepository meetingRepository;

    private Member createMember() {
        return Member.builder()
                .id(1L)
                .email("test@test.com")
                .password("password")
                .nickName("테스터")
                .emoji("😀")
                .build();
    }

    private Meeting createMeeting(Member owner) {
        return Meeting.builder()
                .id(1L)
                .title("스터디 모임")
                .category(MeetingCategory.Study)
                .week(Week.Mon)
                .time(LocalTime.of(10, 0))
                .limitNumberOfPeople(10)
                .description("설명")
                .owner(owner)
                .build();
    }

    @Test
    @DisplayName("모임 목록 조회 성공 - 전체")
    void getList_all() {
        Member owner = createMember();
        Meeting meeting = createMeeting(owner);
        Page<Meeting> meetingPage = new PageImpl<>(List.of(meeting));

        when(meetingRepository.findAll(any(Pageable.class))).thenReturn(meetingPage);

        Page<MeetingResponseDTO.getListDTO> result = meetingService.getList(PageRequest.of(1, 10), null);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).title()).isEqualTo("스터디 모임");
    }

    @Test
    @DisplayName("모임 목록 조회 성공 - 카테고리 필터")
    void getList_categoryFilter() {
        Member owner = createMember();
        Meeting meeting = createMeeting(owner);
        Page<Meeting> meetingPage = new PageImpl<>(List.of(meeting));

        when(meetingRepository.findAllByCategory(any(PageRequest.class), eq(MeetingCategory.Study)))
                .thenReturn(meetingPage);

        Page<MeetingResponseDTO.getListDTO> result = meetingService.getList(
                PageRequest.of(1, 10), MeetingCategory.Study);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).category()).isEqualTo(MeetingCategory.Study);
    }

    @Test
    @DisplayName("모임 생성 성공")
    void post_success() {
        Member owner = createMember();
        MeetingRequestDTO.MeetingPostDto dto = new MeetingRequestDTO.MeetingPostDto(
                "스터디 모임", MeetingCategory.Study, Week.Mon,
                LocalTime.of(10, 0), 10, "설명");

        try (MockedStatic<SecurityUtil> util = mockStatic(SecurityUtil.class)) {
            util.when(SecurityUtil::getLoginEmail).thenReturn("test@test.com");
            when(memberRepository.findByEmail("test@test.com")).thenReturn(Optional.of(owner));

            meetingService.post(dto);

            verify(meetingRepository).save(any(Meeting.class));
        }
    }

    @Test
    @DisplayName("모임 참여 성공")
    void joinMeeting_success() {
        Member owner = createMember();
        Meeting meeting = createMeeting(owner);
        Member joiner = Member.builder()
                .id(2L)
                .email("joiner@test.com")
                .password("password")
                .nickName("참여자")
                .emoji("🎉")
                .build();

        try (MockedStatic<SecurityUtil> util = mockStatic(SecurityUtil.class)) {
            util.when(SecurityUtil::getLoginEmail).thenReturn("joiner@test.com");
            when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting));
            when(memberRepository.findByEmail("joiner@test.com")).thenReturn(Optional.of(joiner));

            meetingService.joinMeeting(1L);

            assertThat(meeting.getMembers()).hasSize(1);
        }
    }

    @Test
    @DisplayName("모임 참여 실패 - 모임 없음")
    void joinMeeting_notFound() {
        try (MockedStatic<SecurityUtil> util = mockStatic(SecurityUtil.class)) {
            util.when(SecurityUtil::getLoginEmail).thenReturn("test@test.com");
            when(meetingRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> meetingService.joinMeeting(99L))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(ResponseCode.MTG_NOT_FOUND));
        }
    }

    @Test
    @DisplayName("모임 상세 조회 성공")
    void getOne_success() {
        Member owner = createMember();
        Meeting meeting = createMeeting(owner);

        when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting));

        MeetingResponseDTO.getOneDTO result = meetingService.getOne(1L);

        assertThat(result.title()).isEqualTo("스터디 모임");
        assertThat(result.ownerName()).isEqualTo("테스터");
        assertThat(result.ownerId()).isEqualTo(1L);
        assertThat(result.limitNumberOfPeople()).isEqualTo(10);
    }
}
