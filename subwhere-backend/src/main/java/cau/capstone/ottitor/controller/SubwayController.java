package cau.capstone.ottitor.controller;

import cau.capstone.ottitor.dto.DataResponseDto;
import cau.capstone.ottitor.service.SubwayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/subway")
@Slf4j
public class SubwayController {

    private final SubwayService subwayService;

    @GetMapping(path = "")
    public DataResponseDto<Object> getRealtimeSubway(
            @RequestParam String subwayNm,
            @RequestParam String trainNo
            ) {

        return DataResponseDto.of(subwayService.getRealTimeSubway(subwayNm, trainNo));
    }

    // 다음 도착역들에 대한 도착예정시간 반환.
    @GetMapping(path = "/arrival")
    public DataResponseDto<Object> getSubwayArrivalTime(@RequestParam String subwayNm, String trainNo) {
        return DataResponseDto.of(subwayService.getArrivalTime(subwayNm, trainNo));
    }

    @GetMapping(path = "/realtime")
    public DataResponseDto<Object> getRealTimeSubwayTotal(@RequestParam String subwayNm) {
        return DataResponseDto.of(subwayService.realtimePositionApi(subwayNm));
    }

    @GetMapping(path = "/station")
    public DataResponseDto<Object> getRealtimeArrivalStation(@RequestParam String statnNm) {
        return DataResponseDto.of(subwayService.subwayArrivalApi(statnNm));
    }
}