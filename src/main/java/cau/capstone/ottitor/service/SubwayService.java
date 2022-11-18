package cau.capstone.ottitor.service;

import cau.capstone.ottitor.domain.Station;
import cau.capstone.ottitor.dto.*;
import cau.capstone.ottitor.repository.StationRepository;
import cau.capstone.ottitor.util.GeneralException;
import cau.capstone.ottitor.util.TrainRouteDirection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static cau.capstone.ottitor.constant.Code.SUBWAY_END;

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

        // 해당 호선의 지하철역 리스트를 데이터베이스에서 가져옴.
        List<Station> stations = stationRepository.findByLineNumOrderByFrCodeAsc(0 + subwayNm);

        /*
          해당 호선의 모든 지하철 정보를 가져옴.
         */
        RestTemplate restTemplate = new RestTemplate();
        RealtimePositionDto realtimePositionDto
                = restTemplate.exchange(
                "http://swopenapi.seoul.go.kr/api/subway/" + apiKey + "/json/realtimePosition/0/200/" + subwayNm,
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
            throw new GeneralException(SUBWAY_END);
        }

        /*
          해당 열차에서 지하철역명, 상하행선구분, 종착지하철역명, 급행여부, 열차상태 정보를 가져와서 RealtimePositionResponseDto 객체에 저장.
          열차상태 중 ext 따로 처리해야함.
         */

        boolean find = false;

        // 역 이름에 () 가 들어가는 경우 해당 부분을 삭제처리.
        for (RealtimePositionDto.RealtimePosition dto : realtimePositionDto.getRealtimePositionList()) {
            if (dto.getTrainNo().equals(trainNo)) {
                find = true;

                for (Station station : stations) {
                    if (dto.getStatnNm().contains(station.getNmKor())) {
                        realtimePositionResponseDto.setStatnNm(new StationNameDto.StationName(
                                station.getNmKor(), station.getNmEng()
                        ));
                    }

                    // 종착역이 "성수종착" 인 경우 빈 문자열로 설정.
                    if (dto.getStatnTnm().contains(station.getNmKor())) {
                        if(dto.getStatnTnm().contains("종착"))
                                realtimePositionResponseDto.setStatnTnm(new StationNameDto.StationName(
                                        "", ""));
                        else
                                realtimePositionResponseDto.setStatnTnm(new StationNameDto.StationName(
                                station.getNmKor(), station.getNmEng()));
                    }
                }

                realtimePositionResponseDto.setTrainSttus(Integer.parseInt(dto.getTrainSttus()));
                realtimePositionResponseDto.setDirectAt(Integer.parseInt(dto.getDirectAt()));
                realtimePositionResponseDto.setUpdnLine(Integer.parseInt(dto.getUpdnLine()));
            }
        }

        // 실시간 위치정보에 요청한 지하철 번호가 존재하지않으면 운행 종료 표시.
        if (!find) {
            throw new GeneralException(SUBWAY_END);
        }

        /*
          해당 호선에 해당하는 역 정보들을 가져와 현재 사용자의 역을 기준으로 앞뒤에 있는 역의 리스트를 가져온다.
         */

        TrainRouteDirection trainRouteDirection = new TrainRouteDirection();

        // 해당호선에 필요한 역들을 제외한 나머지 역들을 삭제.
        removeStation(realtimePositionResponseDto.getStatnNm().getKor(),
                realtimePositionResponseDto.getStatnTnm().getKor(),
                realtimePositionResponseDto.getDirectAt(),
                subwayNm, stations, trainRouteDirection);

        // 출발역 또는 도착역이 신정지선 또는 성수지선일 경우 "지선"을 삭제.
        if (realtimePositionResponseDto.getStatnNm().getKor().contains("지선")) {
            realtimePositionResponseDto.setStatnNm(
                    new StationNameDto.StationName(
                            realtimePositionResponseDto.getStatnNm().getKor().replace("지선", ""),
                            realtimePositionResponseDto.getStatnNm().getEng()
                    )
            );
        }

        if (realtimePositionResponseDto.getStatnTnm().getKor().contains("지선")) {
            realtimePositionResponseDto.setStatnTnm(
                    new StationNameDto.StationName(
                            realtimePositionResponseDto.getStatnTnm().getKor().replace("지선", ""),
                            realtimePositionResponseDto.getStatnTnm().getEng()
                    )
            );
        }

        // test 출력.
        for (Station station : stations) {
            System.out.println(station.getNmKor());
        }

        if (trainRouteDirection.isCycle()) {
            // 내선
            if (realtimePositionResponseDto.getUpdnLine() == 0) {
                innerCycleLine(realtimePositionResponseDto, stations, realtimePositionResponseDto.getStatnNm().getKor());
            }

            // 외선
            else {
                outerCycleLine(realtimePositionResponseDto, stations, realtimePositionResponseDto.getStatnNm().getKor());
            }
        }

        // 반대방향
        else if (trainRouteDirection.isReverse()) {

            // 상행일때 하행처럼 작동.
            if (realtimePositionResponseDto.getUpdnLine() == 0)
                topToBottom(realtimePositionResponseDto, stations, realtimePositionResponseDto.getStatnNm().getKor(),
                        realtimePositionResponseDto.getStatnTnm().getKor());

            // 하행일때 상행처럼 작동.
            else
                bottomToTop(realtimePositionResponseDto, stations, realtimePositionResponseDto.getStatnNm().getKor(),
                        realtimePositionResponseDto.getStatnTnm().getKor());
        }

        else {

            // 하행
            if (realtimePositionResponseDto.getUpdnLine() == 1)
                topToBottom(realtimePositionResponseDto, stations, realtimePositionResponseDto.getStatnNm().getKor(),
                        realtimePositionResponseDto.getStatnTnm().getKor());

            // 상행
            else
                bottomToTop(realtimePositionResponseDto, stations, realtimePositionResponseDto.getStatnNm().getKor(),
                        realtimePositionResponseDto.getStatnTnm().getKor());

        }


        /*
          역 이름을 이용하여 해당 역의 실시간 도착정보를 가져온 후 열차번호를 이용하여 열차를 뽑아낸다 그 후 해당 열차가 해당 역에서 어떤방향으로 문이 열리는지 가져온다.
         */
        RealtimeArrivalDto realtimeArrivalDto =
                restTemplate.exchange(
                        "http://swopenAPI.seoul.go.kr/api/subway/" + apiKey + "/json/realtimeStationArrival/0/100/" + realtimePositionResponseDto.getStatnNm().getKor(),
                        HttpMethod.GET,
                        null,
                        RealtimeArrivalDto.class
                ).getBody();

        assert realtimeArrivalDto != null;

        // 실시간 열차 도착정보 api 로 받아올 수 없는 역들이 존재함. 이 역들에 대한 api 요청을 하면 list 가 null 이 됨.
        if (realtimeArrivalDto.getRealtimeArrivalList() == null) {
            System.out.println("지하철 도착 list 가 비었음.");

            return realtimePositionResponseDto;
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

    /**
     * 현재역이 stations 어디 위치에 있는지 index 설정.
     */
    int getStationIndex(List<Station> stations, String station) {
        for (int i = 0; i < stations.size(); i++) {
            if (stations.get(i).getNmKor().equals(station)) {
                return i;
            }
        }

        // 2호선 순환열차일경우 endStationIndex 를 그냥 0으로 설정. (어차피 사용 x)
        return 0;
    }

    /**
     * 출발지와 종착지를 고려하여 필요한 역들만 남기고 나머지역들을 제거하는 함수.
     * @param curStation : 현재 역 이름.
     * @param endStation : 종착역 이름.
     * @param directAt : 급행 여부.
     * @param subwayNm : 호선명
     * @param stations : 역 리스트
     */
    void removeStation(String curStation, String endStation, int directAt, String subwayNm,
                       List<Station> stations, TrainRouteDirection trainRouteDirection) {

        int curStationIndex = getStationIndex(stations, curStation);
        int endStationIndex = getStationIndex(stations, endStation);

        String curStationFrCode = stations.get(curStationIndex).getFrCode();
        String endStationFrCode = stations.get(endStationIndex).getFrCode();

        // 급행열차이면 급행이 정차하는 역이 아닌 역들을 삭제. 열차의 현재역이 급행 정차역이 아닌 경우가 있을 수 있음. 따라서 현재 역은 남기고 삭제.
        if (directAt == 1) {
            stations.removeIf(station -> station.getDirectAt() == 0 && !station.getNmKor().equals(curStation));

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
            trainRouteDirection.setReverse(true);

            System.out.println("<신정지선>");
        }

        // 2호선 순환인경우.
        else if(subwayNm.equals("2호선")){
            stations.removeIf(station -> station.getFrCode().contains("-"));
            trainRouteDirection.setCycle(true);

            System.out.println("<순환>");
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
    }

    /**
     * 해당역을 기준으로 다음 도착역들의 도착시간을 반환하는 함수. /arrival
     * 구현 미완료.
     */
    public Object getArrivalTime(String subwayNm, String trainNo) {

        ArrivalTimeResponseDto arrivalTimeResponseDto = new ArrivalTimeResponseDto();
        List<Station> stations = stationRepository.findByLineNumOrderByFrCodeAsc(0 + "subwayNm");

        /*
          해당 호선의 모든 지하철 정보를 가져옴.
         */
        RestTemplate restTemplate = new RestTemplate();
        RealtimePositionDto realtimePositionDto
                = restTemplate.exchange(
                "http://swopenapi.seoul.go.kr/api/subway/" + apiKey + "/json/realtimePosition/0/1000/" + subwayNm,
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
            throw new GeneralException(SUBWAY_END);
        }

        boolean find = false;

        String curStation = "";
        String endStation = "";
        int directAt = 0;
        int updnLine = 0;

        // 현재역과 종착역을 설정.
        for (RealtimePositionDto.RealtimePosition dto : realtimePositionDto.getRealtimePositionList()) {
            if (dto.getTrainNo().equals(trainNo)) {
                find = true;

                curStation = dto.getSubwayNm();
                endStation = dto.getStatnTnm();
                directAt = Integer.parseInt(dto.getDirectAt());
                updnLine = Integer.parseInt(dto.getUpdnLine());
            }
        }

        // 실시간 위치정보에 요청한 지하철 번호가 존재하지않으면 운행 종료 표시.
        if (!find) {
            throw new GeneralException(SUBWAY_END);
        }

        TrainRouteDirection trainRouteDirection = new TrainRouteDirection();

        removeStation(curStation, endStation, directAt, subwayNm, stations, trainRouteDirection);
        List<ArrivalTimeResponseDto.ArrivalTime> arrivalTimeList = new ArrayList<>();

        return null;
    }


    /**
     * 위에서 아래로 훑으면서 도착예정시간을 계산하는 함수.
     * @param curStation : 현재역
     * @param endStation : 종착역
     * @param arrivalTimeList : 역 이름과 도착예정시간을 담고있는 list.
     * 구현 미완료.
     */
    void bottomUpGetNextStation(String curStation,
                                   String endStation,
                                   List<Station> stations,
                                   List<ArrivalTimeResponseDto.ArrivalTime> arrivalTimeList) {

        int curStationIndex = getStationIndex(stations, curStation);
        int endStationIndex = getStationIndex(stations, endStation);
        int size = stations.size();

        for (int i = curStationIndex - 1; i >= endStationIndex; i--) {
            arrivalTimeList.add(new ArrivalTimeResponseDto.ArrivalTime(
                    new StationNameDto.StationName(stations.get(i % size).getNmKor(), stations.get(i % size).getNmEng()),
                    stations.get(i % size).getMnt()
            ));
        }
    }

    /**
     * 아래에서 위로 훑으면서 도착예정시간을 계산하는 함수.
     * @param curStation : 현재역
     * @param endStation : 종착역
     * @param arrivalTimeList : 역 이름과 도착예정시간을 담고있는 list.
     * 구현 미완료.
     */
    void topDownGetNextStation(String curStation,
                                   String endStation,
                                   List<Station> stations,
                                   List<ArrivalTimeResponseDto.ArrivalTime> arrivalTimeList) {

        int curStationIndex = getStationIndex(stations, curStation);
        int endStationIndex = getStationIndex(stations, endStation);
        int size = stations.size();

        int arrivalTimeSum = 0;



        // 순환 따로 처리.

        for (int i = curStationIndex + 1; i <= endStationIndex; i++) {
            arrivalTimeList.add(new ArrivalTimeResponseDto.ArrivalTime(
                    new StationNameDto.StationName(stations.get(i % size).getNmKor(), stations.get(i % size).getNmEng()),
                    stations.get(i % size).getMnt() + arrivalTimeSum
            ));

            arrivalTimeSum += stations.get(i).getMnt();
        }
    }

    /**
     * 호선명으로 실시간지하철위치 가져오는 테스트
     */
    public Object realtimePositionApi(String subwayNm) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(
                "http://swopenapi.seoul.go.kr/api/subway/" + apiKey + "/json/realtimePosition/0/200/" + subwayNm,
                HttpMethod.GET,
                null,
                RealtimePositionDto.class
        ).getBody();
    }

    /**
     * 역이름으로 해당 역에 들어오는 실시간 열차정보 가져오는 테스트
     */
    public Object subwayArrivalApi(String statnNm) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(
                "http://swopenAPI.seoul.go.kr/api/subway/" + apiKey + "/json/realtimeStationArrival/0/100/" + statnNm,
                HttpMethod.GET,
                null,
                RealtimeArrivalDto.class
        ).getBody();
    }

    /**
     * 호선을 넘겨받아 해당 호선에 운행하는 열차리스트 중 가장 오래 운행할만한 열차를 뽑아 해당 열차로 실시간 정보 반환.
     */
    public Object testRealtimePositionTemp(String subwayNm) {
        RealtimePositionDto realtimePositionDto = (RealtimePositionDto) realtimePositionApi(subwayNm);

        List<Station> stations = stationRepository.findByLineNumOrderByFrCodeAsc(0 + subwayNm);
        Map<RealtimePositionDto.RealtimePosition, Integer> map = new HashMap<>();

        for (RealtimePositionDto.RealtimePosition realtimePosition : realtimePositionDto.getRealtimePositionList()) {
            String curStation = realtimePosition.getSubwayNm();
            String endStation = realtimePosition.getStatnTnm();

            int curStationIndex = getStationIndex(stations, curStation);
            int endStationIndex = getStationIndex(stations, endStation);

            map.put(realtimePosition, Math.abs(endStationIndex - curStationIndex));
        }

        List<Map.Entry<RealtimePositionDto.RealtimePosition, Integer>> list = new LinkedList<>(map.entrySet());
        list.sort(((o1, o2) -> o2.getValue() - o1.getValue()));
        return getRealTimeSubway(subwayNm, list.get(0).getKey().getTrainNo());
    }
}