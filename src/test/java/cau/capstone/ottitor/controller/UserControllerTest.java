package cau.capstone.ottitor.controller;

import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import cau.capstone.ottitor.constant.Code;
import cau.capstone.ottitor.constant.Role;
import cau.capstone.ottitor.domain.User;
import cau.capstone.ottitor.dto.JwtDto;
import cau.capstone.ottitor.dto.UserDto;
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


@DisplayName("Controller - User")
class UserControllerTest extends BaseControllerTest {

    @Autowired
    private KakaoAccountRepository kakaoAccountRepository;
    @Autowired
    private UserRepository userRepository;

    OAuthControllerTest oAuthControllerTest = new OAuthControllerTest();
    JwtDto jwt;

    @BeforeEach
    void setUp() throws Exception {
        oAuthControllerTest.mockMvc = mockMvc;
        jwt = oAuthControllerTest.signUp(oAuthControllerTest.testUserRegisterBody, Code.OK.getCode(), false);
    }

    @Test
    @DisplayName("내 정보 조회")
    void getMeTest() throws Exception {
        UserDto res = getMe(jwt.getAccessToken(), Code.OK.getCode(), true);

        then(res.getNickname()).isEqualTo(oAuthControllerTest.testUserRegisterBody.get("nickname"));
        then(res.getProfileImgUrl()).isEqualTo(oAuthControllerTest.testUserRegisterBody.get("profileImgUrl"));
    }

    UserDto getMe(String ac, int code, boolean doDocs) throws Exception {
        MvcResult res = mockMvc.perform(get("/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + ac)
            )
            .andExpect(jsonPath("$.code").value(code))
            .andDo(print())
            .andDo(
                !doDocs ? document("temp") : document("내정보조회",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("Authorization").description("access token")
                    ),
                    responseFields(
                        fieldWithPath("success").description("성공 여부")
                        , fieldWithPath("code").description("응답 코드")
                        , fieldWithPath("message").description("메시지")
                        , fieldWithPath("data.email").description("이메일")
                        , fieldWithPath("data.nickname").description("닉네임")
                        , fieldWithPath("data.providerType").description("가입 종류")
                        , fieldWithPath("data.role").description("상태(ROLE_USER, ROLE_BANNED, ROLE_WITHDRAWAL)")
                    )
                )
            )
            .andReturn();

        Map map = gson.fromJson(res.getResponse().getContentAsString(), Map.class);
        return gson.fromJson(gson.toJsonTree(map.get("data")), UserDto.class);
    }


    @Test
    @DisplayName("회원 정보 수정1")
    void updateUserTest1() throws Exception {

        HashMap<String, Object> body = new HashMap<>() {{
            put("nickname", "newnewnick");
        }};

        UserDto res = updateUser(jwt.getAccessToken(), body, Code.OK.getCode());

        then(res.getNickname()).isEqualTo(body.get("nickname"));
        then(res.getProfileImgUrl()).isEqualTo(oAuthControllerTest.testUserRegisterBody.get("profileImgUrl"));
    }

    @Test
    @DisplayName("회원 정보 수정2")
    void updateUserTest2() throws Exception {

        HashMap<String, Object> body = new HashMap<>() {{
            put("profileImgUrl", "https://newnew....");
        }};

        UserDto res = updateUser(jwt.getAccessToken(), body, Code.OK.getCode());

        then(res.getNickname()).isEqualTo(oAuthControllerTest.testUserRegisterBody.get("nickname"));
        then(res.getProfileImgUrl()).isEqualTo(body.get("profileImgUrl"));
    }

    @Test
    @DisplayName("회원 정보 수정 실패 (닉네임 길이)")
    void updateUserFailTest1() throws Exception {
        HashMap<String, Object> body = new HashMap<>() {{
            put("nickname", "newnew nㄴㅇㄹㄴㅁㅇㄹㄴㅁㅇaick");
        }};

        updateUser(jwt.getAccessToken(), body, Code.NICKNAME_FORMAT_ERROR.getCode());
    }


    UserDto updateUser(String ac, HashMap<String, Object> body, int code) throws Exception {
        MvcResult res = mockMvc.perform(patch("/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + ac)
                .content(objectMapper.writeValueAsString(body))
            )
            .andExpect(jsonPath("$.code").value(code))
            .andDo(print())
            .andReturn();

        Map map = gson.fromJson(res.getResponse().getContentAsString(), Map.class);
        return gson.fromJson(gson.toJsonTree(map.get("data")), UserDto.class);
    }


    @Test
    @DisplayName("회원 탈퇴")
    void withdrawUserTest() throws Exception {
        Long userId = userRepository.findByEmail(
            (String) oAuthControllerTest.testUserRegisterBody.get("email")).get().getId();

        Boolean res = withdrawUser(jwt.getAccessToken(), Code.OK.getCode(), true);
        then(res).isTrue();

        User user = userRepository.findById(userId).get();
        then(user.getRole()).isEqualTo(Role.ROLE_WITHDRAWAL);
        then(user.getNickname()).isNull();
        then(user.getProfileImgUrl()).isNull();
    }

    Boolean withdrawUser(String ac, int code, boolean doDocs) throws Exception {
        MvcResult res = mockMvc.perform(delete("/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + ac)
            )
            .andExpect(jsonPath("$.code").value(code))
            .andDo(print())
            .andDo(
                !doDocs ? document("temp") : document("내정보조회",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("Authorization").description("access token")
                    ),
                    responseFields(
                        fieldWithPath("success").description("성공 여부")
                        , fieldWithPath("code").description("응답 코드")
                        , fieldWithPath("message").description("메시지")
                        , fieldWithPath("data").description("탈퇴 성공 여부")
                    )
                )
            )
            .andReturn();

        Map map = gson.fromJson(res.getResponse().getContentAsString(), Map.class);
        return Boolean.parseBoolean(map.get("data").toString());
    }

    @Test
    @EnabledIf("iskakaoTokenPresent")
    @DisplayName("카카오 회원 탈퇴")
    void withdrawKakaoUserTest() throws Exception {
        jwt = (JwtDto) oAuthControllerTest.signInWithKakao(Code.OK.getCode(), false);

        Long userId = userRepository.findByEmail(getMe(jwt.getAccessToken(), Code.OK.getCode(), false).getEmail()).get()
            .getId();

        then(kakaoAccountRepository.findByUserId(userId)).isPresent();

        Boolean res = withdrawUser(jwt.getAccessToken(), Code.OK.getCode(), false);
        then(res).isTrue();

        then(kakaoAccountRepository.findByUserId(userId)).isEmpty();

        User user = userRepository.findById(userId).get();
        then(user.getRole()).isEqualTo(Role.ROLE_WITHDRAWAL);
        then(user.getNickname()).isNull();
        then(user.getProfileImgUrl()).isNull();
    }

    boolean iskakaoTokenPresent() {
        return !oAuthControllerTest.kakaoToken.isBlank();
    }
}

