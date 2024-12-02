package kr.brain.our_app.bookmark.controller;


import kr.brain.our_app.bookmark.domain.TagBookmark;
import kr.brain.our_app.bookmark.dto.BookmarkDto;
import kr.brain.our_app.bookmark.dto.RequestFrontDto;
import kr.brain.our_app.bookmark.dto.TagBookmarkDto;
import kr.brain.our_app.bookmark.service.BookmarkService;
import kr.brain.our_app.bookmark.service.TagBookmarkService;
import kr.brain.our_app.tag.service.TagService;
import kr.brain.our_app.user.dto.UserDto;
import kr.brain.our_app.tag.dto.TagDto;
import kr.brain.our_app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tagbookmark")
//@CrossOrigin(origins = "chrome-extension://fobkchapnlendiediceimjfimlphdkin/")  // Chrome 확장 jun id
public class TagBookmarkController {

    private final TagBookmarkService tagBookmarkService;
    private final TagService tagService;
    private final BookmarkService bookmarkService;
    private final UserService userService;


    @Autowired
    public TagBookmarkController(TagBookmarkService tagBookmarkService, TagService tagService
            , BookmarkService bookmarkService
            , UserService userService) {
        this.tagBookmarkService = tagBookmarkService;
        this.bookmarkService = bookmarkService;
        this.tagService = tagService;
        this.userService = userService;
    }

    //0. front에서 request 받고 처리하는 부분
    @PostMapping("/in")
    public ResponseEntity<List<TagBookmarkDto>> createTagBookmarksFromRequest(@RequestBody RequestFrontDto requestFrontDto) {
        List<TagBookmarkDto> tagBookmarkDtos = tagBookmarkService.requestTagBookmark(requestFrontDto);
        return ResponseEntity.ok(tagBookmarkDtos);
    }

    @GetMapping("/outAll")
    public ResponseEntity<List<RequestFrontDto>> sendTagBookmarksFromRequest(@RequestParam String userEmail) {
        List<RequestFrontDto> responseDtos = tagBookmarkService.responseAllTagBookmark(userEmail);
        return ResponseEntity.ok(responseDtos);
    }


//    @GetMapping("/outAll")
//    public ResponseEntity<List<RequestFrontDto>> sendTagBookmarksFromRequest(@RequestParam String userEmail) {
//        // 사용자 조회 (예외 처리는 추후 추가 필요)
//        UserDto userDto = userService.findByEmail(userEmail);
//        String userId = userDto.getId();
//
//        // 북마크 목록 조회
//        List<BookmarkDto> bookmarks = bookmarkService.findAllBookmarks(userDto);
//
//        // 태그 조회 및 DTO 생성
//        List<RequestFrontDto> responseDtos = bookmarks.stream()
//                .map(bookmark -> {
//                    // 각 북마크에 연결된 태그 조회
//                    List<String> tags = tagBookmarkService.findTagsByBookmarkName(bookmark.getBookmarkName(), userId).stream()
//                            .map(TagDto::getTagName)
//                            .collect(Collectors.toList());
//
//                    // RequestFrontDto 생성
//                    return RequestFrontDto.builder()
//                            .title(bookmark.getBookmarkName())
//                            .url(bookmark.getUrl())
//                            .tags(tags)
//                            .email(userEmail)
//                            .userName(userDto.getUserName())
//                            .build();
//                })
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(responseDtos);
//    }

    @GetMapping("/out")
    public ResponseEntity<List<BookmarkDto>> responseTagBookmark(@RequestParam String tagName
            , @RequestParam String userEmail) {
        System.out.println("Received tagName: " + tagName);  //test
        System.out.println("Received userEmail: " + userEmail);  //test

        List<BookmarkDto> bookmarkDtos = tagBookmarkService.responseTagBookmark(tagName, userEmail);
        System.out.println("Returning bookmarks: " + bookmarkDtos);  // test

        //TODO 반환 방식에 대한 회의 필요 tag -> bookmarkName or bookmarkURL or show all of them
        return ResponseEntity.ok(bookmarkDtos);
    }

    //검색창 최초 접속 시 가지고 있는 모든 bookmark를 출력하도록한다.
    @GetMapping("/onloadsearch")
    public List<BookmarkDto> listBookmarksOnSearchLoad(@RequestBody String Userid) {
        UserDto dto = userService.findById(Userid);
        List<BookmarkDto> bookmarks = bookmarkService.findAllBookmarks(dto);
        return bookmarks;
    }

//    //word를 tag에서 검색해서
//    @GetMapping("/search")
//    public ResponseEntity<List<TagBookmarkDto>> searchTagBookmark(@RequestParam String word, @RequestParam String Email) {
//        String userid = userService.findByEmail(Email).getId(); // 예외처리 할 필요 x 이미 로그인O, 잘못들어올리X
//
//    }



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
//    // 3. 태그 삭제시 tag삭제 + 연결된 tagbookmark 모두 삭제
    // 태그삭제는 아직 고려사항이 아니기에, 북마크 삭제만 고안함
//    @DeleteMapping("/{tagId}")
//    public ResponseEntity<Void> deleteTag(@PathVariable String tagId){
//        tagService.
//
//    }

    // 4. bookmark 삭제 + 연결된 tagbookmark 모두 삭제
    @DeleteMapping("/{bookmarkId}")
    public ResponseEntity<Void> deleteBookmark(@PathVariable String BookmarkId) {
        bookmarkService.deleteBookmark(BookmarkId);
        return ResponseEntity.noContent().build();
        //위의 return은 delete 시에 주로 사용되는 204 no content로, 삭제한 정보를 다시 업데이트 할 필요가없을때
        //주로 사용된다.
    }

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