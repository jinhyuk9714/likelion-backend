package backend.like.controller;

import backend.global.response.ApiResponse;
import backend.like.dto.LikeRequestDTO;
import backend.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post/{id}/like")
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    public ApiResponse<Void> likePost(@PathVariable Long id, @RequestBody LikeRequestDTO likeRequestDTO) {
        likeService.likePost(id, likeRequestDTO);
        return ApiResponse.success(null, "좋아요가 성공적으로 추가되었습니다.");
    }

    @DeleteMapping
    public ApiResponse<Void> unlikePost(@PathVariable Long id, @RequestBody LikeRequestDTO likeRequestDTO) {
        likeService.unlikePost(id, likeRequestDTO);
        return ApiResponse.success(null, "좋아요가 성공적으로 취소되었습니다.");
    }
}