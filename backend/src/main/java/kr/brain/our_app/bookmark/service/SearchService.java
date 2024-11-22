package kr.brain.our_app.bookmark.service;

import kr.brain.our_app.bookmark.domain.Bookmark;
import kr.brain.our_app.bookmark.domain.TagBookmark;
import kr.brain.our_app.bookmark.dto.BookmarkDto;
import kr.brain.our_app.bookmark.dto.BookmarkWithTagsDto;
import kr.brain.our_app.bookmark.dto.SearchDto;
import kr.brain.our_app.bookmark.repository.TagBookmarkRepository;
import kr.brain.our_app.idsha.IDGenerator;
import kr.brain.our_app.tag.dto.TagDto;
import kr.brain.our_app.tag.service.TagService;
import kr.brain.our_app.user.dto.UserDto;
import kr.brain.our_app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {
    private final TagService tagService;
    private final BookmarkService bookmarkService;
    private final TagBookmarkRepository tagBookmarkRepository;
    private final TagBookmarkService tagBookmarkService;
    private final UserService userService;

    @Autowired
    public SearchService(TagService tagService, BookmarkService bookmarkService, TagBookmarkRepository tagBookmarkRepository, UserService userService
    , TagBookmarkService tagBookmarkService) {
        this.tagService = tagService;
        this.bookmarkService = bookmarkService;
        this.tagBookmarkRepository = tagBookmarkRepository;
        this.userService = userService;
        this.tagBookmarkService = tagBookmarkService;
    }

    //FIXME 나중에 bookmark, tagbookmark, tag service에서 userid or userDto 들어오는거 통일해야함 지금 엉망진창
    public SearchDto searchByWord(String word, String email) {
        UserDto userDto = userService.findByEmail(email);
        String userId = userDto.getId();

        // **1. 태그 기반 북마크 검색**
        List<BookmarkWithTagsDto> tagBasedResults = new ArrayList<>();
        if (tagService.existsByTagName(word, userId)) {
            TagDto tagDto = tagService.findByTagName(word, userId);

            List<TagBookmark> tagBookmarks = tagBookmarkRepository
                    .findTagBookmarksByTag_TagNameAndTag_User_Id(tagDto.getTagName(),userId);

            tagBasedResults = tagBookmarks.stream()
                    .map(tagBookmark -> {
                        Bookmark bookmark = tagBookmark.getBookmark();

                        // 해당 북마크에 연결된 모든 태그 가져오기
                        List<String> tags = bookmark.getTags().stream()
                                .map(tb -> tb.getTag().getTagName())
                                .collect(Collectors.toList());

                        return BookmarkWithTagsDto.builder()
                                .bookmarkName(bookmark.getBookmarkName())
                                .url(bookmark.getUrl())
                                .tags(tags)
                                .build();
                    })
                    .collect(Collectors.toList());
        }

        // **2. 북마크 이름 기반 검색**
        List<BookmarkWithTagsDto> nameBasedResults = new ArrayList<>();
        if (bookmarkService.existsByBookmarkName(word, userId)) {
            BookmarkDto bookmarkDto = bookmarkService.findByBookmarkName(word, userDto);

            // 해당 북마크에 연결된 태그 가져오기
            List<String> tags = tagBookmarkService.findTagsByBookmarkName(bookmarkDto.getBookmarkName(),userId)
                    .stream()
                    .map(TagDto::getTagName)
                    .collect(Collectors.toList());
            //TagsByBN이 tagdto로 return되기에, tagname만 리턴되도록 tags를 스트림 변환함

            BookmarkWithTagsDto result = BookmarkWithTagsDto.builder()
                    .bookmarkName(bookmarkDto.getBookmarkName())
                    .url(bookmarkDto.getUrl())
                    .tags(tags)
                    .build();

            nameBasedResults.add(result);
        }

        // **3. 결과 반환**
        return SearchDto.builder()
                .tagResults(tagBasedResults)
                .bookmarkResults(nameBasedResults)
                .build();
    }
}
