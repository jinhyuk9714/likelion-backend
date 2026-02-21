package backend.backend.controller;

import backend.backend.domain.Comment;
import backend.backend.domain.Member;
import backend.backend.domain.dto.Response;
import backend.backend.domain.dto.commentDto.CommentResponseDto;
import backend.backend.global.util.security.SecurityUtil;
import backend.backend.service.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    private CommentResponseDto createCommentResponse(String content) {
        Member member = Member.builder()
                .email("test@test.com").nickName("테스터").emoji("😀").password("pw").build();
        Comment comment = Comment.builder()
                .id(1L).content(content).nickname("테스터").member(member)
                .depth(0).children(new ArrayList<>()).build();
        return new CommentResponseDto(comment, new ArrayList<>());
    }

    @Test
    @DisplayName("댓글 작성 성공")
    void createComment_success() throws Exception {
        try (MockedStatic<SecurityUtil> util = mockStatic(SecurityUtil.class)) {
            util.when(SecurityUtil::getLoginEmail).thenReturn("test@test.com");
            when(commentService.createComment(eq(1L), eq(0L), any(), eq("test@test.com")))
                    .thenReturn(createCommentResponse("댓글 내용"));

            String body = objectMapper.writeValueAsString(
                    new java.util.LinkedHashMap<>() {{
                        put("content", "댓글 내용");
                    }});

            mockMvc.perform(post("/api/post/1/comment/0")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").value("댓글 내용"));
        }
    }

    @Test
    @DisplayName("댓글 조회 성공")
    void getComment_success() throws Exception {
        when(commentService.getComment(1L)).thenReturn(createCommentResponse("댓글 내용"));

        mockMvc.perform(get("/api/comment/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").value("댓글 내용"));
    }

    @Test
    @DisplayName("댓글 수정 성공")
    void updateComment_success() throws Exception {
        try (MockedStatic<SecurityUtil> util = mockStatic(SecurityUtil.class)) {
            util.when(SecurityUtil::getLoginEmail).thenReturn("test@test.com");
            when(commentService.updateComment(eq(1L), any(), eq("test@test.com")))
                    .thenReturn(createCommentResponse("수정 댓글"));

            String body = objectMapper.writeValueAsString(
                    new java.util.LinkedHashMap<>() {{
                        put("content", "수정 댓글");
                    }});

            mockMvc.perform(put("/api/comment/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").value("수정 댓글"));
        }
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void deleteComment_success() throws Exception {
        try (MockedStatic<SecurityUtil> util = mockStatic(SecurityUtil.class)) {
            util.when(SecurityUtil::getLoginEmail).thenReturn("test@test.com");
            when(commentService.deleteComment(1L, "test@test.com")).thenReturn(Response.ok());

            mockMvc.perform(delete("/api/comment/1"))
                    .andExpect(status().isOk());
        }
    }
}
