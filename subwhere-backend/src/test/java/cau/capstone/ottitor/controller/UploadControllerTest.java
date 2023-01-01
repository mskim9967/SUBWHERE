package cau.capstone.ottitor.controller;

import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import cau.capstone.ottitor.constant.Code;
import cau.capstone.ottitor.dto.UploadDto;
import java.io.FileInputStream;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MvcResult;


@DisplayName("Controller - Upload")
class UploadControllerTest extends BaseControllerTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("단일 파일 업로드")
    void uploadFileTest() throws Exception {
        String res = uploadFile("profile", Code.OK.getCode(), true);
        then(res.length()).isGreaterThan(5);
    }

    String uploadFile(String path, int code, boolean doDocs) throws Exception {
        MockMultipartFile image = new MockMultipartFile("file", "test.png", "image/png",
            new FileInputStream(System.getProperty("user.dir") + "/src/main/resources/test.png"));

        MvcResult res = mockMvc.perform(RestDocumentationRequestBuilders.multipart("/upload/{path}?multiple=false", path)
                .file(image)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            )
            .andExpect(jsonPath("$.code").value(code))
            .andDo(print())
            .andDo(
                !doDocs ? document("temp") : document("단일파일업로드",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    pathParameters(
                        parameterWithName("path").description("업로드 할 위치")
                    ),
                    requestParameters(
                        parameterWithName("multiple").description("단일 업로드 시 false")
                    ),
                    requestParts(
                        partWithName("file").description("업로드 할 이미지 파일")
                    ),
                    responseFields(
                        fieldWithPath("success").description("성공 여부"),
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("메시지"),
                        fieldWithPath("data").description("이미지 url")
                    )
                )
            )
            .andReturn();

        Map map = gson.fromJson(res.getResponse().getContentAsString(), Map.class);
        return map.get("data").toString();
    }

    @Test
    @DisplayName("복수 파일 업로드")
    void uploadFilesTest() throws Exception {
        UploadDto response = uploadFiles("profile", Code.OK.getCode(), true);
        then(response.getUrls().size()).isEqualTo(3);
    }

    UploadDto uploadFiles(String path, int code, boolean doDocs) throws Exception {
        MockMultipartFile image = new MockMultipartFile("files", "test.png", "image/png",
            new FileInputStream(System.getProperty("user.dir") + "/src/main/resources/test.png"));

        MvcResult res = mockMvc.perform(RestDocumentationRequestBuilders.multipart("/upload/{path}?multiple=true", path)
                .file(image)
                .file(image)
                .file(image)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            )
            .andExpect(jsonPath("$.code").value(code))
            .andDo(print())
            .andDo(
                !doDocs ? document("temp") : document("복수파일업로드",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    pathParameters(
                        parameterWithName("path").description("업로드 할 위치")
                    ),
                    requestParameters(
                        parameterWithName("multiple").description("복수 업로드 시 true")
                    ),
                    requestParts(
                        partWithName("files").description("업로드 할 이미지 파일들")
                    ),
                    responseFields(
                        fieldWithPath("success").description("성공 여부"),
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("메시지"),
                        fieldWithPath("data.urls.[]").description("이미지 url (업로드 순서대로)")
                    )
                )
            )
            .andReturn();

        Map map = gson.fromJson(res.getResponse().getContentAsString(), Map.class);
        return gson.fromJson(gson.toJsonTree(map.get("data")), UploadDto.class);
    }
}

