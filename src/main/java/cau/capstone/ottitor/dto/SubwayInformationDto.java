package cau.capstone.ottitor.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubwayInformationDto {

    private List<SubwayInformation> row;
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SubwayInformation {
        private String STATION_CD;
	    private String STATION_NM;
	    private String STATION_NM_ENG;
	    private String LINE_NUM;
	    private String FR_CODE;
    }

}
