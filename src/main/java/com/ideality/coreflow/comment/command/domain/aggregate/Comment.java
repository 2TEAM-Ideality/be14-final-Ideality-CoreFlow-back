package com.ideality.coreflow.comment.command.domain.aggregate;

import com.ideality.coreflow.project.command.domain.aggregate.Work;
import com.ideality.coreflow.user.command.domain.aggregate.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "comment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String content;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Column(name = "is_notice", nullable = false)
    private boolean isNotice = false;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CommentType type = CommentType.COMMENT;

    // 연관관계: 다대일 (여러 댓글 → 하나의 작업)
    @JoinColumn(name = "work_id", nullable = false)
    private Work work;

    // 연관관계: 다대일 (여러 댓글 → 하나의 유저)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 연관관계: 자기 자신과의 계층 (대댓글)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parent;
}
