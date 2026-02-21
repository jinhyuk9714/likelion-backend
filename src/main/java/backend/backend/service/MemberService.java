package backend.backend.service;

import backend.backend.domain.Member;
import backend.backend.domain.common.BusinessException;
import backend.backend.domain.common.ResponseCode;
import backend.backend.domain.dto.memberDto.MemberRequestDto;
import backend.backend.domain.dto.memberDto.MemberResponseDto;
import backend.backend.global.util.security.SecurityUtil;
import backend.backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signUp(MemberRequestDto.SignUpDto memberSignUpDto) {
        Member member = Member.builder()
                .email(memberSignUpDto.email())
                .password(memberSignUpDto.password())
                .nickName(memberSignUpDto.nickName())
                .emoji(memberSignUpDto.emoji())
                .build();
        member.encodePassword(passwordEncoder);

        if(memberRepository.findByEmail(memberSignUpDto.email()).isPresent()){
            throw new BusinessException(ResponseCode.MBR_ALREADY_EXISTS);
        }

        memberRepository.save(member);
    }

    public MemberResponseDto.InfoDto getMyInfo() {
        Member member = memberRepository.findByEmail(SecurityUtil.getLoginEmail())
                .orElseThrow(() -> new BusinessException(ResponseCode.MBR_NOT_FOUND));
        return MemberResponseDto.InfoDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickName(member.getNickName())
                .emoji(member.getEmoji())
                .build();
    }
}