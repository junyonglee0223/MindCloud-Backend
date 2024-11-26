//package kr.brain.our_app.bookmark.service;
//
//import kr.brain.our_app.bookmark.domain.Bookmark;
//import kr.brain.our_app.bookmark.domain.TagBookmark;
//import kr.brain.our_app.bookmark.dto.BookmarkWithTagsDto;
//import kr.brain.our_app.bookmark.dto.SearchDto;
//import kr.brain.our_app.bookmark.repository.TagBookmarkRepository;
//import kr.brain.our_app.idsha.IDGenerator;
//import kr.brain.our_app.tag.domain.Tag;
//import kr.brain.our_app.tag.service.TagService;
//import kr.brain.our_app.user.domain.User;
//import kr.brain.our_app.user.service.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//@Transactional
//public class SearchServiceTest {
//
//    @Autowired
//    private SearchService searchService;
//
//    @Autowired
//    private TagService tagService;
//
//    @Autowired
//    private BookmarkService bookmarkService;
//
//    @Autowired
//    private TagBookmarkService tagBookmarkService;
//
//    @Autowired
//    private TagBookmarkRepository tagBookmarkRepository;
//
//    @Autowired
//    private UserService userService;
//
//    private String tagId;
//    private String bookmarkId;
//    private String userId;
//
//    @BeforeEach
//    void setUp() {
//        // 사용자 엔티티 생성
//        User user = new User();
//        user.setId(IDGenerator.generateId("user-id"));
//        user.setUserName("test1");
//        user.setEmail("sample1@gmail.com");
//        userService.save(user); // 엔티티 저장
//        userId = user.getId();
//
//        // 태그 엔티티 생성
//        Tag tag = new Tag();
//        tag.setId(IDGenerator.generateId("java-tag"));
//        tag.setTagName("java");
//        tag.setUser(user); // 사용자 엔티티 설정
//        tagService.save(tag); // 엔티티 저장
//        tagId = tag.getId();
//
//        // 북마크 엔티티 생성
//        Bookmark bookmark = new Bookmark();
//        bookmark.setId(IDGenerator.generateId("bookmark-id"));
//        bookmark.setBookmarkName("Java Documentation");
//        bookmark.setUrl("https://docs.oracle.com");
//        bookmark.setUser(user); // 사용자 엔티티 설정
//        bookmarkService.save(bookmark); // 엔티티 저장
//        bookmarkId = bookmark.getId();
//
//        // TagBookmark 엔티티 생성 및 저장
//        TagBookmark tagBookmark = new TagBookmark();
//        tagBookmark.setId(IDGenerator.generateId("tag-bookmark-id"));
//        tagBookmark.setTag(tag); // 태그 엔티티 설정
//        tagBookmark.setBookmark(bookmark); // 북마크 엔티티 설정
//        tagBookmarkRepository.save(tagBookmark);
//
//        // 디버깅 출력
//        System.out.println("User ID: " + userId);
//        System.out.println("Tag ID: " + tagId);
//        System.out.println("Bookmark ID: " + bookmarkId);
//    }
//
//    @Test
//    void testSearchByWord_TagBasedResults() {
//        // Given
//        String email = "sample1@gmail.com";
//        String word = "java";
//
//        // When
//        SearchDto result = searchService.searchByWord(word, email);
//
//        // Then
//        List<BookmarkWithTagsDto> tagResults = result.getTagResults();
//        assertThat(tagResults).isNotEmpty();
//        assertThat(tagResults.size()).isEqualTo(1);
//        assertThat(tagResults.get(0).getBookmarkName()).isEqualTo("Java Documentation");
//        assertThat(tagResults.get(0).getUrl()).isEqualTo("https://docs.oracle.com");
//
//        // 출력
//        System.out.println("Tag-Based Results:");
//        tagResults.forEach(tagResult -> {
//            System.out.println("Bookmark Name: " + tagResult.getBookmarkName());
//            System.out.println("URL: " + tagResult.getUrl());
//            System.out.println("Tags: " + tagResult.getTags());
//        });
//    }
//
//    @Test
//    void testSearchByWord_BookmarkNameBasedResults() {
//        // Given
//        String email = "sample1@gmail.com";
//        String word = "Java Documentation";
//
//        // When
//        SearchDto result = searchService.searchByWord(word, email);
//
//        // Then
//        List<BookmarkWithTagsDto> bookmarkResults = result.getBookmarkResults();
//        assertThat(bookmarkResults).isNotEmpty();
//        assertThat(bookmarkResults.size()).isEqualTo(1);
//        assertThat(bookmarkResults.get(0).getBookmarkName()).isEqualTo("Java Documentation");
//        assertThat(bookmarkResults.get(0).getUrl()).isEqualTo("https://docs.oracle.com");
//
//        // 출력
//        System.out.println("Bookmark Name-Based Results:");
//        bookmarkResults.forEach(bookmarkResult -> {
//            System.out.println("Bookmark Name: " + bookmarkResult.getBookmarkName());
//            System.out.println("URL: " + bookmarkResult.getUrl());
//            System.out.println("Tags: " + bookmarkResult.getTags());
//        });
//    }
//}