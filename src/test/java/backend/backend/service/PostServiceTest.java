package backend.backend.service;

import backend.backend.domain.Comment;
import backend.backend.domain.Member;
import backend.backend.domain.Post;
import backend.backend.domain.common.BusinessException;
import backend.backend.domain.common.ResponseCode;
import backend.backend.domain.dto.postDto.PostRequestDto;
import backend.backend.domain.dto.postDto.PostResponseDto;
import backend.backend.repository.CommentRepository;
import backend.backend.repository.LikesRepository;
import backend.backend.repository.MemberRepository;
import backend.backend.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private LikesRepository likesRepository;

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
        Post post = Post.builder()
                .id(1L)
                .title("제목")
                .content("내용")
                .member(member)
                .comments(new ArrayList<>())
                .build();
        ReflectionTestUtils.setField(post, "createdAt", LocalDateTime.now());
        return post;
    }

    @Test
    @DisplayName("게시글 목록 조회 성공")
    void getPostList_success() {
        Member member = createMember();
        Post post = createPost(member);
        Page<Post> postPage = new PageImpl<>(List.of(post));
        Pageable pageable = PageRequest.of(0, 10);

        when(postRepository.findAll(pageable)).thenReturn(postPage);
        when(likesRepository.countLikesByPostId(1L)).thenReturn(3);
        when(commentRepository.countCommentByPostId(1L)).thenReturn(2);

        Page<PostResponseDto> result = postService.getPostList(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getLikeCount()).isEqualTo(3);
        assertThat(result.getContent().get(0).getCommentCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("게시글 작성 성공")
    void createPost_success() {
        Member member = createMember();
        PostRequestDto dto = new PostRequestDto("제목", "내용");
        Post savedPost = createPost(member);

        when(memberRepository.findByEmail("test@test.com")).thenReturn(Optional.of(member));
        when(postRepository.save(any(Post.class))).thenReturn(savedPost);

        PostResponseDto result = postService.createPost(dto, "test@test.com");

        assertThat(result.getTitle()).isEqualTo("제목");
        assertThat(result.getContent()).isEqualTo("내용");
        verify(postRepository).save(any(Post.class));
    }

    @Test
    @DisplayName("게시글 작성 실패 - 회원 없음")
    void createPost_memberNotFound() {
        PostRequestDto dto = new PostRequestDto("제목", "내용");

        when(memberRepository.findByEmail("none@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.createPost(dto, "none@test.com"))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ResponseCode.MBR_NOT_FOUND));
    }

    @Test
    @DisplayName("게시글 상세 조회 성공 - 댓글 트리 포함")
    void getPost_success() {
        Member member = createMember();
        Comment parentComment = Comment.builder()
                .id(10L)
                .content("부모 댓글")
                .nickname("테스터")
                .member(member)
                .depth(0)
                .children(new ArrayList<>())
                .build();

        Post post = Post.builder()
                .id(1L)
                .title("제목")
                .content("내용")
                .member(member)
                .comments(List.of(parentComment))
                .build();
        ReflectionTestUtils.setField(post, "createdAt", LocalDateTime.now());

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(likesRepository.countLikesByPostId(1L)).thenReturn(5);
        when(commentRepository.countCommentByPostId(1L)).thenReturn(1);

        PostResponseDto result = postService.getPost(1L);

        assertThat(result.getTitle()).isEqualTo("제목");
        assertThat(result.getLikeCount()).isEqualTo(5);
        assertThat(result.getCommentCount()).isEqualTo(1);
        assertThat(result.getCommentList()).hasSize(1);
    }

    @Test
    @DisplayName("게시글 상세 조회 실패 - 게시글 없음")
    void getPost_notFound() {
        when(postRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.getPost(99L))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ResponseCode.POS_NOT_FOUND));
    }

    @Test
    @DisplayName("게시글 수정 성공")
    void updatePost_success() {
        Member member = createMember();
        Post post = createPost(member);
        PostRequestDto dto = new PostRequestDto("수정 제목", "수정 내용");

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        PostResponseDto result = postService.updatePost(1L, dto, "test@test.com");

        assertThat(result.getTitle()).isEqualTo("수정 제목");
        assertThat(result.getContent()).isEqualTo("수정 내용");
    }

    @Test
    @DisplayName("게시글 수정 실패 - 작성자 아님")
    void updatePost_authFail() {
        Member member = createMember();
        Post post = createPost(member);
        PostRequestDto dto = new PostRequestDto("수정 제목", "수정 내용");

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        assertThatThrownBy(() -> postService.updatePost(1L, dto, "other@test.com"))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ResponseCode.POS_AUTHENTICATION_FAIL));
    }

    @Test
    @DisplayName("게시글 수정 실패 - 수정 기한 만료")
    void updatePost_expired() {
        Member member = createMember();
        Post post = Post.builder()
                .id(1L)
                .title("제목")
                .content("내용")
                .member(member)
                .comments(new ArrayList<>())
                .build();
        ReflectionTestUtils.setField(post, "createdAt", LocalDateTime.now().minusDays(1));
        PostRequestDto dto = new PostRequestDto("수정 제목", "수정 내용");

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        assertThatThrownBy(() -> postService.updatePost(1L, dto, "test@test.com"))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ResponseCode.POS_UPDATE_EXPIRED));
    }

    @Test
    @DisplayName("게시글 삭제 성공")
    void deletePost_success() {
        Member member = createMember();
        Post post = createPost(member);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        postService.deletePost(1L, "test@test.com");

        verify(postRepository).deleteById(1L);
    }

    @Test
    @DisplayName("게시글 삭제 실패 - 작성자 아님")
    void deletePost_authFail() {
        Member member = createMember();
        Post post = createPost(member);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        assertThatThrownBy(() -> postService.deletePost(1L, "other@test.com"))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ResponseCode.POS_AUTHENTICATION_FAIL));
    }
}
