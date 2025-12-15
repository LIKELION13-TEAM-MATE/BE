package com.example.team_mate.domain.post.post.entity;

import com.example.team_mate.domain.comment.comment.entity.Comment;
import com.example.team_mate.domain.member.member.entity.Member;
import com.example.team_mate.domain.project.project.entity.Project;
import com.example.team_mate.domain.file.file.entity.AttachedFile;
import com.example.team_mate.domain.poll.poll.entity.Poll;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;   // 제목

    @Column(columnDefinition = "TEXT")
    private String content; // 내용

    @CreatedDate
    private LocalDateTime createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member author; // 작성자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project; // 어느 프로젝트의 게시판인지

    // 파일 첨부
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AttachedFile> attachedFiles = new ArrayList<>(); // 게시글에 첨부된 파일들

    // 투표
    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Poll poll; // 게시글에 연결된 투표

    private boolean pinned;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public Post(String title, String content, Member author, Project project) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.project = project;
    }

    // 날짜 세팅
    public String getFormattedDate() {
        if (createdDate == null) return "";
        return createdDate.format(DateTimeFormatter.ofPattern("MM/dd HH:mm"));
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
        if (poll != null) {
            poll.setPost(this); // 양방향 연관관계 설정
        }
    }

    // 고정 상태 변경
    public void togglePin() {
        this.pinned = !this.pinned; // true <-> false 반전
    }

    // 게시물 수정
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}