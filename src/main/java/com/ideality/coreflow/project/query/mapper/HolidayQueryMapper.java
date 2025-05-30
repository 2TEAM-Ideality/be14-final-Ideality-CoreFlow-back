package com.ideality.coreflow.project.query.mapper;

import com.ideality.coreflow.project.query.dto.HolidayQueryDto;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface HolidayQueryMapper {
    List<HolidayQueryDto> findByYear(@Param("year") int year);
}
