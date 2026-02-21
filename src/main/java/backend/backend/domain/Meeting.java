package backend.backend.domain;


import backend.backend.domain.common.BaseEntity;
import backend.backend.domain.enums.MeetingCategory;
import backend.backend.domain.enums.Week;
import backend.backend.domain.mapping.MeetingMember;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Table(name = "MEETING")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class Meeting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "meeting_id")
    private Long id; //primary Key

    private String title;

    private MeetingCategory category;

    private Week week;

    private LocalTime time;

    private int limitNumberOfPeople;

    private String description;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "owner")
    private Member owner;

    @Builder.Default
    @OneToMany(mappedBy = "meeting")
    private List<MeetingMember> members = new ArrayList<>();

    public void join(Member member) {
        MeetingMember meetingMember = new MeetingMember(this, member);
        members.add(meetingMember);
    }
}