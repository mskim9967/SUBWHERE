package cau.capstone.ottitor.service;

import cau.capstone.ottitor.domain.Station;
import cau.capstone.ottitor.dto.*;
import cau.capstone.ottitor.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class SubwayService {
    private final StationRepository stationRepository;

    @Value("${api.key}")
    private String apiKey;

    public Object getRealTimeSubway(String subwayNm, String trainNo) {

        RealtimePositionResponseDto realtimePositionResponseDto = new RealtimePositionResponseDto();

        /*
          해당 호선의 모든 지하철 정보를 가져옴.
         */
        RestTemplate restTemplate = new RestTemplate();
        RealtimePositionDto realtimePositionDto
                = restTemplate.exchange(
                "http://swopenapi.seoul.go.kr/api/subway/" + apiKey + "/json/realtimePosition/0/100/" + subwayNm,
                HttpMethod.GET,
                null,
                RealtimePositionDto.class
        ).getBody();

        /*
          RealtimePosition 객체 중 해당하는 열차번호를 가진 열차의 정보를 가져옴.
          해당 호선이 모두 운행을 종료해 getRealtimePositionList() 가 null 이 되면 종료.
         */

        assert realtimePositionDto != null;

        if (realtimePositionDto.getRealtimePositionList() == null) {
            return "해당 열차는 운행이 종료되었습니다.";
        }

        /*
          해당 열차에서 지하철역명, 상하행선구분, 종착지하철역명, 급행여부, 열차상태 정보를 가져와서 RealtimePositionResponseDto 객체에 저장.
          열차상태 중 ext 따로 처리해야함.
         */

        boolean find = false;

        for (RealtimePositionDto.RealtimePosition dto : realtimePositionDto.getRealtimePositionList()) {
            if (dto.getTrainNo().equals(trainNo)) {
                find = true;
                realtimePositionResponseDto.setStatnNm(dto.getStatnNm());
                realtimePositionResponseDto.setTrainSttus(Integer.parseInt(dto.getTrainSttus()));
                realtimePositionResponseDto.setDirectAt(Integer.parseInt(dto.getDirectAt()));
                realtimePositionResponseDto.setStatnTnm(dto.getStatnTnm());
                realtimePositionResponseDto.setUpdnLine(Integer.parseInt(dto.getUpdnLine()));
            }
        }

        // 실시간 위치정보에 요청한 지하철 번호가 존재하지않으면 운행 종료 표시.
        if (!find) {
            return "해당 열차는 운행이 종료되었습니다.";
        }

        /*
          해당 호선에 해당하는 역 정보들을 가져와 현재 사용자의 역을 기준으로 앞뒤에 있는 역의 리스트를 가져온다.
         */

        List<Station> stations = stationRepository.findByLineNumOrderByFrCodeAsc(0 + subwayNm);

        String curStation = realtimePositionResponseDto.getStatnNm();
        String endStation = realtimePositionResponseDto.getStatnTnm();

        int curStationIndex = 0;
        int endStationIndex = 0;

        // 현재역과 종착역에 대한 index 설정.
        for (int i = 0; i < stations.size(); i++) {
            if (stations.get(i).getNmKor().equals(curStation)) {
                curStationIndex = i;
            }

            if (stations.get(i).getNmKor().equals(endStation)) {
                endStationIndex = i;
            }
        }

        String curStationFrCode = stations.get(curStationIndex).getFrCode();
        String endStationFrCode = stations.get(endStationIndex).getFrCode();

        boolean cycle = false;
        boolean reverse = false;

        // 급행열차이면 급행이 정차하는 역이 아닌 역들을 삭제.
        if (realtimePositionResponseDto.getDirectAt() == 1) {
            stations.removeIf(station -> station.getDirectAt() == 0);

            System.out.println("<급행>");
        }

        // 1호선에서 출발지와 도착역 모두 광명역 또는 서동탄역이 아니면, 광명역과 서동탄역을 stations 에서 삭제.
        if (subwayNm.equals("1호선") && !curStationFrCode.contains("-") && !endStationFrCode.contains("-")) {
            stations.removeIf(station -> station.getFrCode().contains("-"));

            System.out.println("<광명역, 서동탄역 제거>");
        }

        // 1호선에서 출발지 또는 도착역에 광명역이 포함된 경우.
        else if (subwayNm.equals("1호선") && (curStationFrCode.contains("144-1") || endStationFrCode.contains("144-1"))) {
            stations.removeIf(station -> getStationIndex(stations, station.getNmKor()) > getStationIndex(stations, "광명"));

            System.out.println("<광명역 이후 역 삭제>");
        }

        // 1호선에서 출발지 또는 도착역에 서동탄역이 포함된 경우.
        else if (subwayNm.equals("1호선") && (curStationFrCode.contains("157-1") || endStationFrCode.contains("157-1"))) {
            stations.removeIf(station -> getStationIndex(stations, station.getNmKor()) > getStationIndex(stations, "서동탄") ||
                    station.getFrCode().contains("144-1"));

            System.out.println("<서동탄역 이후 역 삭제, 광명역 삭제>");
        }

        // 2호선에서 출발지 또는 도착지가 성수지선("211-")일경우 나머지 역들을 삭제.
        if (subwayNm.equals("2호선") && (curStationFrCode.contains("211-") || endStationFrCode.contains("211-")) ||
                (curStation.equals("성수지선") || endStation.equals("성수지선"))) {
            stations.removeIf(station -> !station.getFrCode().contains("211"));

            System.out.println("<성수지선>");
        }

        // 2호선에서 출발지 또는 도착지가 신정지선("234-")일경우 나머지 역들을 삭제.
        else if (subwayNm.equals("2호선") && (curStationFrCode.contains("234-") || endStationFrCode.contains("234-")) ||
                (curStation.equals("신도림지선") || endStation.equals("신도림지선"))) {
            stations.removeIf(station -> !station.getFrCode().contains("234"));
            reverse = true;

            System.out.println("<신정지선>");
        }

        // 2호선 순환인경우.
        else if(subwayNm.equals("2호선")){
            stations.removeIf(station -> station.getFrCode().contains("-"));
            cycle = true;

            // 도착역이 "성수종착" 인경우 도착역을 바꿔줘야함.
            if (realtimePositionResponseDto.getStatnTnm().contains("종착")) {
                realtimePositionResponseDto.setStatnTnm("순환");
            }

            System.out.println("<순환>");
        }

        // 출발역 또는 도착역이 신정지선 또는 성수지선일 경우 "지선"을 삭제.
        if (realtimePositionResponseDto.getStatnNm().contains("지선")) {
            realtimePositionResponseDto.setStatnNm(
                    realtimePositionResponseDto.getStatnNm().replace("지선", "")
            );
        }

        if (realtimePositionResponseDto.getStatnTnm().contains("지선")) {
            realtimePositionResponseDto.setStatnTnm(
                    realtimePositionResponseDto.getStatnTnm().replace("지선", "")
            );
        }

        // 1호선 또는 5호선에서 출발지 또는 도착지의 frCode 가 P를 포함하고 있으면, P가 아닌 분기역들을 stations 에서 삭제.
        if (curStationFrCode.contains("P") || endStationFrCode.contains("P")) {

            for (int i = 0; i < stations.size(); i++) {
                if (stations.get(i).getFrCode().charAt(0) == 'P') {

                    String frCode = stations.get(i).getFrCode().substring(1);
                    stations.removeIf(station -> station.getFrCode().charAt(0) != 'P'
                            && Integer.parseInt(station.getFrCode()) >= Integer.parseInt(frCode));

                    break;
                }
            }

            System.out.println("<P 포함>");
        }

        for (Station station : stations) {
            System.out.println(station.getNmKor());
        }

        if (cycle) {
            // 내선
            if (realtimePositionResponseDto.getUpdnLine() == 0) {
                innerCycleLine(realtimePositionResponseDto, stations, realtimePositionResponseDto.getStatnNm());
            }

            // 외선
            else {
                outerCycleLine(realtimePositionResponseDto, stations, realtimePositionResponseDto.getStatnNm());
            }
        }

        // 반대방향
        else if (reverse) {

            // 상행일때 하행처럼 작동.
            if (realtimePositionResponseDto.getUpdnLine() == 0)
                topToBottom(realtimePositionResponseDto, stations, realtimePositionResponseDto.getStatnNm(),
                        realtimePositionResponseDto.getStatnTnm());

            // 하행일때 상행처럼 작동.
            else
                bottomToTop(realtimePositionResponseDto, stations, realtimePositionResponseDto.getStatnNm(),
                        realtimePositionResponseDto.getStatnTnm());

        }

        else {

            // 하행
            if (realtimePositionResponseDto.getUpdnLine() == 1)
                topToBottom(realtimePositionResponseDto, stations, realtimePositionResponseDto.getStatnNm(),
                        realtimePositionResponseDto.getStatnTnm());

            // 상행
            else
                bottomToTop(realtimePositionResponseDto, stations, realtimePositionResponseDto.getStatnNm(),
                        realtimePositionResponseDto.getStatnTnm());

        }

        /*
          역 이름을 이용하여 해당 역의 실시간 도착정보를 가져온 후 열차번호를 이용하여 열차를 뽑아낸다 그 후 해당 열차가 해당 역에서 어떤방향으로 문이 열리는지 가져온다.
         */
        RealtimeArrivalDto realtimeArrivalDto =
                restTemplate.exchange(
                        "http://swopenAPI.seoul.go.kr/api/subway/" + apiKey + "/json/realtimeStationArrival/0/5/" + realtimePositionResponseDto.getStatnNm(),
                        HttpMethod.GET,
                        null,
                        RealtimeArrivalDto.class
                ).getBody();

        assert realtimeArrivalDto != null;

        // 해당열차가 운행중인데도 가끔식 이 표시가 나오는데, 아마 역 도착하고나서 출발해도 null 이 나와서 그런듯. 처리 필요.
        // 테스트 필요.

        if (realtimeArrivalDto.getRealtimeArrivalList() == null) {
            return "해당 열차는 운행이 종료되었습니다. 236줄";
        }

        // 열차의 문 방향이 왼쪽이면 0, 오른쪽이면 1 저장.
        // 해당 열차가 해당역을 출발했을 때 이미 출발했으므로 해당역에 도착정보가 안나오는 경우가 발생할 수 있음. 예외처리 필요.
        for (RealtimeArrivalDto.RealtimeArrival realtimeArrival : realtimeArrivalDto.getRealtimeArrivalList()) {
            if (realtimeArrival.getBtrainNo().equals(trainNo)) {
                realtimePositionResponseDto.setSubwayHeading((realtimeArrival.getSubwayHeading().equals("왼쪽")) ? 0 : 1);
            }
        }

        return realtimePositionResponseDto;
    }

    /**
     * 하행방향 역 리스트를 가져온다.
     */
    void topToBottom(RealtimePositionResponseDto realtimePositionResponseDto,
                     List<Station> stations, String curStation, String endStation) {

        int curStationIndex = getStationIndex(stations, curStation);

        List<StationNameDto.StationName> prevStatnsList = new ArrayList<>();
        List<StationNameDto.StationName> nextStatnsList = new ArrayList<>();

        // 현재역이 stations 어디 위치에 있는지 index 설정.
        for (int i = 0; i < stations.size(); i++) {
            if (stations.get(i).getNmKor().equals(curStation)) {
                curStationIndex = i;
            }
        }

        // 이전역 리스트 추가
        for (int i = curStationIndex - 1; i >= 0; i--) {

            prevStatnsList.add(0, new StationNameDto.StationName(
                    stations.get(i).getNmKor(), stations.get(i).getNmEng()
            ));

            if (prevStatnsList.size() == 2) {
                break;
            }
        }

        // 다음역 리스트 추가
        for (int i = curStationIndex + 1; i < stations.size(); i++) {

            nextStatnsList.add(new StationNameDto.StationName(
                    stations.get(i).getNmKor(), stations.get(i).getNmEng()
            ));

            if (nextStatnsList.size() == 3) {
                break;
            }

            // 현재 보고있는 역이 종착역이면 종료.
            if (stations.get(i).getNmKor().equals(endStation)) {
                break;
            }
        }

        realtimePositionResponseDto.setPrevStatns(new StationNameDto(prevStatnsList));
        realtimePositionResponseDto.setNextStatns(new StationNameDto(nextStatnsList));
    }

    /**
     * 상행방향 역 리스트를 가져온다. (순환열차에 대한 종착역 처리는 하지 않음.)
     */
    void bottomToTop(RealtimePositionResponseDto realtimePositionResponseDto,
                     List<Station> stations, String curStation, String endStation) {

        int curStationIndex = getStationIndex(stations, curStation);

        List<StationNameDto.StationName> prevStatnsList = new ArrayList<>();
        List<StationNameDto.StationName> nextStatnsList = new ArrayList<>();

        // 이전역 리스트 추가
        for (int i = curStationIndex + 1; i < stations.size(); i++) {
            prevStatnsList.add(0, new StationNameDto.StationName(
                    stations.get(i).getNmKor(), stations.get(i).getNmEng()
            ));

            if (prevStatnsList.size() == 2) {
                break;
            }
        }

        // 다음역 리스트 추가
        for (int i = curStationIndex - 1; i >= 0; i--) {
            nextStatnsList.add(new StationNameDto.StationName(
                    stations.get(i).getNmKor(), stations.get(i).getNmEng()
            ));

            if (nextStatnsList.size() == 3) {
                break;
            }

            // 현재 보고있는 역이 종착역이면 종료.
            if (stations.get(i).getNmKor().equals(endStation)) {
                break;
            }
        }

        realtimePositionResponseDto.setPrevStatns(new StationNameDto(prevStatnsList));
        realtimePositionResponseDto.setNextStatns(new StationNameDto(nextStatnsList));
    }

    /**
     * 외선순환열차의 역 리스트를 가져온다.
     */
    void outerCycleLine(RealtimePositionResponseDto realtimePositionResponseDto,
                        List<Station> stations, String curStation) {

        int curStationIndex = getStationIndex(stations, curStation);

        List<StationNameDto.StationName> prevStatnsList = new ArrayList<>();
        List<StationNameDto.StationName> nextStatnsList = new ArrayList<>();

        int right = curStationIndex;
        int left = curStationIndex;
        int size = stations.size();

        // 이전역 리스트 추가
        while (prevStatnsList.size() < 2) {
            right++;

            prevStatnsList.add(0, new StationNameDto.StationName(
                    stations.get(right % size).getNmKor(), stations.get(right % size).getNmEng()
            ));
        }

        // 다음역 리스트 추가.
        while (nextStatnsList.size() < 3) {
            left--;

            if (left < 0) {
                left = size - 1;
            }

            nextStatnsList.add(new StationNameDto.StationName(
                    stations.get(left).getNmKor(), stations.get(left).getNmEng()
            ));
        }

        realtimePositionResponseDto.setPrevStatns(new StationNameDto(prevStatnsList));
        realtimePositionResponseDto.setNextStatns(new StationNameDto(nextStatnsList));
    }

    /**
     * 내선순환열차의 역 리스트를 가져온다.
     */
    void innerCycleLine(RealtimePositionResponseDto realtimePositionResponseDto,
                        List<Station> stations, String curStation) {

        int curStationIndex = getStationIndex(stations, curStation);

        List<StationNameDto.StationName> prevStatnsList = new ArrayList<>();
        List<StationNameDto.StationName> nextStatnsList = new ArrayList<>();

        int right = curStationIndex;
        int left = curStationIndex;
        int size = stations.size();

        // 이전역 리스트 추가
        while (prevStatnsList.size() < 2) {
            left--;

            if (left < 0) {
                left = size - 1;
            }

            prevStatnsList.add(0, new StationNameDto.StationName(
                    stations.get(left).getNmKor(), stations.get(left).getNmEng()
            ));
        }

        // 다음역 리스트 추가.
        while (nextStatnsList.size() < 3) {
            right++;

            nextStatnsList.add(new StationNameDto.StationName(
                    stations.get(right % size).getNmKor(), stations.get(right % size).getNmEng()
            ));
        }

        realtimePositionResponseDto.setPrevStatns(new StationNameDto(prevStatnsList));
        realtimePositionResponseDto.setNextStatns(new StationNameDto(nextStatnsList));
    }

    // 현재역이 stations 어디 위치에 있는지 index 설정.
    int getStationIndex(List<Station> stations, String station) {
        for (int i = 0; i < stations.size(); i++) {
            if (stations.get(i).getNmKor().equals(station)) {
                return i;
            }
        }

        return -1;
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
    public SubwayInformationDto testSubwayInfo(String subwayNm) {
        RestTemplate restTemplate = new RestTemplate();

        List<Station> stations = stationRepository.findByLineNumOrderByFrCodeAsc("04호선");
        stations.forEach(station -> System.out.println(station.getNmKor() + " " + station.getFrCode() + " " + station.getDirectAt() + " " + station.getMnt()));

        // 요청 한글 인코딩(5호선)
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));


        return restTemplate.exchange(
                // %20은 공백, 공백없이 슬래시 여러개를 붙이면(///) restTemplate 에서 이를 지워버림
                "http://openapi.seoul.go.kr:8088/" + apiKey + "/json/SearchSTNBySubwayLineInfo/1/100/%20/%20/" + subwayNm,
                HttpMethod.GET,
                null,
                SubwayInformationDto.class
        ).getBody();
    }

    /**
     * 역이름으로 해당 역에 들어오는 실시간 열차정보 가져오는 테스트
     */
    public Object testSubwayArrival(String statnNm) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(
                "http://swopenAPI.seoul.go.kr/api/subway/" + apiKey + "/json/realtimeStationArrival/0/5/" + statnNm,
                HttpMethod.GET,
                null,
                RealtimeArrivalDto.class
        ).getBody();
    }
}
