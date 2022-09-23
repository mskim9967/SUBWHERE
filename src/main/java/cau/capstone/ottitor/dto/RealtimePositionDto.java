package cau.capstone.ottitor.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RealtimePositionDto {

    private List<RealTimePosition> realtimePositionList;
    private LocalDateTime currentTIme;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RealTimePosition {
        private String beginRow;
        private String endRow;
        private String curPage;
        private String pageRow;
        private Integer totalCount;
        private Integer rowNum;
        private Integer selectedCount;
        private String subwayId;
        private String subwayNm;
        private String statnId;
        private String statnNm;
        private String trainNo;
        private String lastRecptnDt;
        private String recptnDt;
        private String updnLine;
        private String statnTid;
        private String statnTnm;
        private String trainSttus;
        private String directAt;
        private String lstcarAt;
    }
}
