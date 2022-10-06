package cau.capstone.ottitor.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubwayNameDto {

    private List<SubwayName> subwayNameList;
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SubwayName {
        private String kor;
        private String eng;
    }
}