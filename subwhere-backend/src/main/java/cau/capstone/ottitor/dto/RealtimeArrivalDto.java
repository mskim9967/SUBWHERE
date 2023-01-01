package cau.capstone.ottitor.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RealtimeArrivalDto {

    private List<RealtimeArrival> realtimeArrivalList;
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RealtimeArrival {
        private String beginRow;
        private String endRow;
        private String curPage;
        private String pageRow;
        private Integer totalCount;
        private Integer rowNum;
        private Integer selectedCount;
        private String subwayId;
        private String subwayNm;
        private String updnLine;
        private String trainLineNm;
        private String subwayHeading;
        private String statnFid;
        private String statnTid;
        private String statnId;
        private String statnNm;
        private String ordkey;
        private String subwayList;
        private String statnList;
        private String btrainSttus;
        private String barvlDt;
        private String btrainNo;
        private String bstatnId;
        private String bstatnNm;
        private String recptnDt;
        private String arvlMsg2;
        private String arvlMsg3;
        private String arvlCd;
    }
}
