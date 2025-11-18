package com.example.team_mate.domain.comment.comment.entity;

import com.example.team_mate.domain.member.member.entity.Member;
import com.example.team_mate.domain.post.post.entity.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content; // 댓글 내용

    @CreatedDate
    private LocalDateTime createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;   // 어떤 게시글의 댓글인지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member author; // 작성자

    public Comment(String content, Post post, Member author) {
        this.content = content;
        this.post = post;
        this.author = author;
    }

    // 날짜 포맷팅
    public String getFormattedDate() {
        if (createdDate == null) return "";
        return createdDate.format(DateTimeFormatter.ofPattern("MM/dd HH:mm"));
    }
}