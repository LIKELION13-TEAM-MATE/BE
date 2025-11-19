package com.example.team_mate.domain.poll.poll.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PollOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String optionText;
    private int votes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id")
    private Poll poll; // 이 보기가 어떤 투표의 보기인지

    public PollOption(String optionText) {
        this.optionText = optionText;
        this.votes = 0; // 초기 득표수는 0
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }

    // 득표수 증가
    public void addVote() {
        this.votes++;
    }
}