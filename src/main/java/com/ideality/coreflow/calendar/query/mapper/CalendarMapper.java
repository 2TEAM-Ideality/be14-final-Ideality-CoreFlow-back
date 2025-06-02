package com.ideality.coreflow.calendar.query.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.ideality.coreflow.calendar.query.dto.ResponsePersonalDTO;

@Mapper
public interface CalendarMapper {
	ResponsePersonalDTO selectAllPersonal(Long memberId);
}
