package backend.backend.service;

import backend.backend.domain.Member;
import backend.backend.domain.common.BusinessException;
import backend.backend.domain.common.ResponseCode;
import backend.backend.domain.dto.memberDto.MemberRequestDto;
import backend.backend.domain.dto.memberDto.MemberResponseDto;
import backend.backend.global.util.security.SecurityUtil;
import backend.backend.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Member createMember() {
        return Member.builder()
                .email("test@test.com")
                .password("password")
                .nickName("테스터")
                .emoji("😀")
                .build();
    }

    @Test
    @DisplayName("회원가입 성공")
    void signUp_success() {
        MemberRequestDto.SignUpDto dto = new MemberRequestDto.SignUpDto(
                "test@test.com", "password", "테스터", "😀");

        when(memberRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenReturn(createMember());

        memberService.signUp(dto);

        verify(memberRepository).save(any(Member.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 중복 이메일")
    void signUp_duplicateEmail() {
        MemberRequestDto.SignUpDto dto = new MemberRequestDto.SignUpDto(
                "test@test.com", "password", "테스터", "😀");

        when(memberRepository.findByEmail("test@test.com")).thenReturn(Optional.of(createMember()));

        assertThatThrownBy(() -> memberService.signUp(dto))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ResponseCode.MBR_ALREADY_EXISTS));
    }

    @Test
    @DisplayName("내 정보 조회 성공")
    void getMyInfo_success() {
        Member member = createMember();

        try (MockedStatic<SecurityUtil> util = mockStatic(SecurityUtil.class)) {
            util.when(SecurityUtil::getLoginEmail).thenReturn("test@test.com");
            when(memberRepository.findByEmail("test@test.com")).thenReturn(Optional.of(member));

            MemberResponseDto.InfoDto info = memberService.getMyInfo();

            assertThat(info.email()).isEqualTo("test@test.com");
            assertThat(info.nickName()).isEqualTo("테스터");
        }
    }

    @Test
    @DisplayName("내 정보 조회 실패 - 회원 없음")
    void getMyInfo_notFound() {
        try (MockedStatic<SecurityUtil> util = mockStatic(SecurityUtil.class)) {
            util.when(SecurityUtil::getLoginEmail).thenReturn("none@test.com");
            when(memberRepository.findByEmail("none@test.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> memberService.getMyInfo())
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(ResponseCode.MBR_NOT_FOUND));
        }
    }
}
