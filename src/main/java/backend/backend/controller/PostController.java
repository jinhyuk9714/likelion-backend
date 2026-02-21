package backend.backend.controller;

import backend.backend.domain.dto.Response;
import backend.backend.domain.dto.postDto.PostRequestDto;
import backend.backend.domain.dto.postDto.PostResponseDto;
import backend.backend.global.util.security.SecurityUtil;
import backend.backend.service.LikesService;
import backend.backend.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "게시글 API", description = "게시글 CRUD 및 좋아요 기능")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {

    private final LikesService likesService;
    private final PostService postService;

    @Operation(summary = "게시글 목록 조회", description = "페이지네이션으로 게시글 목록 조회 (기본 10개, id DESC)")
    @GetMapping
    public ResponseEntity<Response<Page<PostResponseDto>>> getPostList(
            @PageableDefault(size = 10, sort = "id", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(Response.ok(postService.getPostList(pageable)));
    }

    @Operation(summary = "좋아요 추가", description = "게시글에 좋아요 추가")
    @PostMapping("/{id}/like")
    public ResponseEntity<Response<Void>> likePost(@PathVariable Long id) {
        String memberEmail = SecurityUtil.getLoginEmail();
        likesService.likePost(id, memberEmail);
        return ResponseEntity.ok(Response.ok("좋아요가 성공적으로 추가되었습니다."));
    }

    @Operation(summary = "좋아요 취소", description = "게시글 좋아요 취소")
    @DeleteMapping("/{id}/like")
    public ResponseEntity<Response<Void>> unlikePost(@PathVariable Long id) {
        String memberEmail = SecurityUtil.getLoginEmail();
        likesService.unlikePost(id, memberEmail);
        return ResponseEntity.ok(Response.ok("좋아요가 성공적으로 취소되었습니다."));
    }

    @Operation(summary = "게시글 작성", description = "새 게시글 작성")
    @PostMapping()
    public ResponseEntity<Response<PostResponseDto>> createPost(@Validated @RequestBody PostRequestDto requestDto){
        String memberEmail = SecurityUtil.getLoginEmail();
        PostResponseDto postResponseDto = postService.createPost(requestDto, memberEmail);
        return ResponseEntity.ok(Response.ok(postResponseDto));
    }

    @Operation(summary = "게시글 상세 조회", description = "게시글 상세 정보 및 댓글 트리 조회")
    @GetMapping("/{id}")
    public ResponseEntity<Response<PostResponseDto>> getPost(@PathVariable("id") Long postId){
        return ResponseEntity.ok(Response.ok(postService.getPost(postId)));
    }

    @Operation(summary = "게시글 수정", description = "게시글 수정 (작성 당일만 가능)")
    @PutMapping("/{id}")
    public ResponseEntity<Response<PostResponseDto>> updatePost(@PathVariable("id") Long postId ,
                                                                @RequestBody PostRequestDto requestDto){
        String memberEmail = SecurityUtil.getLoginEmail();
        return ResponseEntity.ok(Response.ok(postService.updatePost(postId, requestDto, memberEmail)));
    }

    @Operation(summary = "게시글 삭제", description = "게시글 소프트 삭제 (작성자만 가능)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Void>> deletePost(@PathVariable("id") Long postId){
        String memberEmail = SecurityUtil.getLoginEmail();
        return ResponseEntity.ok(postService.deletePost(postId, memberEmail));

    }
}
