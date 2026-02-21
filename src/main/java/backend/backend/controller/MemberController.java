package backend.backend.controller;

import backend.backend.domain.dto.Response;
import backend.backend.domain.dto.memberDto.MemberRequestDto;
import backend.backend.domain.dto.memberDto.MemberResponseDto;
import backend.backend.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "회원 API", description = "회원가입 및 회원 정보 조회")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원가입", description = "이메일, 비밀번호, 닉네임, 이모지로 회원가입")
    @PostMapping("/signUp")
    @ResponseStatus(HttpStatus.OK)
    public void signUp(@Valid @RequestBody MemberRequestDto.SignUpDto memberSignUpDto) {
        memberService.signUp(memberSignUpDto);
    }

    @Operation(summary = "내 정보 조회", description = "로그인된 회원의 정보를 조회")
    @GetMapping("/member")
    public ResponseEntity<Response<MemberResponseDto.InfoDto>> getMyInfo() {
        MemberResponseDto.InfoDto info = memberService.getMyInfo();
        return ResponseEntity.ok(Response.ok(info));
    }
}

