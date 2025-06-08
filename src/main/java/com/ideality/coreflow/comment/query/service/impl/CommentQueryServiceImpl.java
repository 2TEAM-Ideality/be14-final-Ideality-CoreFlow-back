package com.ideality.coreflow.comment.query.service.impl;

import com.ideality.coreflow.comment.query.dto.ResponseCommentDTO;
import com.ideality.coreflow.comment.query.dto.SelectCommentDTO;
import com.ideality.coreflow.comment.query.mapper.CommandMapper;
import com.ideality.coreflow.comment.query.service.CommentQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentQueryServiceImpl implements CommentQueryService {
    private final CommandMapper commandMapper;

    @Override
    public List<ResponseCommentDTO> selectComments(String taskId) {
        List<SelectCommentDTO> selectCommentList = commandMapper.selectComments(taskId);
        List<ResponseCommentDTO> res = new ArrayList<>();
        for (SelectCommentDTO selectCommentDTO : selectCommentList) {
            ResponseCommentDTO responseCommentDTO = new ResponseCommentDTO();
            String resName = selectCommentDTO.getDeptName() + "_" + selectCommentDTO.getJobRankName() + "_" +
                    selectCommentDTO.getName();
            responseCommentDTO.setCommentId(selectCommentDTO.getCommentId());
            responseCommentDTO.setParentCommentId(selectCommentDTO.getParentCommentId());
            responseCommentDTO.setCommentWriter(resName);
            responseCommentDTO.setContent(selectCommentDTO.getContent());
            res.add(responseCommentDTO);
        }
        return res;
    }
}
