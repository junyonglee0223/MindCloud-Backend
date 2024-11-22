package kr.brain.our_app.idsha;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class IDGeneratorTest {

    @Test
    void 아이디_생성_테스트() {
        // Given: 입력 문자열
        String code = "TestCode";

        // When: IDGenerator 호출
        String generatedId = IDGenerator.generateId(code);

        // Then: ID가 null이 아니고 길이가 64인지 확인 (SHA-256 해시 값은 항상 64자리)
        assertNotNull(generatedId);
        assertEquals(64, generatedId.length());

        // Output: 생성된 ID 출력
        System.out.println("생성된 ID: " + generatedId);
    }

    @Test
    void 다른_입력값으로_다른_아이디_생성_테스트() {
        // Given: 서로 다른 입력값
        String code1 = "TestCodetest1@gmail.com";
        String code2 = "TestCodetest1@gmail.com";

        // When: 두 개의 ID 생성
        String id1 = IDGenerator.generateId(code1);
        String id2 = IDGenerator.generateId(code2);

        // Then: 서로 다른 ID 확인
        assertNotEquals(id1, id2);

        // Output: 생성된 ID 출력
        System.out.println("ID1: " + id1);
        System.out.println("ID2: " + id2);
    }

    @Test
    void 같은_입력값_다른_시간_테스트() throws InterruptedException {
        // Given: 동일한 입력값
        String code = "TestCode";

        // When: 약간의 시간 차이를 두고 ID 생성
        String id1 = IDGenerator.generateId(code);
        Thread.sleep(10); // 10ms 대기
        String id2 = IDGenerator.generateId(code);

        // Then: 서로 다른 ID 확인
        assertNotEquals(id1, id2);

        // Output: 생성된 ID 출력
        System.out.println("ID1: " + id1);
        System.out.println("ID2: " + id2);
    }

    @Test
    void 여러_아이디_중복_테스트() throws InterruptedException{
        // Given: 동일한 입력값과 반복 횟수
        String code = "TestCode";
        int iterations = 100;
        Set<String> generatedIds = new HashSet<>();

        // When: 여러 개의 ID 생성
        for (int i = 0; i < iterations; i++) {
            generatedIds.add(IDGenerator.generateId(code));
            Thread.sleep(10); // 10ms 대기
        }
        /*
        * thread sleep 없이 동시에 생성하게 되면 겹치는 id 발생
        * */

        // Then: 생성된 ID 개수가 반복 횟수와 동일한지 확인 (중복 없음)
        assertEquals(iterations, generatedIds.size());

        // Output: 일부 생성된 ID 출력
        System.out.println("생성된 ID 중 일부: ");
        generatedIds.stream().limit(5).forEach(System.out::println);
    }
}
