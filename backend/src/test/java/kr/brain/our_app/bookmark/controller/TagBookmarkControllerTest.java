package kr.brain.our_app.bookmark.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    void testSendTagBookmarksFromRequest() throws Exception {
        // Given: 테스트 사용자 이메일
        String userEmail = "testuser@example.com";

        // When: /outAll 요청
        MvcResult mvcResult = mockMvc.perform(get("/api/tagbookmark/outAll")
                        .param("userEmail", userEmail)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // HTTP 200 OK 응답 확인
                .andReturn();

        // Then: 응답 데이터 출력
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        System.out.println("응답 JSON 데이터: " + jsonResponse);

        // JSON 데이터를 Java 객체로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        RequestFrontDto[] responseArray = objectMapper.readValue(jsonResponse, RequestFrontDto[].class); // 배열로 변환
        List<RequestFrontDto> responseDtos = Arrays.asList(responseArray); // 배열을 리스트로 변환

        // 데이터 검증
        assertThat(responseDtos).isNotEmpty(); // 데이터가 비어있지 않은지 확인
        responseDtos.forEach(dto -> {
            System.out.println("Title: " + dto.getTitle());
            System.out.println("URL: " + dto.getUrl());
            System.out.println("Tags: " + dto.getTags());
            System.out.println("----");
        });
    }


    @Test
    void testSendTagBookmarksWithSpecificTag() throws Exception {
        // Given: 테스트 사용자 이메일과 태그명
        String userEmail = "testuser@example.com";
        String tagName = "SampleTag1";

        // When: /out 요청
        MvcResult mvcResult = mockMvc.perform(get("/api/tagbookmark/out")
                        .param("tagName", tagName)
                        .param("userEmail", userEmail)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // HTTP 200 OK 응답 확인
                .andReturn();

        // Then: 응답 데이터 출력
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        System.out.println("응답 JSON 데이터: " + jsonResponse);

        // JSON 데이터를 Java 객체로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        RequestFrontDto[] responseArray = objectMapper.readValue(jsonResponse, RequestFrontDto[].class); // 배열로 변환
        List<RequestFrontDto> responseDtos = Arrays.asList(responseArray); // 배열을 리스트로 변환

        // 데이터 검증
        assertThat(responseDtos).isNotEmpty(); // 데이터가 비어있지 않은지 확인
        responseDtos.forEach(dto -> {
            System.out.println("Title: " + dto.getTitle());
            System.out.println("URL: " + dto.getUrl());
            System.out.println("Tags: " + dto.getTags());
            System.out.println("----");
        });
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
}