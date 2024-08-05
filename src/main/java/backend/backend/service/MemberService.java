package backend.backend.service;

import backend.backend.domain.Member;
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

    public void signUp(MemberRequestDto.SignUpDto memberSignUpDto) throws Exception {
        Member member = memberSignUpDto.toEntity();
        member.encodePassword(passwordEncoder);

        if(memberRepository.findByEmail(memberSignUpDto.email()).isPresent()){
            throw new Exception("이미 존재하는 아이디입니다.");
        }

        memberRepository.save(member);
    }

    public MemberResponseDto.InfoDto getMyInfo() {
        Member member = memberRepository.findByEmail(SecurityUtil.getLoginEmail()).orElseThrow();
        return MemberResponseDto.InfoDto.builder()
                .email(member.getEmail())
                .nickName(member.getNickName())
                .emoji(member.getEmoji())
                .build();
    }
}