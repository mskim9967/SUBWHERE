package cau.capstone.ottitor.controller;

import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import cau.capstone.ottitor.constant.Code;
import cau.capstone.ottitor.domain.User;
import cau.capstone.ottitor.dto.JwtDto;
import cau.capstone.ottitor.dto.UserDto;
import cau.capstone.ottitor.repository.JwtRepository;
import cau.capstone.ottitor.repository.KakaoAccountRepository;
import cau.capstone.ottitor.repository.UserRepository;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;


@DisplayName("Controller - OAuth")
class OAuthControllerTest extends BaseControllerTest {

    String kakaoToken = "";

    @Autowired
    private KakaoAccountRepository kakaoAccountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtRepository jwtRepository;

    HashMap<String, Object> registerBody, testUserRegisterBody = new HashMap<>() {{
        put("nickname", "nnnnocknmae");
        put("email", "nnnnocknma@ewdw");
        put("providerType", "TEST");
    }};

    @BeforeEach
    void setUp() {
        registerBody = new HashMap<>() {{
            put("nickname", "qqq");
            put("email", "qqqqqqqq@ewdw");

        }};
    }

    @Test
    @DisplayName("테스트 회원가입")
    void signUpTest() throws Exception {
        UploadControllerTest uploadControllerTest = new UploadControllerTest();
        uploadControllerTest.mockMvc = mockMvc;
//        String profileImgUrl = uploadControllerTest.uploadFile("/profile", Code.OK.getCode());
        String profileImgUrl = "ttettttmp";

        long userCnt = userRepository.count();

        registerBody.put("profileImgUrl", profileImgUrl);
        registerBody.put("providerType", "TEST");
        signUp(registerBody, Code.OK.getCode(), false);

        then(userRepository.count()).isEqualTo(userCnt + 1);

        User user = userRepository.findByEmail(registerBody.get("email").toString()).orElse(null);
        then(user).isNotNull();
        then(user.getNickname()).isEqualTo(registerBody.get("nickname").toString());
        then(user.getProfileImgUrl()).isEqualTo(registerBody.get("profileImgUrl").toString());
        then(user.getProviderType().toString()).isEqualTo(registerBody.get("providerType").toString());
    }

    JwtDto signUp(HashMap<String, Object> body, int code, boolean doDocs) throws Exception {
        MvcResult res = mockMvc.perform(post("/oauth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + (body.get("providerType").equals("KAKAO") ? kakaoToken : "dddd"))
                .content(objectMapper.writeValueAsString(body))
            )
            .andExpect(jsonPath("$.code").value(code))
            .andDo(print())
            .andReturn();

        Map map = gson.fromJson(res.getResponse().getContentAsString(), Map.class);
        return gson.fromJson(gson.toJsonTree(map.get("data")), JwtDto.class);
    }


    @EnabledIf("iskakaoTokenPresent")
    @Test
    @DisplayName("가입 안한 상태에서 카카오로 로그인")
    void signInWithKakaoNotRegisteredTest() throws Exception {
        JwtDto res = (JwtDto) signInWithKakao(Code.OK.getCode(), true);
        then(res.getAccessToken()).isNotNull();
        then(res.getRefreshToken()).isNotNull();
        then(res.getGrantType()).isEqualTo("bearer");
        then(res.getAccessTokenExpiresIn()).isNotNull();
    }

    Object signInWithKakao(int code, boolean doDocs) throws Exception {
        MvcResult res = mockMvc.perform(get("/oauth/sign-in?providerType=KAKAO")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + kakaoToken)
            )
            .andExpect(jsonPath("$.code").value(code))
            .andDo(print())
            .andDo(
                !doDocs ? document("temp") : document("카카오로그인",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("Authorization").description("Kakao Access Token")
                    ),
                    responseFields(
                        fieldWithPath("success").description("성공 여부")
                        , fieldWithPath("code").description("응답 코드")
                        , fieldWithPath("message").description("메시지")
                        , fieldWithPath("data.grantType").description("Token type")
                        , fieldWithPath("data.accessToken").description("access token")
                        , fieldWithPath("data.refreshToken").description("refresh token")
                        , fieldWithPath("data.accessTokenExpiresIn").description("accessToken 만료 시각")
                    )
                )
            )
            .andReturn();

