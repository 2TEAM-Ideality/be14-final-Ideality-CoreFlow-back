package com.ideality.coreflow.project.query.controller;

import com.ideality.coreflow.project.query.dto.HolidayQueryDto;
import com.ideality.coreflow.project.query.service.HolidayQueryService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/holidays")
@RequiredArgsConstructor
public class HolidayQueryController {
    private final HolidayQueryService holidayQueryService;
    @GetMapping
    public ResponseEntity<Map<String,Object>> getHolidaysByYear(@RequestParam int year){
        List<HolidayQueryDto> holidays=holidayQueryService.getHolidaysByYear(year);
        Map<String,Object> response=new HashMap<>();
        response.put("status","success");
        response.put("message",year+"년 휴일 목록 조회 성공");
        response.put("data", Map.of("holidays",holidays));
        return ResponseEntity.ok(response);
    }
}
