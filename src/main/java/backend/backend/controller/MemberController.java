package backend.backend.controller;

import backend.backend.domain.dto.Response;
import backend.backend.domain.dto.memberDto.MemberRequestDto;
import backend.backend.domain.dto.memberDto.MemberResponseDto;
import backend.backend.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signUp")
    @ResponseStatus(HttpStatus.OK)
    public void signUp(@Valid @RequestBody MemberRequestDto.SignUpDto memberSignUpDto) {
        memberService.signUp(memberSignUpDto);
    }

    @GetMapping("/member")
    public ResponseEntity<Response<MemberResponseDto.InfoDto>> getMyInfo() {
        MemberResponseDto.InfoDto info = memberService.getMyInfo();
        return ResponseEntity.ok(Response.ok(info));
    }
}

