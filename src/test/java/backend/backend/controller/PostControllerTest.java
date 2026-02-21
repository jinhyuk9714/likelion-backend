package backend.backend.controller;

import backend.backend.domain.dto.Response;
import backend.backend.domain.dto.postDto.PostResponseDto;
import backend.backend.global.util.security.SecurityUtil;
import backend.backend.service.LikesService;
import backend.backend.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @MockBean
    private LikesService likesService;

    private PostResponseDto createPostResponse() {
        PostResponseDto dto = new PostResponseDto();
        dto.setId(1L);
        dto.setTitle("제목");
        dto.setContent("내용");
        dto.setNickName("테스터");
        return dto;
    }

    @Test
    @DisplayName("게시글 목록 조회 성공")
    void getPostList_success() throws Exception {
        Page<PostResponseDto> page = new PageImpl<>(List.of(createPostResponse()));
        when(postService.getPostList(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/post"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].title").value("제목"));
    }

    @Test
    @DisplayName("게시글 작성 성공")
    void createPost_success() throws Exception {
        try (MockedStatic<SecurityUtil> util = mockStatic(SecurityUtil.class)) {
            util.when(SecurityUtil::getLoginEmail).thenReturn("test@test.com");
            when(postService.createPost(any(), eq("test@test.com"))).thenReturn(createPostResponse());

            String body = objectMapper.writeValueAsString(
                    new java.util.LinkedHashMap<>() {{
                        put("title", "제목");
                        put("content", "내용");
                    }});

            mockMvc.perform(post("/api/post")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.title").value("제목"));
        }
    }

    @Test
    @DisplayName("게시글 작성 실패 - 검증 실패")
    void createPost_validationFail() throws Exception {
        try (MockedStatic<SecurityUtil> util = mockStatic(SecurityUtil.class)) {
            util.when(SecurityUtil::getLoginEmail).thenReturn("test@test.com");

            String body = objectMapper.writeValueAsString(
                    new java.util.LinkedHashMap<>() {{
                        put("title", "");
                        put("content", "");
                    }});

            mockMvc.perform(post("/api/post")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    @DisplayName("게시글 상세 조회 성공")
    void getPost_success() throws Exception {
        when(postService.getPost(1L)).thenReturn(createPostResponse());

        mockMvc.perform(get("/api/post/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("제목"));
    }

    @Test
    @DisplayName("게시글 수정 성공")
    void updatePost_success() throws Exception {
        try (MockedStatic<SecurityUtil> util = mockStatic(SecurityUtil.class)) {
            util.when(SecurityUtil::getLoginEmail).thenReturn("test@test.com");
            PostResponseDto updated = createPostResponse();
            updated.setTitle("수정 제목");
            when(postService.updatePost(eq(1L), any(), eq("test@test.com"))).thenReturn(updated);

            String body = objectMapper.writeValueAsString(
                    new java.util.LinkedHashMap<>() {{
                        put("title", "수정 제목");
                        put("content", "수정 내용");
                    }});

            mockMvc.perform(put("/api/post/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.title").value("수정 제목"));
        }
    }

    @Test
    @DisplayName("게시글 삭제 성공")
    void deletePost_success() throws Exception {
        try (MockedStatic<SecurityUtil> util = mockStatic(SecurityUtil.class)) {
            util.when(SecurityUtil::getLoginEmail).thenReturn("test@test.com");
            when(postService.deletePost(1L, "test@test.com")).thenReturn(Response.ok());

            mockMvc.perform(delete("/api/post/1"))
                    .andExpect(status().isOk());
        }
    }
}
