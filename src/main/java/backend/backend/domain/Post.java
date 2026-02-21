package backend.backend.domain;


import backend.backend.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE post SET active_status = 'DELETED' WHERE post_id = ? AND active_status <> 'DELETED'")
@SQLRestriction("active_status <> 'DELETED'")
@Entity(name = "post")
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String title;

    public Post(String title, String content, Member member) {
        this.member = member;
        this.content = content;
        this.title = title;
    }

    public void update(String title, String content){
        this.title = title;
        this.content = content;
    }

}
