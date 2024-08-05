package backend.like.service;

import backend.like.Post;
import backend.like.PostRepository;
import backend.like.Member;
import backend.like.MemberRepository;
import backend.like.dto.LikeRequestDTO;
import backend.like.entity.Like;
import backend.like.repository.LikeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Transactional
    public void likePost(Long id, LikeRequestDTO likeRequestDTO) {
        Member member = memberRepository.findById(likeRequestDTO.getMemberId())
                .orElseThrow(() -> new RuntimeException("해당 회원이 존재하지 않습니다."));

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        if (likeRepository.findByUserAndPost(member, post).isPresent()) {
            throw new RuntimeException("이미 이 게시글을 좋아요했습니다.");
        }

        Like like = Like.builder()
                .member(member)
                .post(post)
                .build();

        likeRepository.save(like);
    }

    @Transactional
    public void unlikePost(Long id, LikeRequestDTO likeRequestDTO) {
        Member member = memberRepository.findById(likeRequestDTO.getMemberId())
                .orElseThrow(() -> new RuntimeException("해당 회원이 존재하지 않습니다."));

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        Like like = likeRepository.findByUserAndPost(member, post)
                .orElseThrow(() -> new RuntimeException("좋아요를 찾을 수 없습니다."));

        likeRepository.delete(like);
    }
}