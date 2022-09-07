package cau.capstone.ottitor.domain;

import cau.capstone.ottitor.constant.ProviderType;
import cau.capstone.ottitor.constant.Role;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true, unique = true)
    private String email;

    @Column(nullable = true)
    @Setter
    private String nickname;

    @Column(nullable = true)
    @Setter
    private String profileImgUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private ProviderType providerType;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    @Setter
    private Role role = Role.ROLE_USER;

    @Builder
    public User(String email, String nickname, String profileImgUrl, ProviderType providerType) {
        this.email = email;
        this.nickname = nickname;
        this.profileImgUrl = profileImgUrl;
        this.providerType = providerType;
    }

    public void withdraw() {
        email = null;
        nickname = null;
        profileImgUrl = null;
        role = Role.ROLE_WITHDRAWAL;
    }
}
