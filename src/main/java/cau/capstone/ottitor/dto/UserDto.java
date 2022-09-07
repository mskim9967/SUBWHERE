package cau.capstone.ottitor.dto;


import static cau.capstone.ottitor.constant.UserConstraint.*;
import static org.apache.logging.log4j.util.Strings.isBlank;

import cau.capstone.ottitor.constant.Code;
import cau.capstone.ottitor.constant.ProviderType;
import cau.capstone.ottitor.constant.Role;
import cau.capstone.ottitor.domain.User;
import cau.capstone.ottitor.repository.UserRepository;
import cau.capstone.ottitor.util.GeneralException;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@AllArgsConstructor
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    private Long id;
    private String email;
    private String nickname;
    private String profileImgUrl;
    private ProviderType providerType;
    private Role role;

    private UserRepository userRepository;

    public UserDto() {
    }

    public static UserDto baseResponse(User user) {

        return UserDto.builder()
            .email(user.getEmail())
            .nickname(user.getNickname())
            .profileImgUrl(user.getProfileImgUrl())
            .providerType(user.getProviderType())
            .role(user.getRole())
            .build();
    }

    public static UserDto previewResponse(User user) {
        return UserDto.builder()
            .email(user.getEmail())
            .nickname(user.getNickname())
            .profileImgUrl(user.getProfileImgUrl())
            .build();
    }


    public User toEntity(UserRepository userRepository) {
        this.userRepository = userRepository;

        return User.builder()
            .nickname(validateNickname(nickname))
            .email(email)
            .profileImgUrl(profileImgUrl)
            .providerType(providerType)
            .build();
    }

    public User updateEntity(User user, UserRepository userRepository) {
        this.userRepository = userRepository;

        Optional.ofNullable(nickname).ifPresent((str) -> user.setNickname(validateNickname(str)));
        Optional.ofNullable(profileImgUrl).ifPresent(user::setProfileImgUrl);
        return user;
    }

    private String validateNickname(String str) {
        String regex = String.format("^\\w{%s,%s}$", NICKNAME_MIN_LENGTH.getKey(), NICKNAME_MAX_LENGTH.getKey());

        if (isBlank(str) || !str.matches(regex)) { // format 검사
            throw new GeneralException(Code.NICKNAME_FORMAT_ERROR, str);
        }

        return str;
    }

}

