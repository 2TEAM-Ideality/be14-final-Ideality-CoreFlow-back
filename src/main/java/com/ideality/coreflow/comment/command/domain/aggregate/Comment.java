package com.ideality.coreflow.comment.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CommentType type = CommentType.COMMENT;

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

        if (isNotice) {
            this.type = CommentType.NOTICE;
        }
    }
}