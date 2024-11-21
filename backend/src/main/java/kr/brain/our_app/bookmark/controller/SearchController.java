package kr.brain.our_app.bookmark.controller;


import kr.brain.our_app.bookmark.dto.SearchDto;
import kr.brain.our_app.bookmark.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
//@CrossOrigin(origins = "chrome-extension://fobkchapnlendiediceimjfimlphdkin/")  // Chrome 확장 jun id
//@CrossOrigin(origins = "chrome-extension://nmppigpifceknpehjphklpnpofimlmea/")  // Chrome 확장 프로그램의 origin 추가

public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public ResponseEntity<SearchDto> searchByWord(@RequestParam String word, @RequestParam String email) {
        SearchDto searchResult = searchService.searchByWord(word, email);
        return ResponseEntity.ok(searchResult);
    }
}