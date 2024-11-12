package kr.brain.our_app.bookmark.dto;

import kr.brain.our_app.bookmark.domain.Bookmark;
import kr.brain.our_app.tag.domain.Tag;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookmarkDto {
    private String id;
    private String bookmarkName;
    private String url;

//    public Bookmark toEntity() {
//        return Bookmark.builder()
//                .id(this.id)
//                .bookmarkName(this.bookmarkName)
//                .url(this.url)
//                .build();
//    }
}
