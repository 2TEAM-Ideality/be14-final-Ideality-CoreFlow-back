package com.ideality.coreflow.comment.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class
Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String content;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    @Column(name = "is_notice", nullable = false)
    @Builder.Default
    private boolean isNotice = false;

    @Column(name = "is_modify", nullable = false)
    @Builder.Default
    private boolean isModify = false;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CommentType type = CommentType.COMMENT;

    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "work_id", nullable = false)
    private Long workId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "parent_comment_id")
    private Long parentCommentId;


    public void updateDeleted() {
        this.isDeleted = true;
    }

    public void updateComment(String content, Boolean isNotice) {

        if (content == null) {
            return;
        }
        this.content = content;
        this.isNotice = isNotice;
        this.isModify = true;

        if (isNotice) {
            this.type = CommentType.NOTICE;
        }
    }

    public void updateNotice() {
        this.isNotice = true;
        this.type = CommentType.NOTICE;
    }
}