package kr.brain.our_app.bookmark.service;

import kr.brain.our_app.bookmark.domain.Bookmark;
import kr.brain.our_app.bookmark.domain.TagBookmark;
import kr.brain.our_app.bookmark.dto.BookmarkDto;
import kr.brain.our_app.bookmark.dto.RequestFrontDto;
import kr.brain.our_app.bookmark.dto.TagBookmarkDto;
import kr.brain.our_app.bookmark.repository.BookmarkRepository;
import kr.brain.our_app.bookmark.repository.TagBookmarkRepository;
import kr.brain.our_app.idsha.IDGenerator;
import kr.brain.our_app.image.service.S3Service;
import kr.brain.our_app.tag.domain.Tag;
import kr.brain.our_app.tag.dto.TagDto;
import kr.brain.our_app.tag.repository.TagRepository;
import kr.brain.our_app.tag.service.TagService;
import kr.brain.our_app.user.domain.User;
import kr.brain.our_app.user.dto.UserDto;
import kr.brain.our_app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagBookmarkService {

    private final TagBookmarkRepository tagBookmarkRepository;

    private final BookmarkService bookmarkService;
    private final TagService tagService;
    private final UserService userService;
    private final S3Service s3Service;

    @Autowired
    public TagBookmarkService(TagBookmarkRepository tagBookmarkRepository,
                              BookmarkService bookmarkService,
                              TagService tagService,
                              UserService userService,
                              S3Service s3Service,
                              TagRepository tagRepository,
                              BookmarkRepository bookmarkRepository) {
        this.tagBookmarkRepository = tagBookmarkRepository;
        this.bookmarkService = bookmarkService;
        this.tagService = tagService;

        this.userService = userService;
        this.s3Service = s3Service;
    }

    //WARN User 생성되는게 어디서 생성되는지 아직 몰라서 tagbookmark 컨트롤러에서 user 생성하는 걸로 두긴했는데,
    //WARN 정확하게 어디서 생성되는지, 로직을 수정해야하는지 등을

    public List<TagBookmarkDto> requestTagBookmark(RequestFrontDto requestFrontDto){
        System.out.println(requestFrontDto);    //TEST

        //WARN 해당 코드 처리하는 방식 나중에 user에서 처리하는 과정 필요함
        if(!userService.existsByEmail(requestFrontDto.getEmail())){
            UserDto createUserDto = UserDto.builder()
                    .id("makeKey")
                    .userName(requestFrontDto.getUserName())
                    .email(requestFrontDto.getEmail())
                    .build();
            userService.createUser(createUserDto);
        }

        UserDto userDto = userService.findByEmail(requestFrontDto.getEmail());

        String title = requestFrontDto.getTitle();
        String url = requestFrontDto.getUrl();

        //no id bookmarkdto
        BookmarkDto bookmarkDto = BookmarkDto.builder()
                .bookmarkName(requestFrontDto.getTitle())
                .url(requestFrontDto.getUrl())
                .build();

        if(!bookmarkService.existsByBookmarkName(title, userDto.getId())){
            bookmarkService.createBookmark(bookmarkDto, userDto);

            String imageUrl = null;
            if(requestFrontDto.getImageUrl() != null){
                try{
                    imageUrl = requestFrontDto.getImageUrl();
                    s3Service.uploadImageFromUrl(imageUrl, title, userDto.getId());
                }catch (Exception e){
                    System.out.println("s3 이미지 업로드 실패");
                }
            }
        }

        BookmarkDto checkedBookmarkDto
                = bookmarkService
                .findByBookmarkName(bookmarkDto.getBookmarkName(),userDto);

        List<TagBookmarkDto>tagBookmarkDtoList = new ArrayList<>();

        for(String tagName : requestFrontDto.getTags()){
            TagDto tagDto = TagDto.builder()
                    .tagName(tagName)
                    .build();
            if(!tagService.existsByTagName(tagName, userDto.getId())){
                tagService.createTag(tagDto, userDto);
            }
            TagDto checkedTagDto
                    = tagService.findByTagName(tagName, userDto.getId());
//                    .orElseThrow(IllegalArgumentException::new);
            //service 내에서 예외처리하면서 삭제함
            //entity check -> create dto
            //check logic is impied in other method
            if(!existsByTagIdAndBookmarkId(checkedTagDto.getId(), checkedBookmarkDto.getId())){
                createTagBookmark(checkedTagDto.getId(), checkedBookmarkDto.getId(), userDto.getId());
            }

            TagBookmarkDto checkedTagBookmarkDto =
                    findTagBookmarkByTagAndBookmark(checkedTagDto, checkedBookmarkDto);

            tagBookmarkDtoList.add(checkedTagBookmarkDto);
        }
        return tagBookmarkDtoList;
    }

    public List<RequestFrontDto> responseAllTagBookmark(String userEmail){
        UserDto userDto = userService.findByEmail(userEmail);
        String userId = userDto.getId();

        // 북마크 목록 조회
        List<BookmarkDto> bookmarks = bookmarkService.findAllBookmarks(userDto);

        // 태그 조회 및 DTO 생성
        List<RequestFrontDto> responseDtos = bookmarks.stream()
                .map(bookmark -> {
                    // 각 북마크에 연결된 태그 조회
                    List<String> tags = findTagsByBookmarkName(bookmark.getBookmarkName(), userId).stream()
                            .map(TagDto::getTagName)
                            .collect(Collectors.toList());
                    String imageUrl = s3Service.getFileUrl(bookmark.getBookmarkName(), userId);

                    // RequestFrontDto 생성
                    return RequestFrontDto.builder()
                            .title(bookmark.getBookmarkName())
                            .url(bookmark.getUrl())
                            .tags(tags)
                            .imageUrl(imageUrl)
                            .email(userEmail)
                            .userName(userDto.getUserName())
                            .build();
                })
                .collect(Collectors.toList());

        return  responseDtos;
    }


    //tag 요청했을 경우 bookmarkDto 반환하는 서비스 로직
    //이거는 search service의 searchByword 함수가 대체하고 있다. 지워도 될 것 같지만 남겨둔다.
    public List<BookmarkDto> responseTagBookmark(String tagName, String userEmail){
        String userID = userService.findByEmail(userEmail).getId();

        //FIXME user dto로 받아야 하는지?? -> service가 dto로 받고 있는 상황
        // -> 나중에 string 입력받는 방식으로 수정해야 함

        UserDto userDto = UserDto.builder()
                .id(userID).build();
        String tagId = tagService.findByTagName(tagName, userDto.getId()).getId();
        List<TagBookmarkDto> tagBookmarkDtos = findByTagId(tagId);

        List<BookmarkDto> bookmarkDtos = new ArrayList<>();
        for(TagBookmarkDto tbd : tagBookmarkDtos){
            BookmarkDto bookmarkDto = bookmarkService.findBookmarkById(tbd.getBookmarkId());
            bookmarkDtos.add(bookmarkDto);
        }
        return bookmarkDtos;
    }

    //tag랑 bookmark로 tagbookmark 객체 찾기
    public TagBookmarkDto findTagBookmarkByTagAndBookmark(TagDto tagDto, BookmarkDto bookmarkDto
    ){
        if(!tagService.existsById(tagDto.getId()) || !bookmarkService.existsById(bookmarkDto.getId())){
            throw new IllegalArgumentException("Tag or Bookmark does not exist");
        }

        return tagBookmarkRepository
                .findByTagIdAndBookmarkId(tagDto.getId(), bookmarkDto.getId())
                .map(tagBookmark -> TagBookmarkDto.builder()
                        .tagId(tagDto.getId())
                        .bookmarkId(bookmarkDto.getId())
                        .build())
                .orElseThrow(null);
    }

    public TagBookmarkDto createTagBookmark(String tagId, String bookmarkId, String userId){
        //FIXME user entity 만드는 부분 여기에 구현해놨음
        // 1. dto에 user_id를 추가해서 통일성을 맞춘다.   -> 통일성 맞춤
        // 2. tagbookmark 생성시 userDto를 지속적으로 입력한다. -> 전체 refactoring 필요
        // 3. create 하는 부분에서만 user entity 필요함 find 영역에서는 필요없음
        // 4. id가 들어오는지 name인지 모르긴 하지만 일단 id로 두는걸로

        if (tagBookmarkRepository.existsByTagIdAndBookmarkId(tagId, bookmarkId)) {
            return null;
        }
        UserDto currentUserDto = userService.findById(userId);

        User user = User.builder()
                .id(currentUserDto.getId())
                .userName(currentUserDto.getUserName())
                .email(currentUserDto.getEmail())
                .build();

        TagDto tagDto = tagService.findById(tagId);
        //TODO user entity를 입력해 줘야 하는 문제 있음(반복적인 코드)
        Tag tag = Tag.builder()
                .id(tagDto.getId())
                .tagName(tagDto.getTagName())
                .user(user)
                .build();

        //TODO set 설정 관련 문제 해결해야 함
        BookmarkDto bookmarkDto = bookmarkService.findBookmarkById(bookmarkId);

        Bookmark bookmark = Bookmark.builder()
                .id(bookmarkDto.getId())
                .bookmarkName(bookmarkDto.getBookmarkName())
                .url(bookmarkDto.getUrl())
                .user(user)
                .build();

        String createTagBookmarkId
                = IDGenerator.generateId(tagId + bookmarkId);


        // 새로 tagbookmark 객체 생성
        TagBookmark tagBookmark = TagBookmark.builder()
                .id(createTagBookmarkId)
                .tag(tag)
                .bookmark(bookmark).build();

        TagBookmark savedTagBookmark = tagBookmarkRepository.save(tagBookmark);

        return TagBookmarkDto.builder()
                .tagId(savedTagBookmark.getTag().getId())
                .bookmarkId(savedTagBookmark.getBookmark().getId())
                .build();
    }

    List<TagBookmarkDto> findByTagId(String tagId){
        return tagBookmarkRepository.findByTagId(tagId).stream()
                .map(tagBookmark -> TagBookmarkDto.builder()
                        .tagId(tagBookmark.getTag().getId())
                        .bookmarkId(tagBookmark.getBookmark().getId())
                        .build())
                .collect(Collectors.toList());
    }

    List<TagBookmarkDto> findByBookmarkId(String bookmarkId){
        return tagBookmarkRepository.findByBookmarkId(bookmarkId).stream()
                .map(tagBookmark -> TagBookmarkDto.builder()
                        .tagId(tagBookmark.getTag().getId())
                        .bookmarkId(tagBookmark.getBookmark().getId())
                        .build())
                .collect(Collectors.toList());
    }

    public boolean existsByTagIdAndBookmarkId(String tagId, String bookmarkId){
        return tagBookmarkRepository.existsByTagIdAndBookmarkId(tagId, bookmarkId);
    }

    //FIXME 리턴 방식 다시 생각해봐야함
    //bookmark name과 userid로
    public BookmarkDto searchBookmarkByBookmarkName(String bookmarkName , String userId) {
        // 사용자가 검색란에 bookmarkname을 입력, bookmarkdto 반환front에서 userId를 쏴준 상황
        if(!bookmarkService.existsByBookmarkName(bookmarkName,userId)){
            throw new IllegalArgumentException("Bookmark does not exist");
        }
        UserDto currentUserDto = userService.findById(userId);
        BookmarkDto bookmarkDto = bookmarkService.findByBookmarkName(bookmarkName,currentUserDto);
        return bookmarkDto;
    }

    //bookmarkname으로 bookmark가 가지고 있는 태그들 뽑아내는 메서드 -> tagdto 형식으로 반환. 다양하게 사용 가능
    public List<TagDto> findTagsByBookmarkName(String bookmarkName, String userId) {
        if (bookmarkService.existsByBookmarkName(bookmarkName, userId)) {
            UserDto userDto = userService.findById(userId);
            BookmarkDto bookmarkDto = bookmarkService.findByBookmarkName(bookmarkName, userDto);

            List<TagBookmarkDto> tagBookmarkDtos = tagBookmarkRepository.findByBookmarkId(bookmarkDto.getId())
                    .stream()
                    .map(tagBookmark -> TagBookmarkDto.builder()
                            .bookmarkId(tagBookmark.getBookmark().getId()) // 올바른 bookmarkId
                            .tagId(tagBookmark.getTag().getId()) // 올바른 tagId
                            .build())
                    .toList();

            List<TagDto> tagDtos = new ArrayList<>();
            for (TagBookmarkDto tbd : tagBookmarkDtos) {
                tagDtos.add(tagService.findById(tbd.getTagId())); // 정확한 TagDto 조회
            }
            return tagDtos;
        } else {
            return Collections.emptyList();
        }
    }


    // TagName을 입력받고 해당 tag에 속하는 bookmark들을 모두 출력하는 메서드
    public List<BookmarkDto> findBookmarksByTagName(String tagName, String userId) {
        if (tagService.existsByTagName(tagName, userId)) { // Tag가 존재하는지 확인
            // UserDto와 TagDto를 가져옵니다.
            UserDto userDto = userService.findById(userId);
            TagDto tagDto = tagService.findByTagName(tagName, userDto.getId());

            // tagName에 해당하는 TagBookmarks를 가져옵니다.
            List<TagBookmark> tagBookmarks = tagBookmarkRepository.
                    findTagBookmarksByTag_TagNameAndTag_User_Id(tagDto.getTagName() , userId);

            // tag에 속한 bookmark가 없는 경우 빈 리스트 반환
            if (tagBookmarks.isEmpty()) {
                return Collections.emptyList();
            }

            // TagBookmark에서 BookmarkDto에 접근해서 찾아오기
            return tagBookmarks.stream()
                    .map(tagBookmark -> bookmarkService.findBookmarkById
                            (tagBookmark.getBookmark().getId()))
                    .collect(Collectors.toList());
        }// tagbookmark에서getbookmark()를 가져오는데, 그거의 id를 바다와서 bookmark객체를 return 받음
        //.collect(Collectors.toList()): 스트림에서 변환된 BookmarkDto 객체들을 리스트로 수집하여 반환합니다.
        else {
            // 해당 태그가 존재하지 않는 경우 빈 리스트 반환
            return Collections.emptyList();
        }
    }

//    public List<TagBookmarkDto> findAllByTag(TagDto tagDto) {
//        // TagDto의 정보를 바탕으로 Tag 엔티티 조회
//        Tag tag = tagRepository.findByTagName(tagDto.getTagName())
//                .orElseThrow(() -> new IllegalArgumentException("Tag not found"));
//
//        return tagBookmarkRepository.findAllByTag(tag)
//                .stream()
//                .map(tagBookmark -> TagBookmarkDto.builder()
//                        .tagName(tagBookmark.getTag().getTagName())
//                        .bookmarkName(tagBookmark.getBookmark().getBookmarkName())
//                        .build())
//                .collect(Collectors.toList());
//    }

//    // 2. TagId로 연결된 모든 Bookmark 조회
//    public List<Bookmark> getBookmarksByTagId(Long tagId) {
//        Tag tag = tagRepository.findById(tagId)
//                .orElseThrow(() -> new IllegalArgumentException("Tag not found"));
//
//        List<TagBookmark> tagBookmarks = tagBookmarkRepository.findByTag(tag);
//
//        // 각 TagBookmark에서 Bookmark만 추출하여 반환
//        return tagBookmarks.stream()
//                .map(TagBookmark::getBookmark)
//                .collect(java.util.stream.Collectors.toList());
//    }
//
//    // 3. TagName으로 연결된 모든 Bookmark 조회
//    public List<Bookmark> getBookmarksByTagName(String tagName) {
//        Optional<Tag> tag = tagRepository.findByTagName(tagName); // 단일 Tag 반환
//
//        if (tag == null) {
//            throw new IllegalArgumentException("해당 TagName을 가진 Tag 객체가 존재하지 않음");
//        }
//
//    }

//    // 4. BookmarkId로 연결된 모든 Tag 조회
//    public List<Tag> getTagsByBookmarkId(Long bookmarkId) {
//        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
//                .orElseThrow(() -> new IllegalArgumentException("Bookmark not found"));
//
//        List<TagBookmark> tagBookmarks = tagBookmarkRepository.findByBookmark(bookmark);
//
//        // 각 TagBookmark에서 Tag만 추출하여 반환
//        return tagBookmarks.stream()
//                .map(TagBookmark::getTag)
//                .collect(java.util.stream.Collectors.toList());
//    }
//
//    // 5. BookmarkName으로 연결된 모든 Tag 조회
//    public List<Tag> getTagsByBookmarkName(String bookmarkName) {
//        // BookmarkRepository에서 List<Bookmark>가 반환된다고 가정
//        List<Bookmark> bookmarks = bookmarkRepository.findByBookmarkName(bookmarkName);
//
//        // 빈 리스트일 경우 예외 처리
//        if (bookmarks.isEmpty()) {
//            throw new IllegalArgumentException("Bookmark not found");
//        }
//
//        // 첫 번째 북마크의 ID를 사용해 태그를 조회
//        Bookmark bookmark = bookmarks.get(0); // 만약 여러 북마크가 있으면 첫 번째만 사용 (필요시 수정)
//        return getTagsByBookmark(bookmark.getId());
//    }


//    // 태그 이름으로 북마크 가져오기
//    public List<Bookmark> getBookmarksByTagName(String tagName) {
//        // 태그명을 통해 태그를 찾음
//        Tag tag = tagRepository.findByTagName(tagName).orElseThrow(()
//                -> new IllegalArgumentException("Tag not found with name: " + tagName));
//
//        // 해당 태그로 연결된 TagBookmark에서 북마크를 가져옴
//        List<TagBookmark> tagBookmarks = tagBookmarkRepository.findByTag(tag);
//
//        // TagBookmark에서 Bookmark 객체들을 추출하여 반환
//        return tagBookmarks.stream()
//                .map(TagBookmark::getBookmark)
//                .collect(Collectors.toList());
//    }
}
