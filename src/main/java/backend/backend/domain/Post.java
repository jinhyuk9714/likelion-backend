package backend.backend.domain;


import backend.backend.domain.common.BaseEntity;
import backend.backend.domain.dto.postDto.PostRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@NoArgsConstructor
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


    public Post(PostRequestDto postRequestDto, Member member) {
        this.member = member;
        this.content = postRequestDto.getContent();
        this.title = postRequestDto.getTitle();
    }

    public void update(PostRequestDto postRequestDto){
        this.title = postRequestDto.getTitle();
        this.content = postRequestDto.getContent();
    }

}
