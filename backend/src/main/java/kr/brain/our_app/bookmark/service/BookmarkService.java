package kr.brain.our_app.bookmark.service;

import kr.brain.our_app.bookmark.domain.Bookmark;
import kr.brain.our_app.bookmark.dto.BookmarkDto;
import kr.brain.our_app.bookmark.repository.BookmarkRepository;
import kr.brain.our_app.idsha.IDGenerator;
import kr.brain.our_app.tag.dto.TagDto;
import kr.brain.our_app.user.domain.User;
import kr.brain.our_app.user.dto.UserDto;
import kr.brain.our_app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final UserService userService;

    @Autowired
    public BookmarkService(BookmarkRepository bookmarkRepository, UserService userService) {
        this.bookmarkRepository = bookmarkRepository;
        this.userService = userService;
    }

    // 1. 북마크 저장
    public BookmarkDto createBookmark(BookmarkDto bookmarkDto, UserDto userDto) {
        // 유저 ID로 User 객체 조회
        UserDto findUserDto = userService.findById(userDto.getId());

        //FIXME entity 변경 로직 추가 toEntity 메서드 추가 고려하는게 좋을 듯...
        User user = User.builder()
                .id(findUserDto.getId())
                .userName(findUserDto.getUserName())
                .email(findUserDto.getEmail())
                .build();

        String createbookmarkId = IDGenerator.generateId(bookmarkDto.getBookmarkName()+user.getId());
        //user 객체를 전달해서 setUser(user) 전달x

        if(existsById(createbookmarkId)) {
            throw new IllegalArgumentException("Bookmark already exists");
        }

        Bookmark bookmark = Bookmark.builder()
                .id(createbookmarkId)
                .bookmarkName(bookmarkDto.getBookmarkName())
                .url(bookmarkDto.getUrl())
                .user(user)
                .build();

        Bookmark savedBookmark = bookmarkRepository.save(bookmark);

        return BookmarkDto.builder()
                .id(savedBookmark.getId())
                .bookmarkName(savedBookmark.getBookmarkName())
                .url(savedBookmark.getUrl())
                .build();
    }

    // 2. 북마크 전체 조회
    public List<BookmarkDto> findAllBookmarks(UserDto userDto) {
        List<Bookmark> bookmarks = bookmarkRepository.findAllByUser_Id(userDto.getId());

        if (bookmarks.isEmpty()) {
            throw new IllegalArgumentException("해당 사용자는 북마크를 가지고 있지 않습니다.");
        }

        return bookmarks.stream()
                .map(bookmark -> BookmarkDto.builder()
                        .id(bookmark.getId())
                        .bookmarkName(bookmark.getBookmarkName())
                        .url(bookmark.getUrl())
                        .build())
                .collect(Collectors.toList());
    }
    //2번과 3번은 일부러 예외처리 방식을 다르게 했는데, 2는 좀더 명확한 예외상황을 볼 수있다는 장점이 있고
    //3번은 만들기 좀 더 쉽다는 장점이 있다. 한번 의논해보고 예외처리 방식을 통일하면 좋을 것 같다.

    // 3. 이름으로 북마크 찾기
    public BookmarkDto findByBookmarkName(String bookmarkName, UserDto userDto) {
        return bookmarkRepository.findByBookmarkNameAndUser_Id(bookmarkName, userDto.getId())
                .map(bookmark -> BookmarkDto.builder()
                        .id(bookmark.getId())
                        .bookmarkName(bookmark.getBookmarkName())
                        .url(bookmark.getUrl())
                        .build())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 bookmarkName을 가진 bookmark가 존재하지 않습니다: " + bookmarkName));
    }

    // 4. 북마크 삭제
    public void deleteBookmark(String bookmarkId) {
        // 1. Bookmark ID로 객체 조회
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID를 가진 Bookmark가 존재하지 않습니다."));

        // 2. Bookmark 객체 삭제
        bookmarkRepository.delete(bookmark);
    }

    // 5. 북마크 아이디로 북마크 조회
    public BookmarkDto findBookmarkById(String bookmarkId) {
        return bookmarkRepository.findById(bookmarkId) // Optional<Bookmark>를 반환하는 메서드 사용
                .map(bookmark -> new BookmarkDto(
                        bookmark.getId(),
                        bookmark.getBookmarkName(),
                        bookmark.getUrl()
                )).orElseThrow(()-> new IllegalArgumentException("해당 ID를 가진 Bookmark를 찾을 수 없습니다."));
    }
    public boolean existsById(String bookmarkId){
        return bookmarkRepository.existsById(bookmarkId);
    }
    public boolean existsByBookmarkName(String bookmarkName, String userId){
        return bookmarkRepository.existsByBookmarkNameAndUser_Id(bookmarkName, userId);
    }
}

