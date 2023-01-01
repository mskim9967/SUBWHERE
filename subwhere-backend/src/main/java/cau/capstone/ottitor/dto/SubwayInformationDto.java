
package cau.capstone.ottitor.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubwayInformationDto {

    // property가 대문자로 시작하면 JsonProperty annotation으로 명시해줘야 매핑 가능
    @JsonProperty("SearchSTNBySubwayLineInfo")
    private SearchSTNBySubwayLineInfo SearchSTNBySubwayLineInfo;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SearchSTNBySubwayLineInfo {

        private List<SubwayInformation> row;

        @Data
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class SubwayInformation {

            @JsonProperty("STATION_CD")
            private String STATION_CD;
            @JsonProperty("STATION_NM")
            private String STATION_NM;
            @JsonProperty("STATION_NM_ENG")
            private String STATION_NM_ENG;
            @JsonProperty("LINE_NUM")
            private String LINE_NUM;
            @JsonProperty("FR_CODE")
            private String FR_CODE;
        }
    }

}