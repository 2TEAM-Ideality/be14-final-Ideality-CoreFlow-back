package com.ideality.coreflow.calendar.query.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.ideality.coreflow.calendar.query.dto.ResponsePersonalDTO;

@Mapper
public interface CalendarMapper {
	List<ResponsePersonalDTO> selectAllPersonal(Long memberId);

	ResponsePersonalDTO selectPersonalDetail(Map<String, Object> param);
}
