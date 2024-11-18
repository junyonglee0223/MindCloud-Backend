package kr.brain.our_app.bookmark.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class SearchDto {
    private List<BookmarkWithTagsDto> tagResults;
    private List<BookmarkWithTagsDto> bookmarkResults;
}
