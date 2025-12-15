package com.example.team_mate.domain.poll.poll.entity;

import com.example.team_mate.domain.post.post.entity.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Poll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;      // 투표 제목
    private LocalDate endDate; // 투표 마감일

    private boolean allowMultiple; // 중복 선택 허용 여부

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PollOption> options = new ArrayList<>();

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PollVote> votes = new ArrayList<>();

    public Poll(String title, LocalDate endDate, boolean allowMultiple) {
        this.title = title;
        this.endDate = endDate;
        this.allowMultiple = allowMultiple;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public void addOption(PollOption option) {
        this.options.add(option);
        option.setPoll(this);
    }
}