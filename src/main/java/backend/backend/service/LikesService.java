package backend.backend.service;

import backend.backend.domain.Likes;
import backend.backend.domain.Member;
import backend.backend.domain.Post;
import backend.backend.domain.dto.likesDto.LikesRequestDto;
import backend.backend.repository.LikesRepository;
import backend.backend.repository.MemberRepository;
import backend.backend.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikesService {

    private final LikesRepository likesRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Transactional
    public void likePost(Long id, LikesRequestDto likesRequestDTO) {
        Member member = memberRepository.findById(likesRequestDTO.getMemberId())
                .orElseThrow(() -> new RuntimeException("해당 회원이 존재하지 않습니다."));

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        if (likesRepository.findByMemberAndPost(member, post).isPresent()) {
            throw new RuntimeException("이미 이 게시글을 좋아요했습니다.");
        }

        Likes likes = new Likes(member, post);

        likesRepository.save(likes);
    }

    @Transactional
    public void unlikePost(Long id, LikesRequestDto likesRequestDTO) {
        Member member = memberRepository.findById(likesRequestDTO.getMemberId())
                .orElseThrow(() -> new RuntimeException("해당 회원이 존재하지 않습니다."));

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        Likes like = likesRepository.findByMemberAndPost(member, post)
                .orElseThrow(() -> new RuntimeException("좋아요를 찾을 수 없습니다."));

        likesRepository.delete(like);
    }
}