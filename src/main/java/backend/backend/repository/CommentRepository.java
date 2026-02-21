package backend.backend.repository;

import backend.backend.domain.Comment;
import backend.backend.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {


    Optional<Comment> findByPostAndId(Post post, Long id);

    @Query("SELECT c FROM Comment c WHERE c.parent.id = :parentId AND c.activeStatus <> 'DELETED'")
    List<Comment> findChildrenComments(@Param("parentId") Long parentId);

    int countCommentByPostId(Long postId);
}