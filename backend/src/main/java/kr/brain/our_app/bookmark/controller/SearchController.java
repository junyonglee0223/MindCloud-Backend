package kr.brain.our_app.bookmark.controller;


import kr.brain.our_app.bookmark.dto.BookmarkWithTagsDto;
import kr.brain.our_app.bookmark.dto.ModifyDto;
import kr.brain.our_app.bookmark.dto.SearchDto;
import kr.brain.our_app.bookmark.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")

public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public ResponseEntity<SearchDto> searchByWord(@RequestParam String word,
                                                  @RequestParam String email) {
        SearchDto searchResult = searchService.searchByWord(word, email);
        return ResponseEntity.ok(searchResult);
    }

    // 북마크 삭제 API
    @DeleteMapping("/bookmarks")
    public ResponseEntity<String> deleteBookmark(@RequestParam String bookmarkName,
                                                 @RequestParam String email) {
        searchService.deleteBookmarkInSearch(bookmarkName, email);
        return ResponseEntity.ok("북마크가 삭제되었습니다.");
    }

    @PostMapping("/modify")
    public ResponseEntity<BookmarkWithTagsDto> modifyBookmarkWithTags(@RequestBody ModifyDto modifyDto) {
        BookmarkWithTagsDto modifiedBookmark = searchService.modifyBWTD(modifyDto);
        // 결과 반환
        return ResponseEntity.ok(modifiedBookmark);
    }//


}