package backend.backend.controller;

import backend.backend.domain.dto.memberDto.MemberRequestDto;
import backend.backend.sevice.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원가입
     */
    @PostMapping("/signUp")
    @ResponseStatus(HttpStatus.OK)
    public void signUp(@Valid @RequestBody MemberRequestDto.SignUpDto memberSignUpDto) throws Exception {
        memberService.signUp(memberSignUpDto);
    }
}

