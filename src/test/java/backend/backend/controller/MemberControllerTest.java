package backend.backend.controller;

import backend.backend.domain.dto.memberDto.MemberResponseDto;
import backend.backend.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    @Test
    @DisplayName("회원가입 성공")
    void signUp_success() throws Exception {
        doNothing().when(memberService).signUp(any());

        String body = objectMapper.writeValueAsString(
                new java.util.LinkedHashMap<>() {{
                    put("email", "test@test.com");
                    put("password", "password");
                    put("nickName", "테스터");
                    put("emoji", "😀");
                }});

        mockMvc.perform(post("/api/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원가입 실패 - 검증 실패")
    void signUp_validationFail() throws Exception {
        String body = objectMapper.writeValueAsString(
                new java.util.LinkedHashMap<>() {{
                    put("email", "");
                    put("password", "");
                    put("nickName", "");
                }});

        mockMvc.perform(post("/api/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("내 정보 조회 성공")
    void getMyInfo_success() throws Exception {
        MemberResponseDto.InfoDto info = new MemberResponseDto.InfoDto(
                1L, "test@test.com", "테스터", "😀");

        when(memberService.getMyInfo()).thenReturn(info);

        mockMvc.perform(get("/api/member"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("test@test.com"))
                .andExpect(jsonPath("$.data.nickName").value("테스터"));
    }
}
