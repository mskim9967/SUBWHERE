package cau.capstone.ottitor.service;

import cau.capstone.ottitor.constant.Code;
import cau.capstone.ottitor.domain.Jwt;
import cau.capstone.ottitor.domain.User;
import cau.capstone.ottitor.dto.JwtDto;
import cau.capstone.ottitor.jwt.TokenProvider;
import cau.capstone.ottitor.repository.JwtRepository;
import cau.capstone.ottitor.repository.UserRepository;
import cau.capstone.ottitor.util.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class JwtService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final JwtRepository jwtRepository;

    public JwtDto reissueJwt(JwtDto jwtDto) {
        if (!tokenProvider.validateToken(jwtDto.getRefreshToken())) {
            throw new GeneralException(Code.INVALID_REFRESH_TOKEN);
        }

        Authentication authentication = tokenProvider.getAuthentication(jwtDto.getAccessToken());
        Long userId = Long.parseLong(authentication.getName());

        Jwt jwt = jwtRepository.findOneByUserId(userId)
            .orElseThrow(() -> new GeneralException(Code.REFRESH_TOKEN_NOT_FOUND));
        if (!jwt.getRefreshToken().equals(jwtDto.getRefreshToken())) {
            throw new GeneralException(Code.INVALID_REFRESH_TOKEN);
        }

        jwtRepository.delete(jwt);

        User user = userRepository.findById(userId).orElseThrow(() -> new GeneralException(Code.USER_NOT_FOUND));
        JwtDto newJwtDto = tokenProvider.generateJwt(user);
        Jwt newJwt = Jwt.builder().user(user).refreshToken(newJwtDto.getRefreshToken()).build();
        jwtRepository.save(newJwt);

        return newJwtDto;
    }

    public JwtDto issueJwt(User user) {
        jwtRepository.findOneByUserId(user.getId()).ifPresent(jwtRepository::delete);
        JwtDto jwtDto = tokenProvider.generateJwt(user);
        Jwt jwt = Jwt.builder().user(user).refreshToken(jwtDto.getRefreshToken()).build();
        jwtRepository.save(jwt);
        return jwtDto;
    }

}