package kr.brain.our_app.bookmark.service;

import kr.brain.our_app.bookmark.domain.Bookmark;
import kr.brain.our_app.bookmark.dto.BookmarkDto;
import kr.brain.our_app.bookmark.dto.RequestFrontDto;
import kr.brain.our_app.bookmark.repository.BookmarkRepository;
import kr.brain.our_app.bookmark.service.BookmarkService;
import kr.brain.our_app.idsha.IDGenerator;
import kr.brain.our_app.tag.domain.Tag;
import kr.brain.our_app.tag.dto.TagDto;
import kr.brain.our_app.tag.service.TagService;
import kr.brain.our_app.bookmark.domain.TagBookmark;
import kr.brain.our_app.bookmark.dto.TagBookmarkDto;
import kr.brain.our_app.bookmark.repository.TagBookmarkRepository;
import kr.brain.our_app.bookmark.service.TagBookmarkService;
import kr.brain.our_app.user.dto.UserDto;
import kr.brain.our_app.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class TagBookmarkServiceTest {

    @Autowired
    private TagBookmarkService tagBookmarkService;

    @Autowired
    private TagService tagService;

    @Autowired
    private BookmarkService bookmarkService;

    @Autowired
    private TagBookmarkRepository tagBookmarkRepository;

    @Autowired
    private UserService userService;

    private String tagId;
    private String bookmarkId;
    private String userId;

    @BeforeEach
    void setUp() {
        // 필요한 데이터 준비
        UserDto userDto = UserDto.builder()
                .userName("test1")
                .email("ljyong3339@gmail.com")
                .build();

        userId = userService.createUser(userDto).getId();

        TagDto tagDto = TagDto.builder()
                .tagName("SampleTag")
                .build();
        tagId = tagService.createTag(tagDto, userDto).getId();

        BookmarkDto bookmarkDto = BookmarkDto.builder()
                .bookmarkName("SampleBookmark")
                .url("http://sample.com")
                .build();
        bookmarkId = bookmarkService.createBookmark(bookmarkDto, userDto).getId();
    }

    @Test
    void testRequestTagBookmark_CreatesNewTagsAndBookmark() {
        RequestFrontDto requestFrontDto = RequestFrontDto.builder()
                .email("ljyong3339@gmail.com")
                .userName("test1")
                .title("Sample Bookmark")
                .url("http://sample.com")
                .tags(Arrays.asList("tag1", "tag2"))
                .build();

        // When: TagBookmark 생성
        List<TagBookmarkDto> result = tagBookmarkService.requestTagBookmark(requestFrontDto);

        // Then: 결과 검증
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(requestFrontDto.getTags().size());

        // 추가 검증: User와 Bookmark 생성 여부 확인
        UserDto userDto = userService.findByEmail(requestFrontDto.getEmail());
        assertThat(userDto).isNotNull();
        assertThat(userDto.getUserName()).isEqualTo(requestFrontDto.getUserName());
    }

    @Test
    void testCreateTagBookmark_Success() {

        // given
        TagBookmarkDto tagBookmarkDto = tagBookmarkService.createTagBookmark(tagId, bookmarkId, userId);

        // when
        TagBookmark retrievedTagBookmark = tagBookmarkRepository.findByTagIdAndBookmarkId(tagId, bookmarkId).orElse(null);

        // then
        assertThat(retrievedTagBookmark).isNotNull();
        assertThat(retrievedTagBookmark.getTag().getId()).isEqualTo(tagId);
        assertThat(retrievedTagBookmark.getBookmark().getId()).isEqualTo(bookmarkId);
    }
    @Test
    void testResponseTagBookmark_ReturnsCorrectBookmarks() {
        // Given: RequestFrontDto를 통해 Tag와 Bookmark를 생성
        RequestFrontDto requestFrontDto = RequestFrontDto.builder()
                .email("ljyong3339@gmail.com")
                .userName("test1")
                .title("Sample Bookmark")
                .url("http://sample.com")
                .tags(Arrays.asList("SampleTag"))
                .build();
        tagBookmarkService.requestTagBookmark(requestFrontDto);

        // When: 특정 태그와 사용자 이메일로 북마크 목록 조회
        List<BookmarkDto> bookmarkDtos = tagBookmarkService.responseTagBookmark("SampleTag", "ljyong3339@gmail.com");
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!");//test
        System.out.println(bookmarkDtos);   //TEST
        bookmarkDtos.forEach(bookmarkDto -> {
            System.out.println("Bookmark Name: " + bookmarkDto.getBookmarkName());
            System.out.println("URL: " + bookmarkDto.getUrl());
            System.out.println("----");
        });

        // Then: 결과 검증
        assertThat(bookmarkDtos).isNotEmpty();
        assertThat(bookmarkDtos.size()).isEqualTo(1); // Sample Bookmark 하나만 존재
        assertThat(bookmarkDtos.get(0).getBookmarkName()).isEqualTo("Sample Bookmark");
        assertThat(bookmarkDtos.get(0).getUrl()).isEqualTo("http://sample.com");
    }
    @Test
    void testFindTagsByBookmarkName() {

        // 추가 태그 생성
        TagDto tagDto2 = TagDto.builder().tagName("SampleTag2").build();
        TagDto tagDto3 = TagDto.builder().tagName("SampleTag3").build();
        String tagId2 = tagService.createTag(tagDto2, userService.findById(userId)).getId();
        String tagId3 = tagService.createTag(tagDto3, userService.findById(userId)).getId();

        // 태그와 북마크 연결
        tagBookmarkService.createTagBookmark(tagId, bookmarkId, userId);
        tagBookmarkService.createTagBookmark(tagId2, bookmarkId, userId); // SampleBookmark와 SampleTag2 연결
        tagBookmarkService.createTagBookmark(tagId3, bookmarkId, userId); // SampleBookmark와 SampleTag3 연결

        // When: 특정 북마크의 태그 조회
        List<TagDto> tagsForBookmark = tagBookmarkService.findTagsByBookmarkName("SampleBookmark", userId);

        // Then: 반환된 태그 검증
        assertThat(tagsForBookmark).isNotEmpty();
        assertThat(tagsForBookmark.size()).isEqualTo(3); // "SampleBookmark"에 연결된 태그는 3개
        List<String> tagNames = tagsForBookmark.stream().map(TagDto::getTagName).toList();
        assertThat(tagNames).contains("SampleTag", "SampleTag2", "SampleTag3");

        // 출력
        System.out.println("Tags for SampleBookmark:");
        tagsForBookmark.forEach(tag -> System.out.println("Tag Name: " + tag.getTagName()));

        // When: 존재하지 않는 북마크의 태그 조회
        List<TagDto> tagsForNonExistentBookmark = tagBookmarkService.findTagsByBookmarkName("NonExistentBookmark", userId);

        // Then: 반환된 태그가 비어있는지 확인
        assertThat(tagsForNonExistentBookmark).isEmpty();

        // 출력
        System.out.println("Tags for NonExistentBookmark: " + tagsForNonExistentBookmark.size());

        // 추가 테스트: 다른 북마크와 연결된 태그 조회
        BookmarkDto newBookmark = BookmarkDto.builder()
                .bookmarkName("SampleBookmark2")
                .url("http://sample2.com")
                .build();
        String newBookmarkId = bookmarkService.createBookmark(newBookmark, userService.findById(userId)).getId();

        tagBookmarkService.createTagBookmark(tagId2, newBookmarkId, userId); // SampleBookmark2와 SampleTag2 연결

        List<TagDto> tagsForNewBookmark = tagBookmarkService.findTagsByBookmarkName("SampleBookmark2", userId);
        assertThat(tagsForNewBookmark).hasSize(1); // SampleBookmark2에 연결된 태그는 1개
        assertThat(tagsForNewBookmark.get(0).getTagName()).isEqualTo("SampleTag2");

        // 출력
        System.out.println("Tags for SampleBookmark2:");
        tagsForNewBookmark.forEach(tag -> System.out.println("Tag Name: " + tag.getTagName()));
    }

}
