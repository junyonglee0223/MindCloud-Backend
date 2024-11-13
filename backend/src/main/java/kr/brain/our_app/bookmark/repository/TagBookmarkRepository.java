package kr.brain.our_app.bookmark.repository;

import kr.brain.our_app.bookmark.domain.Bookmark;
import kr.brain.our_app.bookmark.domain.TagBookmark;
import kr.brain.our_app.tag.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
//Page 객체를 사용할 떄, 위 2개를 사용하지 않고 딴거 사용하면 오류난다.
import java.util.List;
import java.util.Optional;

public interface TagBookmarkRepository extends JpaRepository<TagBookmark, String>  {

    // 1. Tag와 Bookmark 객체로 TagBookmark 조회
    Optional<TagBookmark> findByTagAndBookmark(Tag tag, Bookmark bookmark);
    Optional<TagBookmark> findByTagIdAndBookmarkId(String tagId, String bookmarkId);
    // Tag ID와 Bookmark ID로 TagBookmark 존재 여부 확인
    boolean existsByTagIdAndBookmarkId(String tagId, String bookmarkId);
    List<TagBookmark> findByTagId(String tagId);
    List<TagBookmark> findByBookmarkId(String bookmarkId);

    List<TagBookmark> findTagBookmarksByTag_TagNameAndTag_User_Id(String tagName, String userId);
    List<TagBookmark> findAllByBookmark(Bookmark bookmark);
    List<TagBookmark> findAllByTag(Tag tag);


//    // 2-1. Tag의 ID와 Bookmark의 ID로 TagBookmark객체 생성
//    Optional<TagBookmark> createTagBookmarkById(Long tagId, Long bookmarkId);
//
//    // 2-2 . Tagname, bookmarkname으로 TagBookmark 객체 생성
//    Optional<TagBookmark> createTagBookmarkByName(String tagName, String bookmarkName);
//
//    // 3-1. 특정 Tag에 연결된 모든 TagBookmark 조회
//    List<TagBookmark> findByTag(Tag tag);
//
//    // 3-2. 특정 Bookmark에 연결된 모든 TagBookmark 조회
//    List<TagBookmark> findByBookmark(Bookmark bookmark);
//
//    // 4-1. 특정 Tag의 ID로 연결된 모든 TagBookmark 조회 -> 이거 필요없는데?
//    void findByTagId(Long tagId);
//
//    // 4-2. 특정 Bookmark의 ID로 연결된 모든 TagBookmark 조회
//    void findByBookmarkId(Long bookmarkId);
//
//    List<Bookmark> findBookmarkByTagName(Tag tag);
//
//    //repository 전반적으로 수정.
//
//    // 1.

}
