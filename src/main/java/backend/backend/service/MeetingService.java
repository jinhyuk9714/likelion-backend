package backend.backend.service;


import backend.backend.domain.Meeting;
import backend.backend.domain.Member;
import backend.backend.domain.dto.meetingDto.MeetingRequestDTO;
import backend.backend.domain.dto.meetingDto.MeetingResponseDTO;
import backend.backend.domain.enums.MeetingCategory;
import backend.backend.domain.common.BusinessException;
import backend.backend.domain.common.ResponseCode;
import backend.backend.global.util.security.SecurityUtil;
import backend.backend.repository.MeetingRepository;
import backend.backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingService {

    private final MemberRepository memberRepository;
    private final MeetingRepository meetingRepository;

    public Page<MeetingResponseDTO.getListDTO> getList(Pageable pageable, MeetingCategory category) {
        int page = pageable.getPageNumber() - 1; // page 위치에 있는 값은 0부터 시작한다.
        int pageLimit = 3; // 한페이지에 보여줄 글 개수
        Page<Meeting> postsPages;
        if(category != null) {
            postsPages = meetingRepository.findAllByCategory(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")), category);
        }
        else {
            postsPages = meetingRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));
        }

        // 목록 : id, title, content, author
        Page<MeetingResponseDTO.getListDTO> meetingResponseDto = postsPages.map(
                postPage -> new MeetingResponseDTO.getListDTO(postPage.getId(),
                        postPage.getTitle(),
                        postPage.getCategory(),
                        postPage.getWeek(),
                        postPage.getTime(),
                        postPage.getLimitNumberOfPeople(),
                        postPage.getMembers().size()));
        return meetingResponseDto;
    }

    @Transactional
    public void post(MeetingRequestDTO.MeetingPostDto meetingPostDto) {
        Member owner = memberRepository.findByEmail(SecurityUtil.getLoginEmail())
                .orElseThrow(() -> new BusinessException(ResponseCode.MTG_AUTHENTICATION_FAIL));

        Meeting meeting = Meeting.builder()
                .title(meetingPostDto.title())
                .category(meetingPostDto.category())
                .week(meetingPostDto.week())
                .time(meetingPostDto.time())
                .limitNumberOfPeople(meetingPostDto.limitNumberOfPeople())
                .description(meetingPostDto.description())
                .owner(owner)
                .build();

        meetingRepository.save(meeting);
    }

    @Transactional
    public void joinMeeting(Long meetingId) {

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new BusinessException(ResponseCode.MTG_NOT_FOUND));
        Member member = memberRepository.findByEmail(SecurityUtil.getLoginEmail())
                .orElseThrow(() -> new BusinessException(ResponseCode.MTG_AUTHENTICATION_FAIL));

        meeting.join(member);
    }

    public MeetingResponseDTO.getOneDTO getOne(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new BusinessException(ResponseCode.MTG_NOT_FOUND));
        return MeetingResponseDTO.getOneDTO.builder()
                .title(meeting.getTitle())
                .week(meeting.getWeek())
                .time(meeting.getTime())
                .limitNumberOfPeople(meeting.getLimitNumberOfPeople())
                .numberOfParticipants(meeting.getMembers().size())
                .description(meeting.getDescription())
                .ownerId(meeting.getOwner().getId())
                .ownerName(meeting.getOwner().getNickName())
                .build();
    }
}

