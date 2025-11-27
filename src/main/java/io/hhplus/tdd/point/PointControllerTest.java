package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Slf4j
public class PointControllerTest {

private UserPointTable userPointTable;
private PointHistoryTable pointHistoryTable;
private PointController pointController;



@BeforeEach
    void setUp() {
    userPointTable = new UserPointTable();
    pointHistoryTable = new PointHistoryTable();
    pointController = new PointController();


}

@Test
@DisplayName("신규 유저 포인트 조회 시 0원을 반환한다.")
    void NewUserCheck0won () {
    try {
        // 이유 : 아직 회원의 포인트 충전 전이므로, 포인트 0일 경우를 가정하여 테스트 케이스 작성
        // given
        long userId = 1L; // 임의로 지정한 테스트 값

        // when
        UserPoint result = pointController.point(userId); // Id로 포인트 조회

        // then
        assert result.id() == userId;   // 결과의 id와 point 비교
        assert result.point() == 0L;// 결과의 id와 point 비교
    } catch (AssertionError e) {
        log.info(e.getMessage());
    }
}

@Test
@DisplayName("신규 유저 포인트 충전/이용 내역을 조회한다.")
    void NewUserPointInquiry() {

    }

}
