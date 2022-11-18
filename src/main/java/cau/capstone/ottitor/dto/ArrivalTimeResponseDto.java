package cau.capstone.ottitor.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArrivalTimeResponseDto {

    private List<ArrivalTime> arrivalTimeList;
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ArrivalTime {
        private StationNameDto.StationName statnNm;
        private Integer arrivalTime;

        public ArrivalTime(StationNameDto.StationName statnNm, Integer arrivalTime) {
            this.statnNm = statnNm;
            this.arrivalTime = arrivalTime;
        }
    }
}
