package cau.capstone.ottitor.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class KakaoAccount extends BaseTimeEntity {

    @Id
    @Column
    private String id;

    @OneToOne
    @JoinColumn
    private User user;

    @Builder
    public KakaoAccount(String id, User user) {
        this.id = id;
        this.user = user;
    }
}
