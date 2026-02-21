package backend.backend.controller;

import backend.backend.domain.dto.Response;
import backend.backend.domain.dto.commentDto.CommentRequestDto;
import backend.backend.domain.dto.commentDto.CommentResponseDto;
import backend.backend.global.util.security.SecurityUtil;
import backend.backend.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "댓글 API", description = "댓글 및 대댓글 CRUD")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class CommentController {
    private final CommentService commentService;

    @Operation(summary = "댓글 작성", description = "댓글(comment_id=0) 또는 대댓글(comment_id=부모ID) 작성")
    @PostMapping("/post/{id}/comment/{comment_id}")
    public ResponseEntity<Response<CommentResponseDto>> createComment(@Validated @PathVariable Long id, @PathVariable(required = false) Long comment_id,
                                                                      @RequestBody CommentRequestDto requestDto){
        String memberEmail = SecurityUtil.getLoginEmail();
        return ResponseEntity.ok(Response.ok(commentService.createComment(id, comment_id, requestDto, memberEmail)));
    }

    @Operation(summary = "댓글 수정", description = "댓글 내용 수정 (작성자만 가능)")
    @PutMapping("comment/{id}")
    public ResponseEntity<Response<CommentResponseDto>> updateComment(@Validated @PathVariable Long id, @RequestBody CommentRequestDto requestDto){
        String memberEmail = SecurityUtil.getLoginEmail();
        return ResponseEntity.ok(Response.ok(commentService.updateComment(id, requestDto, memberEmail)));
    }

    @Operation(summary = "댓글 삭제", description = "댓글 소프트 삭제 (작성자만 가능)")
    @DeleteMapping("comment/{id}")
    public ResponseEntity<Response<Void>> deleteComment(@PathVariable  Long id){
        String memberEmail = SecurityUtil.getLoginEmail();
        return ResponseEntity.ok(commentService.deleteComment(id, memberEmail));
    }

    @Operation(summary = "댓글 상세 조회", description = "댓글 상세 정보 및 자식 댓글 조회")
    @GetMapping("comment/{id}")
    public ResponseEntity<Response<CommentResponseDto>> getComment(@PathVariable Long id) {
        return ResponseEntity.ok(Response.ok(commentService.getComment(id)));
    }
}