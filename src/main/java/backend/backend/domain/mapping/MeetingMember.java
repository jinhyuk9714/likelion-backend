package backend.backend.domain.mapping;

import backend.backend.domain.Meeting;
import backend.backend.domain.Member;
import backend.backend.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
@Entity
@RequiredArgsConstructor
public class MeetingMember extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public MeetingMember(Meeting meeting, Member member) {
        this.meeting = meeting;
        this.member = member;
    }
}
