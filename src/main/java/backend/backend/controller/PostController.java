package backend.backend.controller;

import backend.backend.domain.dto.likesDto.LikesRequestDto;
import backend.backend.global.response.ApiResponse;
import backend.backend.service.LikesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {

    private final LikesService likesService;

    @PostMapping("/{id}/like")
    public ApiResponse<Void> likePost(@PathVariable Long id, @RequestBody LikesRequestDto likesRequestDto) {
        likesService.likePost(id, likesRequestDto);
        return ApiResponse.success(null, "좋아요가 성공적으로 추가되었습니다.");
    }

    @DeleteMapping("/{id}/like")
    public ApiResponse<Void> unlikePost(@PathVariable Long id, @RequestBody LikesRequestDto likesRequestDto) {
        likesService.unlikePost(id, likesRequestDto);
        return ApiResponse.success(null, "좋아요가 성공적으로 취소되었습니다.");
    }
}
