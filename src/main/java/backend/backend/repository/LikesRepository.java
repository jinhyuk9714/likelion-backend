package backend.backend.repository;

import backend.backend.domain.Likes;
import backend.backend.domain.Member;
import backend.backend.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {
    Optional<Likes> findByMemberAndPost(Member member, Post post);

    int countLikesByPostId(Long id);
}