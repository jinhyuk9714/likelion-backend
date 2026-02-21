package backend.backend.service;

import backend.backend.domain.Comment;
import backend.backend.domain.Member;
import backend.backend.domain.Post;
import backend.backend.domain.common.BusinessException;
import backend.backend.domain.common.ResponseCode;
import backend.backend.domain.dto.commentDto.CommentRequestDto;
import backend.backend.domain.dto.commentDto.CommentResponseDto;
import backend.backend.repository.CommentRepository;
import backend.backend.repository.MemberRepository;
import backend.backend.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CommentRepository commentRepository;

    private Member createMember() {
        return Member.builder()
                .email("test@test.com")
                .password("password")
                .nickName("테스터")
                .emoji("😀")
                .build();
    }

    private Post createPost(Member member) {
        return Post.builder()
                .id(1L)
                .title("제목")
                .content("내용")
                .member(member)
                .comments(new ArrayList<>())
                .build();
    }

    private CommentRequestDto createCommentRequestDto(String content) {
        CommentRequestDto dto = new CommentRequestDto();
        ReflectionTestUtils.setField(dto, "content", content);
        return dto;
    }

    @Test
    @DisplayName("댓글 작성 성공 - 부모 댓글")
    void createComment_parent() {
        Member member = createMember();
        Post post = createPost(member);
        CommentRequestDto dto = createCommentRequestDto("댓글 내용");
        Comment savedComment = Comment.builder()
                .id(1L)
                .content("댓글 내용")
                .nickname("테스터")
                .post(post)
                .member(member)
                .depth(0)
                .children(new ArrayList<>())
                .build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(memberRepository.findByEmail("test@test.com")).thenReturn(Optional.of(member));
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        CommentResponseDto result = commentService.createComment(1L, 0L, dto, "test@test.com");

        assertThat(result.getContent()).isEqualTo("댓글 내용");
        assertThat(result.getDepth()).isEqualTo(0);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 작성 성공 - 대댓글")
    void createComment_child() {
        Member member = createMember();
        Post post = createPost(member);
        CommentRequestDto dto = createCommentRequestDto("대댓글 내용");
        Comment parentComment = Comment.builder()
                .id(10L)
                .content("부모 댓글")
                .nickname("테스터")
                .post(post)
                .member(member)
                .depth(0)
                .children(new ArrayList<>())
                .build();
        Comment childComment = Comment.builder()
                .id(11L)
                .content("대댓글 내용")
                .nickname("테스터")
                .post(post)
                .member(member)
                .parent(parentComment)
                .depth(1)
                .children(new ArrayList<>())
                .build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(memberRepository.findByEmail("test@test.com")).thenReturn(Optional.of(member));
        when(commentRepository.findById(10L)).thenReturn(Optional.of(parentComment));
        when(commentRepository.findByPostAndId(post, 10L)).thenReturn(Optional.of(parentComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(childComment);

        CommentResponseDto result = commentService.createComment(1L, 10L, dto, "test@test.com");

        assertThat(result.getContent()).isEqualTo("대댓글 내용");
        assertThat(result.getDepth()).isEqualTo(1);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 작성 실패 - 게시글 없음")
    void createComment_postNotFound() {
        CommentRequestDto dto = createCommentRequestDto("댓글 내용");

        when(postRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.createComment(99L, 0L, dto, "test@test.com"))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ResponseCode.CMT_POST_NOT_FOUND));
    }

    @Test
    @DisplayName("댓글 조회 성공 - 자식 댓글 포함")
    void getComment_success() {
        Member member = createMember();
        Post post = createPost(member);
        Comment parentComment = Comment.builder()
                .id(1L)
                .content("부모 댓글")
                .nickname("테스터")
                .post(post)
                .member(member)
                .depth(0)
                .children(new ArrayList<>())
                .build();
        Comment childComment = Comment.builder()
                .id(2L)
                .content("자식 댓글")
                .nickname("테스터")
                .post(post)
                .member(member)
                .parent(parentComment)
                .depth(1)
                .children(new ArrayList<>())
                .build();

        when(commentRepository.findById(1L)).thenReturn(Optional.of(parentComment));
        when(commentRepository.findChildrenComments(1L)).thenReturn(List.of(childComment));

        CommentResponseDto result = commentService.getComment(1L);

        assertThat(result.getContent()).isEqualTo("부모 댓글");
        assertThat(result.getChildren()).hasSize(1);
        assertThat(result.getChildren().get(0).getContent()).isEqualTo("자식 댓글");
    }

    @Test
    @DisplayName("댓글 수정 성공")
    void updateComment_success() {
        Member member = createMember();
        Post post = createPost(member);
        Comment comment = Comment.builder()
                .id(1L)
                .content("원래 댓글")
                .nickname("테스터")
                .post(post)
                .member(member)
                .depth(0)
                .children(new ArrayList<>())
                .build();
        CommentRequestDto dto = createCommentRequestDto("수정 댓글");

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        CommentResponseDto result = commentService.updateComment(1L, dto, "test@test.com");

        assertThat(result.getContent()).isEqualTo("수정 댓글");
    }

    @Test
    @DisplayName("댓글 수정 실패 - 작성자 아님")
    void updateComment_authFail() {
        Member member = createMember();
        Post post = createPost(member);
        Comment comment = Comment.builder()
                .id(1L)
                .content("원래 댓글")
                .nickname("테스터")
                .post(post)
                .member(member)
                .depth(0)
                .children(new ArrayList<>())
                .build();
        CommentRequestDto dto = createCommentRequestDto("수정 댓글");

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.updateComment(1L, dto, "other@test.com"))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ResponseCode.CMT_AUTHENTICATION_FAIL));
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void deleteComment_success() {
        Member member = createMember();
        Post post = createPost(member);
        Comment comment = Comment.builder()
                .id(1L)
                .content("댓글")
                .nickname("테스터")
                .post(post)
                .member(member)
                .depth(0)
                .children(new ArrayList<>())
                .build();

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        commentService.deleteComment(1L, "test@test.com");

        verify(commentRepository).deleteById(1L);
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 작성자 아님")
    void deleteComment_authFail() {
        Member member = createMember();
        Post post = createPost(member);
        Comment comment = Comment.builder()
                .id(1L)
                .content("댓글")
                .nickname("테스터")
                .post(post)
                .member(member)
                .depth(0)
                .children(new ArrayList<>())
                .build();

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.deleteComment(1L, "other@test.com"))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ResponseCode.CMT_AUTHENTICATION_FAIL));
    }
}
