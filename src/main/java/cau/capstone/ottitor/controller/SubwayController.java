package cau.capstone.ottitor.controller;

import cau.capstone.ottitor.dto.DataResponseDto;
import cau.capstone.ottitor.service.SubwayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/subway")
@Slf4j
public class SubwayController {

    private final SubwayService subwayService;

    @GetMapping(path = "")
    public DataResponseDto<Object> getRealtimeSubway(String subwayNm, String trainNo) {
        return DataResponseDto.of(subwayService.getRealTimeSubway(subwayNm, trainNo));
    }

    @GetMapping(path = "/realtime")
    public DataResponseDto<Object> getRealTimeSubwayTotal(String subwayNm) {
        return DataResponseDto.of(subwayService.testRealtimePosition(subwayNm));
    }

    @GetMapping(path = "/info")
    public DataResponseDto<Object> getSubwayInformation(String subwayNm) {
        return DataResponseDto.of(subwayService.testSubwayInfo(subwayNm));
    }
}