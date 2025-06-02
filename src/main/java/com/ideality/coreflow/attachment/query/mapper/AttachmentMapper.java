package com.ideality.coreflow.attachment.query.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.ideality.coreflow.attachment.query.dto.ResponseAttachmentDTO;

@Mapper
public interface AttachmentMapper {

	ResponseAttachmentDTO selectUrl(Map<String, Object> paramMap);
}
