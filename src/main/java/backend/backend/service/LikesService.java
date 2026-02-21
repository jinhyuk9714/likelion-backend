package backend.backend.service;

import backend.backend.domain.Likes;
import backend.backend.domain.Member;
import backend.backend.domain.Post;
import backend.backend.domain.common.BusinessException;
import backend.backend.domain.common.ResponseCode;
import backend.backend.repository.LikesRepository;
import backend.backend.repository.MemberRepository;
import backend.backend.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikesService {

    private final LikesRepository likesRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Transactional
    public void likePost(Long postId, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new BusinessException(ResponseCode.LIK_MEMBER_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ResponseCode.LIK_POST_NOT_FOUND));

        if (likesRepository.findByMemberAndPost(member, post).isPresent()) {
            throw new BusinessException(ResponseCode.LIK_ALREADY_LIKED);
        }

        Likes likes = new Likes(member, post);
        likesRepository.save(likes);
    }

    @Transactional
    public void unlikePost(Long postId, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new BusinessException(ResponseCode.LIK_MEMBER_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ResponseCode.LIK_POST_NOT_FOUND));

        Likes like = likesRepository.findByMemberAndPost(member, post)
                .orElseThrow(() -> new BusinessException(ResponseCode.LIK_NOT_FOUND));

        likesRepository.delete(like);
    }
}
