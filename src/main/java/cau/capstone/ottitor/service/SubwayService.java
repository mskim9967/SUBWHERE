package cau.capstone.ottitor.service;


import cau.capstone.ottitor.dto.RealtimePositionDto;
import cau.capstone.ottitor.dto.RealtimePositionResponseDto;
import cau.capstone.ottitor.dto.SubwayInformationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class SubwayService {

    @Value("${api-key}")
    private String apiKey;

    public Object getRealTimeSubway(String subwayNm, String trainNo) {

        RealtimePositionResponseDto realtimePositionResponseDto = new RealtimePositionResponseDto();

        /**
         * 해당 호선의 모든 지하철 정보를 가져옴.
         */
        RestTemplate restTemplate = new RestTemplate();
        RealtimePositionDto realtimePositionDto
                = restTemplate.exchange(
                "http://swopenapi.seoul.go.kr/api/subway/" + apiKey + "/json/realtimePosition/0/100/" + subwayNm,
                HttpMethod.GET,
                null,
                RealtimePositionDto.class
        ).getBody();


        /**
         * RealtimePosition 객체 중 해당하는 열차번호를 가진 열차의 정보를 가져옴.
         * 해당 열차번호가 존재하지 않을 때, 예외처리 필요
         */

        assert realtimePositionDto != null;

        /**
         * 해당 열차에서 지하철역명, 상하행선구분, 종착지하철역명, 급행여부, 열차상태 정보를 가져와서 RealtimePositionResponseDto 객체에 저장.
         */
        for (RealtimePositionDto.RealtimePosition dto : realtimePositionDto.getRealtimePositionList()) {
            if (dto.getTrainNo().equals(trainNo)) {
                realtimePositionResponseDto.setStatnNm(dto.getStatnNm());
                realtimePositionResponseDto.setTrainSttus(Integer.parseInt(dto.getTrainSttus()));
                realtimePositionResponseDto.setDirectAt(Integer.parseInt(dto.getDirectAt()));
                realtimePositionResponseDto.setStatnTnm(dto.getStatnTnm());
                realtimePositionResponseDto.setUpdnLine(Integer.parseInt(dto.getUpdnLine()));
            }
        }

//        List<String> prevStatns = new ArrayList<>();
//        List<String> nextStatns = new ArrayList<>();



        return null;
    }

    /**
     * 호선명으로 실시간지하철위치 가져오는 테스트
     */
    public Object testRealtimePosition(String subwayNm) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(
                "http://swopenapi.seoul.go.kr/api/subway/" + apiKey + "/json/realtimePosition/0/100/" + subwayNm,
                HttpMethod.GET,
                null,
                RealtimePositionDto.class
        ).getBody();
    }

    /**
     * 호선명으로 지하철역정보 가져오는 테스트
     */
    public Object testSubwayInfo(String subwayNm) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(
                "http://openapi.seoul.go.kr:8088/" + apiKey + "/json/SearchSTNBySubwayLineInfo/1/100///" + subwayNm,
                HttpMethod.GET,
                null,
                SubwayInformationDto.class
        ).getBody();
    }
}
