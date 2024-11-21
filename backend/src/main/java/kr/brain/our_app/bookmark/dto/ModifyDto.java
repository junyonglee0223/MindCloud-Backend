package kr.brain.our_app.bookmark.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder

public class ModifyDto {
    private BookmarkWithTagsDto bookmarkWithTagsDto;
    private String email;
    private String preBookmarkName;
}
