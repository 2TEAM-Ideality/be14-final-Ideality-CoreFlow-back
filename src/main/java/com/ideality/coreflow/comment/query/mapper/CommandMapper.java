package com.ideality.coreflow.comment.query.mapper;

import com.ideality.coreflow.comment.query.dto.SelectCommentDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommandMapper {
    List<SelectCommentDTO> selectComments(String taskId);
}
