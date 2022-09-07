package cau.capstone.ottitor.controller;

import cau.capstone.ottitor.constant.Code;
import cau.capstone.ottitor.constant.ProviderType;
import cau.capstone.ottitor.dto.DataResponseDto;
import cau.capstone.ottitor.dto.JwtDto;
import cau.capstone.ottitor.dto.UserDto;
import cau.capstone.ottitor.service.JwtService;
import cau.capstone.ottitor.service.OAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/oauth")
@Slf4j
public class OAuthController {

    private final OAuthService oAuthService;
    private final JwtService jwtService;

    @GetMapping("/sign-in")
    public DataResponseDto<Object> loginWithProviderToken(
        @RequestParam("providerType") ProviderType providerType,
        @RequestHeader("Authorization") String providerAccessToken
    ) {
        Object signInResult = oAuthService.signIn(providerType, providerAccessToken);
        if (!signInResult.getClass().equals(JwtDto.class)) {
            oAuthService.signUp((UserDto) signInResult, providerAccessToken);
            signInResult = oAuthService.signIn(providerType, providerAccessToken);
        }
        return DataResponseDto.of(signInResult);
//        return DataResponseDto.of(Code.NOT_REGISTERED, signInResult);
    }

    @PostMapping("/sign-up")
    public DataResponseDto<Object> signUp(
        @RequestHeader("Authorization") String providerAccessToken,
        @RequestBody UserDto userDto
    ) {
        return DataResponseDto.of(oAuthService.signUp(userDto, providerAccessToken));
    }

    @PostMapping("/reissue")
    public DataResponseDto<Object> reissueJwt(@RequestBody JwtDto jwtDto) {
        return DataResponseDto.of(jwtService.reissueJwt(jwtDto));
    }
}
