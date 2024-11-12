
package kr.brain.our_app.bookmark.controller;

import kr.brain.our_app.bookmark.domain.Bookmark;
import kr.brain.our_app.bookmark.domain.TagBookmark;
import kr.brain.our_app.bookmark.dto.BookmarkDto;
import kr.brain.our_app.bookmark.dto.RequestFrontDto;
import kr.brain.our_app.bookmark.dto.TagBookmarkDto;
import kr.brain.our_app.bookmark.repository.BookmarkRepository;
import kr.brain.our_app.bookmark.service.BookmarkService;
import kr.brain.our_app.bookmark.service.TagBookmarkService;
import kr.brain.our_app.tag.domain.Tag;
import kr.brain.our_app.tag.dto.TagDto;
import kr.brain.our_app.tag.repository.TagRepository;
import kr.brain.our_app.tag.service.TagService;
import kr.brain.our_app.user.dto.UserDto;
import kr.brain.our_app.user.service.UserService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tagbookmark")
@CrossOrigin(origins = "chrome-extension://jdceomhmccgnolblojlknlhopaoikoda")  // Chrome 확장 프로그램의 origin 추가
public class TagBookmarkController {

    private final TagBookmarkService tagBookmarkService;
    private final TagService tagService;
    private final BookmarkService bookmarkService;
    private final UserService userService;


    @Autowired
    public TagBookmarkController(TagBookmarkService tagBookmarkService, TagService tagService
            , BookmarkService bookmarkService
            , UserService userService){
        this.tagBookmarkService = tagBookmarkService;
        this.bookmarkService = bookmarkService;
        this.tagService = tagService;
        this.userService = userService;
    }

    //0. front에서 request 받고 처리하는 부분
    @PostMapping("/in")
    public ResponseEntity<List<TagBookmarkDto>> createTagBookmarksFromRequest(@RequestBody RequestFrontDto requestFrontDto){
        List<TagBookmarkDto> tagBookmarkDtos = tagBookmarkService.requestTagBookmark(requestFrontDto);
        return ResponseEntity.ok(tagBookmarkDtos);
    }

    @GetMapping("/out")
    public ResponseEntity<List<BookmarkDto>> responseTagBookmark(@RequestParam String tagName
            , @RequestParam String userEmail){
        System.out.println("Received tagName: " + tagName);  //test
        System.out.println("Received userEmail: " + userEmail);  //test

        List<BookmarkDto>bookmarkDtos = tagBookmarkService.responseTagBookmark(tagName, userEmail);
        System.out.println("Returning bookmarks: " + bookmarkDtos);  // test

        //TODO 반환 방식에 대한 회의 필요 tag -> bookmarkName or bookmarkURL or show all of them
        return ResponseEntity.ok(bookmarkDtos);
    }

    // 1. TagBookmark 생성
//    @PostMapping
//    public ResponseEntity<TagBookmarkDto> createTagBookmark(@RequestBody TagBookmarkDto tagBookmarkDto) {
//        TagBookmarkDto createdTagBookmark = tagBookmarkService.createTagBookmark(tagBookmarkDto.getTagId(), tagBookmarkDto.getBookmarkId());
//        return ResponseEntity.ok(createdTagBookmark);
//    }

//    // 2. 특정 Bookmark에 연결된 모든 Tag 조회
//    @GetMapping("/by-bookmark/{bookmarkName}")
//    public ResponseEntity<List<TagBookmarkDto>> getTagsByBookmark(@PathVariable String bookmarkName ,
//                                                                  @RequestBody UserDto userDto) {
//        BookmarkDto bookmarkDto = bookmarkService.findByBookmarkName(bookmarkName, userDto);
//        List<TagBookmarkDto> tagBookmarkDtos = tagBookmarkService.findAllByBookmark(bookmarkDto);
//        return ResponseEntity.ok(tagBookmarkDtos);
//    }
//
//    // 3. 특정 Tag에 연결된 모
//    // 든 Bookmark 조회
//    @GetMapping("/by-tag/{tagName}")
//    public ResponseEntity<List<TagBookmarkDto>> getBookmarksByTag(@PathVariable String tagName ,
//                                                                  @RequestBody UserDto userdto) {
//        TagDto tagDto = tagService.findByTagName(tagName , userdto);
////                .orElseThrow(() -> new IllegalArgumentException("Tag not found"));
////                  tag서비스 내부에서 예외처리를 하면서, 컨트롤러에서 예외처리할 필요가 없어짐
//        List<TagBookmarkDto> tagBookmarkDtos = tagBookmarkService.findAllByTag(tagDto);
//        return ResponseEntity.ok(tagBookmarkDtos);
//    }

