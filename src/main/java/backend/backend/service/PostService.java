package backend.backend.service;

import backend.backend.domain.Comment;
import backend.backend.domain.Member;
import backend.backend.domain.Post;
import backend.backend.domain.common.BusinessException;
import backend.backend.domain.common.ResponseCode;
import backend.backend.domain.dto.Response;
import backend.backend.domain.dto.commentDto.CommentResponseDto;
import backend.backend.domain.dto.postDto.PostRequestDto;
import backend.backend.domain.dto.postDto.PostResponseDto;
import backend.backend.repository.CommentRepository;
import backend.backend.repository.LikesRepository;
import backend.backend.repository.MemberRepository;
import backend.backend.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final LikesRepository likesRepository;
    private final CommentRepository commentRepository;

    public Page<PostResponseDto> getPostList(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        return posts.map(post -> {
            int likeCount = likesRepository.countLikesByPostId(post.getId());
            int commentCount = commentRepository.countCommentByPostId(post.getId());
            return new PostResponseDto(post, commentCount, likeCount);
        });
    }

    @Transactional
    public PostResponseDto createPost(PostRequestDto postRequestDto, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new BusinessException(ResponseCode.MBR_NOT_FOUND));

        Post post = postRepository.save(new Post(postRequestDto.getTitle(), postRequestDto.getContent(), member));
        return new PostResponseDto(post);
    }

    public PostResponseDto getPost(Long postId){
        Post post = postRepository.findById(postId).
                orElseThrow(() -> new BusinessException(ResponseCode.POS_NOT_FOUND));

        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
        int likeCount = likesRepository.countLikesByPostId(post.getId());
        int commentCount = commentRepository.countCommentByPostId(post.getId());
        for(Comment comment : post.getComments()){
            List<CommentResponseDto> childCommentList = new ArrayList<>();
            if (comment.getParent() == null) {
                for(Comment childComment : comment.getChildren()){
                    if(postId.equals(childComment.getPost().getId())){
                        childCommentList.add(new CommentResponseDto(childComment));
                    }
                }
                commentResponseDtoList.add(new CommentResponseDto(comment,childCommentList));
            }
        }
        return new PostResponseDto(post, commentResponseDtoList,commentCount,likeCount);
    }

    @Transactional
    public PostResponseDto updatePost(Long postId, PostRequestDto postRequestDto, String memberEmail){
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new BusinessException(ResponseCode.POS_NOT_FOUND)
        );
        LocalDate postCreationDate = post.getCreatedAt().toLocalDate();
        LocalDate currentDate = LocalDate.now();
        if(memberEmail.equals(post.getMember().getEmail())){
            if(!currentDate.isEqual(postCreationDate)){
                throw new BusinessException(ResponseCode.POS_UPDATE_EXPIRED);
            }else{
                post.update(postRequestDto.getTitle(), postRequestDto.getContent());
                return new PostResponseDto(post);
            }
        }else{
            throw new BusinessException(ResponseCode.POS_AUTHENTICATION_FAIL);
        }
    }

    @Transactional
    public Response<Void> deletePost(Long postId, String memberEmail){
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new BusinessException(ResponseCode.POS_NOT_FOUND)
        );

        if(post.getMember().getEmail().equals(memberEmail)){
            postRepository.deleteById(postId);
        }else{
            throw new BusinessException(ResponseCode.POS_AUTHENTICATION_FAIL);
        }
        return Response.ok();
    }

}
