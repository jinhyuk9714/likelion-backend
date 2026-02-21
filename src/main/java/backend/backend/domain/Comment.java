package backend.backend.domain;

import backend.backend.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE comment SET active_status = 'DELETED' WHERE comment_id = ? AND active_status <> 'DELETED'")
@SQLRestriction("active_status <> 'DELETED'")
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Column(nullable = false)
    private String nickname;

    @JoinColumn(name = "post_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @JoinColumn(name="parent_comment_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Comment parent;

    @Column(nullable = false)
    private int depth;

    @Builder.Default
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> children = new ArrayList<>();

    @Column(length = 100, nullable = false)
    private String content;

    public Comment(String content, String nickname, Post post, Member member){
        this.content = content;
        this.nickname = nickname;
        this.member = member;
        this.post = post;
        this.depth = 0;
    }

    public Comment(String content, String nickname, Post post, Member member, Comment parent){
        this.content = content;
        this.nickname = nickname;
        this.member = member;
        this.post = post;
        this.depth = 1;
        this.parent = parent;
    }

    public void update(String content){
        this.content = content;
    }

}