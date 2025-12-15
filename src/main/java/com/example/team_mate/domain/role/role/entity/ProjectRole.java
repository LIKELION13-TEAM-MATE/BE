package com.example.team_mate.domain.role.role.entity;

import com.example.team_mate.domain.project.project.entity.Project;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "project_role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 프로젝트의 역할인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    // 역할 이름
    @Column(nullable = false)
    private String name;
}
