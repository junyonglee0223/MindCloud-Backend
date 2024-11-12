package kr.brain.our_app;

import kr.brain.our_app.bookmark.repository.BookmarkRepository;
import kr.brain.our_app.bookmark.repository.TagBookmarkRepository;
import kr.brain.our_app.tag.repository.TagRepository;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class OurAppApplication {

	public static void main(String[] args) {

		ConfigurableApplicationContext context = SpringApplication.run(OurAppApplication.class, args);
		//resetDatabase(context);
	}

	@Controller
	public static class HelloController{
		@GetMapping("/")
		public String Hello(){
			//return "Hello World!";
			return "index";
		}
	}
	private static void resetDatabase(ConfigurableApplicationContext context) {
		// 각 Repository Bean 가져오기
		TagRepository tagRepository = context.getBean(TagRepository.class);
		BookmarkRepository bookmarkRepository = context.getBean(BookmarkRepository.class);
		TagBookmarkRepository tagBookmarkRepository = context.getBean(TagBookmarkRepository.class);

		// 1. TagBookmark 데이터 삭제
		System.out.println("Resetting TagBookmark table...");
		tagBookmarkRepository.deleteAll();

		// 2. Bookmark 데이터 삭제
		System.out.println("Resetting Bookmark table...");
		bookmarkRepository.deleteAll();

		// 3. Tag 데이터 삭제
		System.out.println("Resetting Tag table...");
		tagRepository.deleteAll();

		// 초기화 확인 로그
		System.out.println("All tables have been reset.");
	}
}

