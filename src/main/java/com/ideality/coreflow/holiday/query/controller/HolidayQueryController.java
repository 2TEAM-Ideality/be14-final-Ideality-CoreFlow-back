package com.ideality.coreflow.holiday.query.controller;

import com.ideality.coreflow.holiday.query.dto.HolidayQueryDto;
import com.ideality.coreflow.holiday.query.service.HolidayQueryService;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/holidays")
@RequiredArgsConstructor
public class HolidayQueryController {
    private final HolidayQueryService holidayQueryService;

    @GetMapping("/check")
    public ResponseEntity<Map<String,Object>> isHoliday(@RequestParam("date")
                                                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        boolean isHoliday=holidayQueryService.isHoliday(date);
        Map<String,Object> response=new HashMap<>();
        response.put("status","success");
        response.put("message", date.toString()+" 휴일 여부 조회 성공");
        response.put("data", Map.of("isHoliday", isHoliday));
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Map<String,Object>> getHolidays(){
        List<HolidayQueryDto> holidays=holidayQueryService.getHolidays();
        Map<String,Object> response=new HashMap<>();
        response.put("status","success");
        response.put("message", "전체 휴일 목록 조회 성공");
        response.put("data", Map.of("holidays", holidays));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/year")
    public ResponseEntity<Map<String,Object>> getHolidaysByYear(@RequestParam int year){
        List<HolidayQueryDto> holidays=holidayQueryService.getHolidaysByYear(year);
        Map<String,Object> response=new HashMap<>();
        response.put("status","success");
        response.put("message",year+"년 휴일 목록 조회 성공");
        response.put("data", Map.of("holidays",holidays));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/month")
    public ResponseEntity<Map<String,Object>> getHolidaysByMonth(@RequestParam int year, @RequestParam int month){
        List<HolidayQueryDto> holidays=holidayQueryService.getHolidaysByMonth(year, month);
        Map<String,Object> response=new HashMap<>();
        response.put("status","success");
        response.put("message",year+"년 "+month+"월 휴일 목록 조회 성공");
        response.put("data", Map.of("holidays",holidays));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/range")
    public ResponseEntity<Map<String,Object>> getHolidaysByRange(@RequestParam
                                                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                                 LocalDate start,
                                                                 @RequestParam
                                                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                                 LocalDate end){
            List<HolidayQueryDto> holidays=holidayQueryService.getHolidaysBetween(start, end);
            Map<String,Object> response=new HashMap<>();
            response.put("status","success");
            response.put("message", start.toString()+" ~ "+end.toString()+" 휴일 목록 조회 성공");
            response.put("data", Map.of("holidays",holidays));
            return ResponseEntity.ok(response);
    }


}
