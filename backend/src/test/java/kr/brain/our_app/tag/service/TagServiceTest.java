package kr.brain.our_app.tag.service;

import kr.brain.our_app.OurAppApplication;
import kr.brain.our_app.idsha.IDGenerator;
import kr.brain.our_app.tag.domain.Tag;
import kr.brain.our_app.tag.dto.TagDto;
import kr.brain.our_app.tag.repository.TagRepository;
import kr.brain.our_app.user.domain.User;
import kr.brain.our_app.user.dto.UserDto;
import kr.brain.our_app.user.repository.UserRepository;
import kr.brain.our_app.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = OurAppApplication.class)// 실제 스프링 부트를 이용한 통합 테스트를 위한 어노테이션
@Transactional  // 데이터베이스에 영향을 주지 않도록 롤백 처리
class TagServiceTest {

    @Autowired
    private TagService tagService;

    @Autowired
    private UserService userService;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;

    private UserDto userDto;
    private TagDto tagDto;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 생성 및 저장
        userDto = UserDto.builder()
                .id(IDGenerator.generateId("user1@example.com"))
                .userName("testUser")
                .email("user1@example.com")
                .build();
        // User를 데이터베이스에 저장
        userService.createUser(userDto);

        // TagDto 초기화
        tagDto = TagDto.builder()
                .tagName("TestTag1")
                .build();
    }

    @Test
    void testCreateTag() {
        // 태그 생성
        TagDto createdTag = tagService.createTag(tagDto, userDto);

        // 검증
        assertThat(createdTag).isNotNull();
        assertThat(createdTag.getTagName()).isEqualTo(tagDto.getTagName());

        // 데이터베이스에 태그가 저장되었는지 확인
        Tag savedTag = tagRepository.findById(createdTag.getId()).orElse(null);
        assertThat(savedTag).isNotNull();
        assertThat(savedTag.getTagName()).isEqualTo(tagDto.getTagName());


        //로그 찍어서 확인해보기
        System.out.println("!!TTTTsavedtag로 데이터베이스에 태그가 저장됐는지 확인용" + savedTag.getTagName());
    }

    @Test
    void testCreateTag_DuplicateTagName() {
        // 태그 생성
        tagService.createTag(tagDto, userDto);

        // 중복된 태그 이름으로 생성 시 예외 발생 확인
        assertThrows(IllegalArgumentException.class, () -> tagService.createTag(tagDto, userDto));
    }

    @Test
    void testFindAllTags() {
        // 여러 개의 태그 생성
        List<TagDto> tagDtos = List.of(
                TagDto.builder().tagName("TestTag1").build(),
                TagDto.builder().tagName("TestTag2").build(),
                TagDto.builder().tagName("TestTag3").build()
        );

        // 각각의 태그를 생성하여 저장
        for (TagDto tagDto : tagDtos) {
            tagService.createTag(tagDto, userDto);
        }

        // 모든 태그 조회
        List<TagDto> tags = tagService.findAllTags(userDto);

        // 검증
        assertThat(tags).isNotNull();
        assertThat(tags.size()).isEqualTo(tagDtos.size());

        System.out.println("All Tags 출력:");
        for (TagDto tag : tags) {
            System.out.println("Tag: " + tag.getTagName());
        }

        // 개별 태그 이름이 올바르게 저장되었는지 검증
        for (int i = 0; i < tagDtos.size(); i++) {
            assertThat(tags.get(i).getTagName()).isEqualTo(tagDtos.get(i).getTagName());
        }
    }


    @Test
    void testFindByTagName() {
        // 태그 생성
        tagService.createTag(tagDto, userDto);

        // 이름으로 태그 조회
        TagDto foundTag = tagService.findByTagName(tagDto.getTagName(),userDto.getId());

        // 검증
        assertThat(foundTag).isNotNull();
        assertThat(foundTag.getTagName()).isEqualTo(tagDto.getTagName());
    }

//    @Test
//    void testFindByTagName_NotFound() {
//        // 존재하지 않는 태그 이름 조회 시 예외 발생 확인
//        assertThrows(IllegalArgumentException.class, () -> tagService.findByTagName("NonExistentTag"));
//    }

    @Test
    void testFindById() {
        // 태그 생성
        TagDto createdTag = tagService.createTag(tagDto, userDto);

        // ID로 태그 조회
        TagDto foundTag = tagService.findById(createdTag.getId());

        // 검증
        assertThat(foundTag).isNotNull();
        assertThat(foundTag.getTagName()).isEqualTo(tagDto.getTagName());
    }

    @Test
    void testFindById_NotFound() {
        // 존재하지 않는 태그 ID로 조회 시 예외 발생 확인
        assertThrows(IllegalArgumentException.class, () -> tagService.findById("NonExistentId"));
    }
}
