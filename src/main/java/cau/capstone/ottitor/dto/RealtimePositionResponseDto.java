package cau.capstone.ottitor.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RealtimePositionResponseDto {

    private String statnNm;
    private Integer trainSttus;
    private String statnTnm;
    private Integer directAt;
    private Integer updnLine;
    private List<SubwayNameDto.SubwayName> prevStatns;
    private List<SubwayNameDto.SubwayName> nextStatns;
    private Integer subwayHeading;
}
