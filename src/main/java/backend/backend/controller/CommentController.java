package backend.backend.controller;

import backend.backend.domain.dto.Response;
import backend.backend.domain.dto.commentDto.CommentRequestDto;
import backend.backend.domain.dto.commentDto.CommentResponseDto;
import backend.backend.global.util.security.SecurityUtil;
import backend.backend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/post/{id}/comment/{comment_id}") // 뒤쪽 comment_id는 부모 댓글일 경우 0으로 입력
    public ResponseEntity<Response<CommentResponseDto>> createComment(@Validated @PathVariable Long id, @PathVariable(required = false) Long comment_id,
                                                                      @RequestBody CommentRequestDto requestDto){
        String memberEmail = SecurityUtil.getLoginEmail();
        return ResponseEntity.ok(Response.ok(commentService.createComment(id, comment_id, requestDto, memberEmail)));
    }
    @PutMapping("comment/{id}")
    public ResponseEntity<Response<CommentResponseDto>> updateComment(@Validated @PathVariable Long id, @RequestBody CommentRequestDto requestDto){
        String memberEmail = SecurityUtil.getLoginEmail();
        return ResponseEntity.ok(Response.ok(commentService.updateComment(id, requestDto, memberEmail)));
    }

    @DeleteMapping("comment/{id}")
    public ResponseEntity<Response<Void>> deleteComment(@PathVariable  Long id){
        String memberEmail = SecurityUtil.getLoginEmail();
        return ResponseEntity.ok(commentService.deleteComment(id, memberEmail));
    }

    //comment 상세
    @GetMapping("comment/{id}")
    public ResponseEntity<Response<CommentResponseDto>> getComment(@PathVariable Long id) {
        return ResponseEntity.ok(Response.ok(commentService.getComment(id)));
    }
}