package backend.backend.domain.dto.postDto;

import backend.backend.domain.Post;
import backend.backend.domain.dto.commentDto.CommentResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {
    private String title;
    private Long id;
    private String nickName;
    private String emoji;
    private String email;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CommentResponseDto> commentList = new ArrayList<>();
    private int likeCount;
    private int commentCount;

    public PostResponseDto(Post post){
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
        this.nickName = post.getMember().getNickName();
        this.emoji = post.getMember().getEmoji();
        this.email = post.getMember().getEmail();
    }

    public PostResponseDto(Post post, List<CommentResponseDto> commentResponseDtos, int commentCount, int likeCount){
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
        this.nickName = post.getMember().getNickName();
        this.emoji = post.getMember().getEmoji();
        this.email = post.getMember().getEmail();
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.commentList = commentResponseDtos;
    }

    public PostResponseDto(Post post, int commentCount, int likeCount){
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
        this.nickName = post.getMember().getNickName();
        this.emoji = post.getMember().getEmoji();
        this.email = post.getMember().getEmail();
        this.likeCount = likeCount;
        this.commentCount = commentCount;
    }

}
