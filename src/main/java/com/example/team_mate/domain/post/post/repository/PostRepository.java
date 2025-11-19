package com.example.team_mate.domain.post.post.repository;

import com.example.team_mate.domain.post.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    // 최신순으로 정렬
    List<Post> findAllByProjectIdOrderByPinnedDescCreatedDateDesc(Long projectId);
}