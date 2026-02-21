package backend.backend.service;

import backend.backend.domain.Likes;
import backend.backend.domain.Member;
import backend.backend.domain.Post;
import backend.backend.domain.common.BusinessException;
import backend.backend.domain.common.ResponseCode;
import backend.backend.repository.LikesRepository;
import backend.backend.repository.MemberRepository;
import backend.backend.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikesServiceTest {

    @InjectMocks
    private LikesService likesService;

    @Mock
    private LikesRepository likesRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PostRepository postRepository;

    private Member createMember() {
        return Member.builder()
                .email("test@test.com")
                .password("password")
                .nickName("테스터")
                .build();
    }

    private Post createPost(Member member) {
        return new Post("제목", "내용", member);
    }

    @Test
    @DisplayName("좋아요 성공")
    void likePost_success() {
        Member member = createMember();
        Post post = createPost(member);

        when(memberRepository.findByEmail("test@test.com")).thenReturn(Optional.of(member));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(likesRepository.findByMemberAndPost(member, post)).thenReturn(Optional.empty());

        likesService.likePost(1L, "test@test.com");

        verify(likesRepository).save(any(Likes.class));
    }

    @Test
    @DisplayName("좋아요 실패 - 이미 좋아요")
    void likePost_alreadyLiked() {
        Member member = createMember();
        Post post = createPost(member);

        when(memberRepository.findByEmail("test@test.com")).thenReturn(Optional.of(member));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(likesRepository.findByMemberAndPost(member, post)).thenReturn(Optional.of(new Likes(member, post)));

        assertThatThrownBy(() -> likesService.likePost(1L, "test@test.com"))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ResponseCode.LIK_ALREADY_LIKED));
    }

    @Test
    @DisplayName("좋아요 실패 - 회원 없음")
    void likePost_memberNotFound() {
        when(memberRepository.findByEmail("none@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> likesService.likePost(1L, "none@test.com"))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ResponseCode.LIK_MEMBER_NOT_FOUND));
    }

    @Test
    @DisplayName("좋아요 취소 성공")
    void unlikePost_success() {
        Member member = createMember();
        Post post = createPost(member);
        Likes likes = new Likes(member, post);

        when(memberRepository.findByEmail("test@test.com")).thenReturn(Optional.of(member));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(likesRepository.findByMemberAndPost(member, post)).thenReturn(Optional.of(likes));

        likesService.unlikePost(1L, "test@test.com");

        verify(likesRepository).delete(likes);
    }

    @Test
    @DisplayName("좋아요 취소 실패 - 좋아요 없음")
    void unlikePost_notFound() {
        Member member = createMember();
        Post post = createPost(member);

        when(memberRepository.findByEmail("test@test.com")).thenReturn(Optional.of(member));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(likesRepository.findByMemberAndPost(member, post)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> likesService.unlikePost(1L, "test@test.com"))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ResponseCode.LIK_NOT_FOUND));
    }
}
