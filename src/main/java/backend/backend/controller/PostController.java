package backend.backend.controller;

import backend.backend.domain.dto.Response;
import backend.backend.domain.dto.postDto.PostRequestDto;
import backend.backend.domain.dto.postDto.PostResponseDto;
import backend.backend.global.util.security.SecurityUtil;
import backend.backend.service.LikesService;
import backend.backend.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {

    private final LikesService likesService;
    private final PostService postService;

    @GetMapping
    public ResponseEntity<Response<Page<PostResponseDto>>> getPostList(
            @PageableDefault(size = 10, sort = "id", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(Response.ok(postService.getPostList(pageable)));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Response<Void>> likePost(@PathVariable Long id) {
        String memberEmail = SecurityUtil.getLoginEmail();
        likesService.likePost(id, memberEmail);
        return ResponseEntity.ok(Response.ok("좋아요가 성공적으로 추가되었습니다."));
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<Response<Void>> unlikePost(@PathVariable Long id) {
        String memberEmail = SecurityUtil.getLoginEmail();
        likesService.unlikePost(id, memberEmail);
        return ResponseEntity.ok(Response.ok("좋아요가 성공적으로 취소되었습니다."));
    }

    @PostMapping()
    public ResponseEntity<Response<PostResponseDto>> createPost(@Validated @RequestBody PostRequestDto requestDto){
        String memberEmail = SecurityUtil.getLoginEmail();
        PostResponseDto postResponseDto = postService.createPost(requestDto, memberEmail);
        return ResponseEntity.ok(Response.ok(postResponseDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<PostResponseDto>> getPost(@PathVariable("id") Long postId){
        return ResponseEntity.ok(Response.ok(postService.getPost(postId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<PostResponseDto>> updatePost(@PathVariable("id") Long postId ,
                                                                @RequestBody PostRequestDto requestDto){
        String memberEmail = SecurityUtil.getLoginEmail();
        return ResponseEntity.ok(Response.ok(postService.updatePost(postId, requestDto, memberEmail)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Void>> deletePost(@PathVariable("id") Long postId){
        String memberEmail = SecurityUtil.getLoginEmail();
        return ResponseEntity.ok(postService.deletePost(postId, memberEmail));

    }
}