    //TODO 지금 userdto추가하면서 @RequestBody로 받는다고 가정하고, findByTagName으로 만들었다.

    /*************************************************************************************/
//    private final TagRepository tagRepository;
//    private final BookmarkRepository bookmarkRepository;
//
//    @Autowired
//    public TagBookmarkController(TagBookmarkService tagBookmarkService, TagRepository tagRepository,
//                                 BookmarkRepository bookmarkRepository) {
//        this.tagBookmarkService = tagBookmarkService;
//        this.tagRepository = tagRepository;
//        this.bookmarkRepository = bookmarkRepository;
//    }
//
//    // 1. bookmark id와 tag id로 태그와 북마크의 결합 생성
//    @PostMapping
//    public ResponseEntity<TagBookmark> createTagBookmark(@RequestParam String tagName, @RequestParam String bookmarkName) {
//        TagBookmark tagbookmark = tagBookmarkService.createTagBookmark(tagName, bookmarkName);
//        return ResponseEntity.ok(tagbookmark);
//    }
//
//    // 2. 특정 태그에 속한 북마크들을 페이징 형식으로 조회
//    @GetMapping("/{tagId}/bookmarks")
//    public ResponseEntity<Page<TagBookmark>> getBookmarksByTag(@PathVariable Long tagId, Pageable pageable) {
//        Tag tag = tagRepository.findById(tagId).orElseThrow(() ->
//                new IllegalArgumentException("해당 ID의 태그를 찾을 수 없습니다."));
//        Page<TagBookmark> bookmarks = tagBookmarkService.getBookmarksByTag(tag, pageable);
//
//        return ResponseEntity.ok(bookmarks);
//    }
//
//    // 3. 태그와 북마크의 결합 삭제 -> 일부 북마크에서
//    @DeleteMapping
//    public ResponseEntity<Void> removeTagBookmark(@RequestParam Long tagId, @RequestBody Bookmark bookmark) {
//        Tag tag = tagRepository.findById(tagId).orElseThrow(() ->
//                new IllegalArgumentException("해당 ID의 태그를 찾을 수 없습니다."));
//        tagBookmarkService.removeTagBookmark(tag, bookmark);
//        return ResponseEntity.noContent().build();
//    }
//
//    // 4. 특정 태그와 관련된 모든 결합 삭제 -> 태그 삭제 시 결합 모두 삭제, 이거 태그에서 구현 해야함
//    // 근데 아직은 구현 안 함
//    @DeleteMapping("/{tagId}/all-bookmarks")
//    public ResponseEntity<Void> deleteAllTagBookmarks(@PathVariable Long tagId) {
//        tagBookmarkService.deleteAllByTagId(tagId);
//        return ResponseEntity.noContent().build();
//    }

/*******************************************************************************************/

//    //2. 태그명으로 북마크 조회
//    @GetMapping("/{tagName}/bookmarks")
//    public ResponseEntity<List<Bookmark>> sgetBookmarksByTagName(@PathVariable String tagName) {
//        List<Bookmark> bookmarks = bookmarkService.getBookmarksByTagName(tagName); // 서비스에서 메서드가 필요함
//        return ResponseEntity.ok(bookmarks);  // 빈 리스트가 반환되어도 200 OK
//    }
//
//    //3. 북마크에 어떤 태그가 있는지 조회하는 API
//    @GetMapping("/bookmarks/{bookmarkId}/tags")
//    public ResponseEntity<List<Tag>> getTagsByBookmarkId(@PathVariable Long bookmarkId) {
//        List<Tag> tags = tagBookmarkService.getTagsByBookmarkId(bookmarkId); // 서비스에서 메서드가 필요함
//        return ResponseEntity.ok(tags);
//    }

    // 위 주석처리한 두개는 tagbookmark controlelr에서 다룰 예정.

}


// json 으로 postman으로 db 연결 되고있는지 확인하고,
// front에서 json으로 데이터 전송 -> postman에서 controller api 로 데이터 전송해보라.