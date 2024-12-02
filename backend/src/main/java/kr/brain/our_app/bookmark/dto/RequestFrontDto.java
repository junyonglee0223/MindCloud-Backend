package kr.brain.our_app.bookmark.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class RequestFrontDto {
    private String title;
    private String url;
    private List<String>tags;
    private String userName;
    private String email;
    private String imageUrl;
}
