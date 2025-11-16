package com.example.team_mate.domain.role.role.entity;

import com.example.team_mate.domain.member.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "project_role_assignment",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"project_role_id", "member_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectRoleAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 역할인지
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_role_id")
    private ProjectRole projectRole;

    // 어떤 멤버에게 배정됐는지
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;
}
