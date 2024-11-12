package kr.brain.our_app.bookmark.domain;

import jakarta.persistence.*;
import kr.brain.our_app.user.domain.User;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Table(name = "Bookmark")
@Getter
@Setter
@Entity
@Builder

//얘는 dto 라기 보다는 entity인데, 폴더 이름 수정 건의해야겠다.

public class Bookmark {
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmark_id")
    private String id;

    //BOOKMARKNAME은 중복을 허용. ID는 중복 허용 X 따라서 리스트사용
    @Column
    private String bookmarkName;

    @URL
    @Column
    @Lob
    private String url;

    // User와 일대일 관계 설정
    @ManyToOne
    @JoinColumn(name = "user_id") // Bookmark 테이블에 user_id FK 생성
    private User user;

    //태그는 중복을 허용하지않음. 따라서 SET을 사용
    @OneToMany(mappedBy = "bookmark", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TagBookmark> tags = new HashSet<>();

}



