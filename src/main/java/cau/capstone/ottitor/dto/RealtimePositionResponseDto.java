package cau.capstone.ottitor.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RealtimePositionResponseDto {
    private StationNameDto.StationName statnNm;
    private Integer trainSttus;
    private StationNameDto.StationName statnTnm;
    private Integer directAt;
    private Integer updnLine;
    private StationNameDto prevStatns;
    private StationNameDto nextStatns;
    private Integer subwayHeading;
}
