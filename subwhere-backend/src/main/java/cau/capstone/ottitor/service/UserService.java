package cau.capstone.ottitor.service;


import cau.capstone.ottitor.constant.Code;
import cau.capstone.ottitor.domain.User;
import cau.capstone.ottitor.dto.UserDto;
import cau.capstone.ottitor.repository.JwtRepository;
import cau.capstone.ottitor.repository.UserRepository;
import cau.capstone.ottitor.util.GeneralException;
import cau.capstone.ottitor.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

    private final OAuthService oAuthService;
    private final UserRepository userRepository;
    private final JwtRepository jwtRepository;

    private User getUser(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new GeneralException(Code.UNAUTHORIZED));
    }


    public UserDto get(Long userId) {
        return UserDto.baseResponse(getUser(userId));
    }

    public UserDto getMe() {
        return get(SecurityUtil.getUserId());
    }

    public UserDto updateMe(UserDto userDto) {
        User me = getUser(SecurityUtil.getUserId());
        me = userRepository.save(userDto.updateEntity(me, userRepository));
        return UserDto.baseResponse(me);
    }

    public Boolean delete(Long userId) {
        User user = getUser(userId);

        switch (user.getProviderType()) {
            case KAKAO:
                oAuthService.unlinkKakaoAccount(userId);
                break;
        }

        jwtRepository.deleteByUserId(user.getId());
        user.withdraw();

        return true;
    }

    public Boolean deleteMe() {
        return delete(SecurityUtil.getUserId());
    }
}
