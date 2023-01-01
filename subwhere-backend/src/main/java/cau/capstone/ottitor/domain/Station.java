package cau.capstone.ottitor.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Station extends BaseTimeEntity {

    @Id
    private String code;

    @Column
    private String nmKor;

    @Column
    private String nmEng;

    @Column
    private String lineNum;

    @Column
    private String frCode;

    @Column
    private Integer mnt;

    @Column
    private Integer directAt;

    @Builder
    public Station(String code, String nmKor, String nmEng, String lineNum, String frCode, Integer mnt, Integer directAt) {
        this.code = code;
        this.nmKor = nmKor;
        this.nmEng = nmEng;
        this.lineNum = lineNum;
        this.frCode = frCode;
        this.mnt = mnt;
        this.directAt = directAt;
    }
}