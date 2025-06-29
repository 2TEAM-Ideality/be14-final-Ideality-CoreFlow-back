package com.ideality.coreflow.comment.query.mapper;

import com.ideality.coreflow.attachment.query.dto.GetTaskInfoDTO;
import com.ideality.coreflow.comment.query.dto.ResponseCommentForModifyDTO;
import com.ideality.coreflow.comment.query.dto.SelectCommentDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    List<SelectCommentDTO> selectComments(Long taskId);

    ResponseCommentForModifyDTO selectCommentByModify(Long commentId);

    List<SelectCommentDTO> selectNotices(Long taskId);

    List<Long> selectAllComments(Long taskId);

    List<GetTaskInfoDTO> selectAllCommentsByList(Long taskIdList);
}
