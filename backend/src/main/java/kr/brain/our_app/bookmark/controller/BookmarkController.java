package kr.brain.our_app.bookmark.controller;
import kr.brain.our_app.bookmark.domain.Bookmark;
import kr.brain.our_app.bookmark.dto.BookmarkDto;
import kr.brain.our_app.bookmark.service.BookmarkService;
import kr.brain.our_app.user.domain.User;
import kr.brain.our_app.user.dto.UserDto;
import kr.brain.our_app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bookmark")
public class BookmarkController {

    private final BookmarkService bookmarkService;
    private final UserService userService;

    @Autowired
    public BookmarkController(BookmarkService bookmarkService, UserService userService){

        this.bookmarkService = bookmarkService;
        this.userService = userService;
    }

    // 1. 북마크 생성
    @PostMapping
    public ResponseEntity<BookmarkDto> createBookmark(@RequestBody BookmarkDto bookmarkDto,
                                                      @RequestParam String userId) {
        UserDto userDto = userService.findById(userId);

        BookmarkDto savedBookmark = bookmarkService.createBookmark(bookmarkDto, userDto);
        return ResponseEntity.ok(savedBookmark);
    }

    // 2. 모든 북마크 조회
    @GetMapping("/all")
    public ResponseEntity<List<BookmarkDto>> getAllBookmarks(@RequestParam String userId) {
        UserDto userDto = userService.findById(userId);
        List<BookmarkDto> bookmarks = bookmarkService.findAllBookmarks(userDto);
        return ResponseEntity.ok(bookmarks);
    }

    // 3. 이름으로 북마크 조회
    @GetMapping("/by-name")
    public ResponseEntity<BookmarkDto> getBookmarkByName(@RequestParam String bookmarkName, @RequestParam String userId) {
        UserDto userDto = userService.findById(userId);
        BookmarkDto bookmark = bookmarkService.findByBookmarkName(bookmarkName, userDto);
        return ResponseEntity.ok(bookmark);
    }

    // 4. 북마크 ID로 북마크 조회
    @GetMapping("/{bookmarkId}")
    public ResponseEntity<BookmarkDto> getBookmarkById(@PathVariable String bookmarkId) {
        BookmarkDto bookmark = bookmarkService.findBookmarkById(bookmarkId);
        return ResponseEntity.ok(bookmark);
    }

    // 5. 북마크 삭제
    @DeleteMapping("/{bookmarkId}")
    public ResponseEntity<Void> deleteBookmark(@PathVariable String bookmarkId) {
        bookmarkService.deleteBookmark(bookmarkId);
        return ResponseEntity.noContent().build();
    }
}