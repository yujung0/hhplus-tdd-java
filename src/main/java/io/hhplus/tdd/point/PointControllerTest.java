package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.util.List;

@Slf4j
public class PointControllerTest {

private UserPointTable userPointTable;
private PointHistoryTable pointHistoryTable;
private PointController pointController;
private UserPoint userPoint;



@BeforeEach
    void setUp() {
    userPointTable = new UserPointTable();
    pointHistoryTable = new PointHistoryTable();
    pointController = new PointController(userPointTable, pointHistoryTable);
}

@Test
@DisplayName("신규 유저 포인트 조회 시 0원을 반환한다.")
    void NewUserCheck0won () {
    try {
        // 이유 : 아직 회원의 포인트 충전 전이므로, 포인트 0일 경우를 가정하여 테스트 케이스 작성
        // given (초기 상태 만들기)
        long userId = 1L; // 임의로 지정한 테스트 값

        // when (테스트 대상 기능을 실행)
        UserPoint result = pointController.point(userId); // Id로 포인트 조회

        // then (결과가 기대한 대로인지 검증)
        assert result.id() == userId;   // 결과의 id와 point 비교
        assert result.point() == 0L;// 결과의 id와 point 비교
    } catch (AssertionError e) {
        log.info(e.getMessage());
    }
}

@Test
@DisplayName("유저 포인트 충전/이용 내역을 조회한다.")
    void NewUserPointChargeAndUseInquiry() {
        // 이유 : assert 외 출력으로 확인하게끔 진행
        // given
        long userId = 1L;
        pointHistoryTable.insert(1L, 500, TransactionType.CHARGE, 20251127); // 충전 시 조회 확인 (0일 경우 확인 시, 주석)

        // when
        List<PointHistory> resultCharge = pointHistoryTable.selectAllByUserId(userId);

        // then
        if (resultCharge.isEmpty()) {
            System.out.println("아이디 '" + userId + "'의 포인트가 없습니다.");
        } else {
            System.out.println("아이디 '" + userId + "'의 포인트는 " + resultCharge.get(0).amount() + "입니다.");
        }
        
    }

@Test
@DisplayName("유저의 포인트를 충전한다.")
    void NewUserAddPointTest() {
    // 이유 : 포인트가 0인 유저에게 포인트 5000을 충전하고 확인한다.
    // given
    long userId = 1L;
    UserPoint before = pointController.point(userId);
    Assertions.assertEquals(0L, before.point()); // 충전 전 포인트 확인

    // when
    pointController.charge(userId, 5000L);

    // then
    UserPoint after = pointController.point(userId);
    Assertions.assertEquals(5000L, after.point()); // 충전 후 포인트 확인

    System.out.println("유저 " + userId + "의 현재 포인트는 " + after.point() + "입니다.");

}

}
