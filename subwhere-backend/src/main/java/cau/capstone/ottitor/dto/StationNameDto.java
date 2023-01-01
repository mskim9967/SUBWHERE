package cau.capstone.ottitor.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StationNameDto {

    private List<StationName> stationNameList;

    public StationNameDto(List<StationName> stationNameList) {
        this.stationNameList = stationNameList;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class StationName {
        private String kor;
        private String eng;
        public StationName(String nmKor, String nmEng) {
            kor = nmKor;
            eng = nmEng;
        }
    }
}