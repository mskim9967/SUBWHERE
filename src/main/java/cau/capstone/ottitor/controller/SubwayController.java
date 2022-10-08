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
    public DataResponseDto<Object> getRealtimeSubway(@RequestParam String subwayNm, @RequestParam String trainNo) {
        return DataResponseDto.of(subwayService.getRealTimeSubway(subwayNm, trainNo));
    }

    @GetMapping(path = "/realtime")
    public DataResponseDto<Object> getRealTimeSubwayTotal(@RequestParam String subwayNm) {
        return DataResponseDto.of(subwayService.testRealtimePosition(subwayNm));
    }

//    @GetMapping(path = "/info")
//    public DataResponseDto<Object> getSubwayInformation(@RequestParam String subwayNm) {
//        return DataResponseDto.of(subwayService.testSubwayInfo(subwayNm));
//    }

    @GetMapping(path = "/arrival")
    public DataResponseDto<Object> getSubwayArrival(@RequestParam String statnNm) {
        return DataResponseDto.of(subwayService.testSubwayArrival(statnNm));
    }
}