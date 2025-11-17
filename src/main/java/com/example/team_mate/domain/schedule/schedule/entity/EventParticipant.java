package com.example.team_mate.domain.schedule.schedule.entity;

import com.example.team_mate.domain.member.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "event_participant",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"event_id", "member_id"})
        }
)
public class EventParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 일정인지
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id")
    private Event event;

    // 어떤 멤버가 참여자인지
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    public EventParticipant(Event event, Member member) {
        this.event = event;
        this.member = member;
        event.addParticipant(this);
    }
}