        Map map = gson.fromJson(res.getResponse().getContentAsString(), Map.class);
        if (code == Code.OK.getCode()) {
            return gson.fromJson(gson.toJsonTree(map.get("data")), JwtDto.class);
        }
        return gson.fromJson(gson.toJsonTree(map.get("data")), UserDto.class);
    }

    @EnabledIf("iskakaoTokenPresent")
    @Test
    @DisplayName("가입 안한 상태에서 카카오로 로그인")
    void signUpWithKakaoTest() throws Exception {
        long kakaoCnt = kakaoAccountRepository.count();
        long userCnt = userRepository.count();

        JwtDto res = (JwtDto) signInWithKakao(Code.OK.getCode(), true);

        then(userRepository.count()).isEqualTo(userCnt + 1);
        then(kakaoAccountRepository.count()).isEqualTo(kakaoCnt + 1);
        then(res.getAccessToken()).isNotNull();
        then(res.getRefreshToken()).isNotNull();
        then(res.getGrantType()).isEqualTo("bearer");
        then(res.getAccessTokenExpiresIn()).isNotNull();
    }


    @EnabledIf("iskakaoTokenPresent")
    @Test
    @DisplayName("가입 한 상태에서 카카오로 로그인")
    void signInWithKakaoSuccessTest() throws Exception {
        signUpWithKakaoTest();
        JwtDto res = (JwtDto) signInWithKakao(Code.OK.getCode(), false);
        then(res.getAccessToken()).isNotNull();
        then(res.getRefreshToken()).isNotNull();
        then(res.getGrantType()).isEqualTo("bearer");
        then(res.getAccessTokenExpiresIn()).isNotNull();
    }


    @EnabledIf("iskakaoTokenPresent")
    @Test
    @DisplayName("카카오 중복가입")
    void signUpWithKakaoDuplicateTest() throws Exception {
        registerBody.put("providerType", "KAKAO");
        signUp(registerBody, Code.OK.getCode(), false);
        signUp(registerBody, Code.ALREADY_REGISTERED.getCode(), false);
    }


    boolean iskakaoTokenPresent() {
        return !kakaoToken.isBlank();
    }


    @Test
    @DisplayName("jwt 재발급")
    void getMeTest() throws Exception {
        JwtDto jwt = signUp(testUserRegisterBody, Code.OK.getCode(), false);

        long beforeCnt = jwtRepository.count();

        HashMap<String, Object> jwtBody = new HashMap<>() {{
            put("accessToken", jwt.getAccessToken());
            put("refreshToken", jwt.getRefreshToken());
        }};

        JwtDto res = reissue(jwtBody, Code.OK.getCode(), true);

        then(res.getRefreshToken()).isNotNull();
        then(res.getAccessToken()).isNotNull();

        then(jwtRepository.count()).isEqualTo(beforeCnt);
    }

    JwtDto reissue(HashMap<String, Object> body, int code, boolean doDocs) throws Exception {
        MvcResult res = mockMvc.perform(post("/oauth/reissue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
            )
            .andExpect(jsonPath("$.code").value(code))
            .andDo(print())
            .andDo(
                !doDocs ? document("temp") : document("토큰재발급",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("accessToken").description("만료된 access token")
                        , fieldWithPath("refreshToken").description("보관중인 refresh token")
                    ),
                    responseFields(
                        fieldWithPath("success").description("성공 여부")
                        , fieldWithPath("code").description("응답 코드")
                        , fieldWithPath("message").description("메시지")
                        , fieldWithPath("data.grantType").description("Token type")
                        , fieldWithPath("data.accessToken").description("access token")
                        , fieldWithPath("data.refreshToken").description("refresh token")
                        , fieldWithPath("data.accessTokenExpiresIn").description("accessToken 만료 시각")
                    )
                )
            )
            .andReturn();

        Map map = gson.fromJson(res.getResponse().getContentAsString(), Map.class);
        return gson.fromJson(gson.toJsonTree(map.get("data")), JwtDto.class);
    }
}

