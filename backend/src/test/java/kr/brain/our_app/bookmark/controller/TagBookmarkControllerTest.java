package kr.brain.our_app.bookmark.controller;

import jakarta.transaction.Transactional;
import kr.brain.our_app.bookmark.domain.Bookmark;
import kr.brain.our_app.bookmark.domain.TagBookmark;
import kr.brain.our_app.bookmark.dto.BookmarkDto;
import kr.brain.our_app.bookmark.dto.RequestFrontDto;
import kr.brain.our_app.bookmark.service.TagBookmarkService;
import kr.brain.our_app.tag.domain.Tag;
import kr.brain.our_app.tag.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.containsString;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TagBookmarkControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TagBookmarkService tagBookmarkService;

    private RequestFrontDto requestFrontDto;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        requestFrontDto = RequestFrontDto.builder()
                .title("Sample Bookmark")
                .url("http://sample.com")
                .tags(Arrays.asList("SampleTag1", "SampleTag2"))
                .userName("testUser")
                .email("testuser@example.com")
                .build();
        tagBookmarkService.requestTagBookmark(requestFrontDto);
    }
    @Test
    void testResponseTagBookmark() throws Exception {
        //request 2 setting
        RequestFrontDto requestFrontDto2 = RequestFrontDto.builder()
                .title("Sample Bookmark2")
                .url("http://sample2.com")
                .tags(Arrays.asList("SampleTag1", "SampleTag3"))
                .userName("testUser")
                .email("testuser@example.com")
                .build();
        tagBookmarkService.requestTagBookmark(requestFrontDto2);

        // Given: 요청할 태그와 사용자 이메일
        String tagName = "SampleTag1";
        String userEmail = "testuser@example.com";

        // When: GET 요청 실행
        MvcResult result = mockMvc.perform(get("/api/tagbookmark/out")
                        .param("tagName", tagName)
                        .param("userEmail", userEmail))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Then: 응답 데이터 검증
        String responseContent = result.getResponse().getContentAsString();
        System.out.println("GET Response: " + responseContent);

        //assertThat(responseContent).contains("Sample Bookmark");
    }

    @Test
    void testCreateTagBookmarksFromRequest() throws Exception {
        // JSON 요청 데이터
        String jsonRequest = """
                {
                    "title": "Sample Bookmark",
                    "url": "http://sample.com",
                    "tags": ["SampleTag1", "SampleTag2"],
                    "userName": "testUser",
                    "email": "testuser@example.com"
                }
                """;

        // When: POST 요청 실행
        MvcResult result = mockMvc.perform(post("/api/tagbookmark/in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();

        // Then: 응답 데이터 검증
        String responseContent = result.getResponse().getContentAsString();
        assertThat(responseContent).isNotEmpty();
        System.out.println("POST Response: " + responseContent);
    }

    @Test
    public void testCreateTagBookmarkByRequestParam()throws Exception{
        mockMvc.perform(post("/api/tagbookmark/in")
                .param("bookmarkName", "test1")
                .param("bookmarkUrl", "https://testGoogle.com")
                .contentType(MediaType.APPLICATION_JSON)
        );
    }
//
//    @Test
//    public void testCreateTagBookmark() throws Exception {
//        when(tagRepository.findById(any(Long.class))).thenReturn(Optional.of(tag));
//        when(tagBookmarkService.createTagBookmark(any(Tag.class), any(Bookmark.class))).thenReturn(tagBookmark);
//
//        mockMvc.perform(post("/api/bookmarks/tags")
//                        .param("tagId", "1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"url\": \"http://example.com\"}"))
//                .andExpect(status().isOk())
//                .andExpect(content().string(containsString("http://example.com")));
//    }
//
//    @Test
//    public void testGetBookmarksByTag() throws Exception {
//        Pageable pageable = PageRequest.of(0, 10);
//        when(tagRepository.findById(eq(1L))).thenReturn(Optional.of(tag));
//        when(tagBookmarkService.getBookmarksByTag(any(Tag.class), any(Pageable.class)))
//                .thenReturn(new PageImpl<>(Collections.singletonList(tagBookmark), pageable, 1));
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/bookmarks/tags/1/bookmarks")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    public void testRemoveTagBookmark() throws Exception {
//        when(tagRepository.findById(any(Long.class))).thenReturn(Optional.of(tag));
//
//        mockMvc.perform(MockMvcRequestBuilders.delete("/api/bookmarks/tags")
//                        .param("tagId", "1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"url\": \"http://example.com\"}"))
//                .andExpect(status().isNoContent());
//    }
//
//    @Test
//    public void testDeleteAllTagBookmarks() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.delete("/api/bookmarks/tags/1/all-bookmarks"))
//                .andExpect(status().isNoContent());
//    }
}