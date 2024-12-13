package kr.brain.our_app.tag.domain;

import jakarta.persistence.*;
import kr.brain.our_app.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Entity
@Table(name ="Tag")
@Getter
@Setter
@Builder
@AllArgsConstructor

public class Tag {
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="tag_id")
    private String id;

    //@NotEmpty
    //@Column(nullable = false, length = 25, unique = true)
    @Column
    private String tagName;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Tag() {
        // 기본 생성자는 아무 동작을 하지 않거나 초기값을 설정 할 수 있음
    }


}
