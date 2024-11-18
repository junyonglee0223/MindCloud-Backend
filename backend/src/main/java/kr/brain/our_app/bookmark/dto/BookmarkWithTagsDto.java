package kr.brain.our_app.bookmark.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class BookmarkWithTagsDto {
    private String bookmarkName;
    private String url;
    private List<String> tags;
}
