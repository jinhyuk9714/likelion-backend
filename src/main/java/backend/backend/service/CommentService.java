package backend.backend.service;

import backend.backend.domain.Comment;
import backend.backend.domain.Member;
import backend.backend.domain.Post;
import backend.backend.domain.common.BusinessException;
import backend.backend.domain.common.ResponseCode;
import backend.backend.domain.dto.Response;
import backend.backend.domain.dto.commentDto.CommentRequestDto;
import backend.backend.domain.dto.commentDto.CommentResponseDto;
import backend.backend.repository.CommentRepository;
import backend.backend.repository.MemberRepository;
import backend.backend.repository.PostRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;


    public CommentResponseDto createComment(Long postId, Long commentId, CommentRequestDto requestDto, String memberEmail) {

        Post post = postRepository.findById(postId).orElseThrow(() -> new BusinessException(ResponseCode.CMT_POST_NOT_FOUND));

        Member member = memberRepository.findByEmail(memberEmail).orElseThrow(() -> new BusinessException(ResponseCode.CMT_AUTHENTICATION_FAIL));

        Comment comment;
        if (commentId == 0) {
            comment = commentRepository.save(new Comment(requestDto.getContent(), member.getNickName(), post, member));
        } else {
            Comment childComment = commentRepository.findById(commentId).orElseThrow(
                    () -> new BusinessException(ResponseCode.CMT_PARENT_NOT_FOUND));

            if (commentRepository.findByPostAndId(post, commentId).isEmpty()) {
                throw new BusinessException(ResponseCode.CMT_PARENT_NOT_FOUND);
            }

            comment = commentRepository.save(new Comment(requestDto.getContent(), member.getNickName(), post, member, childComment));
        }
        return new CommentResponseDto(comment, commentId);
    }


    public CommentResponseDto getComment(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new BusinessException(ResponseCode.CMT_POST_NOT_FOUND)
        );

        List<Comment> childrenComments = commentRepository.findChildrenComments(id);
        List<CommentResponseDto> childrenDtoList = new ArrayList<>();

        for (Comment childComment : childrenComments) {
            CommentResponseDto childDto = new CommentResponseDto(childComment, id);
            childrenDtoList.add(childDto);
        }

        return new CommentResponseDto(comment, childrenDtoList);
    }


    public CommentResponseDto updateComment(Long id, CommentRequestDto requestDto, String memberEmail) {
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new BusinessException(ResponseCode.CMT_NOT_FOUND)
        );

        if (comment.getMember().getEmail().equals(memberEmail)) {
            comment.update(requestDto.getContent());
        } else {
            throw new BusinessException(ResponseCode.CMT_AUTHENTICATION_FAIL);
        }
        return new CommentResponseDto(comment);
    }


    public Response<Void> deleteComment(Long id, String memberEmail) {
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new BusinessException(ResponseCode.CMT_NOT_FOUND)
        );
        if (comment.getMember().getEmail().equals(memberEmail)) {
            commentRepository.deleteById(id);
        } else {
            throw new BusinessException(ResponseCode.CMT_AUTHENTICATION_FAIL);
        }

        return Response.ok();
    }
}

