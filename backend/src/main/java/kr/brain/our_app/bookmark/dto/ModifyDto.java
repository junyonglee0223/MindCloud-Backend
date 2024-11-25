package kr.brain.our_app.bookmark.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@ToString
public class ModifyDto {
    private BookmarkWithTagsDto bookmarkWithTagsDto;
    private String email;
    private String preBookmarkName;
}
