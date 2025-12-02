package io.hhplus.tdd;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointController;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
    @DisplayName("유저 포인트 충전/이용 내역을 조회한다. (포인트 있는 유저)")
    void NewUserPointChargeAndUseInquiry_A() {
        // 이유 : 한 가지 테스트는 한 가지 검증만 한다는 규칙에 따라, 포인트 유무에 따른 케이스 분리
        // given - A (포인트 있는 유저)
        long userId = 1L; // 포인트 있는 유저

        List<PointHistory> HavePointUser = pointController.history(userId);
        pointHistoryTable.insert(1L, 500, TransactionType.CHARGE, 20251127); // 충전 시 조회 확인 (0일 경우 확인 시, 주석)

        // when - A
//        List<PointHistory> resultCharge = pointHistoryTable.selectAllByUserId(userId);
        List<PointHistory> history = pointController.history(userId);

        // then - A
        int UserId1Size = history.size();
        Assertions.assertEquals(UserId1Size, 1L);
    }

    @Test
    @DisplayName("유저 포인트 충전/이용 내역을 조회한다. (포인트 없는 유저)")
    void NewUserPointChargeAndUseInquiry_B() {

        // given - B (포인트 없는 유저)
        long userId2 = 2L; // 포인트 없는 유저
        List<PointHistory> NotPointUser = pointController.history(userId2);
        // when - B
        List<PointHistory> history2 = pointController.history(userId2);

        // then - B
        int UserId2Size = history2.size();
        org.assertj.core.api.Assertions.assertThat(UserId2Size).isEqualTo(0); // Junit 보다 가독성 좋다고 하여 assertj 로 사용
    }


    @Test
    @DisplayName("유저의 포인트를 충전한다.")
    void NewUserAddPointTest() {
    // 이유 : 포인트가 0인 유저에게 포인트 5000을 충전하고 확인한다.
    // given
    long userId = 1L;
    UserPoint beforeCharge = pointController.point(userId);
    Assertions.assertEquals(0L, beforeCharge.point()); // 충전 전 포인트 확인

    // when
    pointController.charge(userId, 5000L);

    // then
    UserPoint afterCharge = pointController.point(userId);
    Assertions.assertEquals(5000L, afterCharge.point()); // 충전 후 포인트 확인

    System.out.println("유저 " + userId + "의 현재 포인트는 " + afterCharge.point() + "입니다.");

}

    @Test
    @DisplayName("유저의 포인트를 사용한다. (잔액>사용액)")

    void NewUserPointUse() throws Exception {
    // 이유 : 잔액 > 사용액일 경우 테스트
    // given
    long userId = 1L;
    pointController.charge(userId, 5000L); // 포인트 충전

    UserPoint beforeUse = pointController.point(userId);
    Assertions.assertEquals(5000L, beforeUse.point()); // 사용 전 포인트 확인

    System.out.println("유저의 포인트 잔액 : " + pointController.point(userId).point());

    // when
    pointController.use(userId, 500L); // 포인트 500 사용

    // then
    UserPoint afterUse = pointController.point(userId);
    Assertions.assertEquals(4500L, afterUse.point()); // 사용 후 잔액 확인
    System.out.println("유저의 포인트 잔액 : " + pointController.point(userId).point());
}


    @Test
    @DisplayName("유저의 포인트를 사용한다. (잔액<사용액)")
    void NewUserMinusPointUse() throws Exception {
        // 이유 : 잔액 < 사용액일 경우 테스트
        // given
        long userId = 1L;
        pointController.charge(userId, 5000L); // 포인트 충전

        UserPoint beforeUse = pointController.point(userId);
        Assertions.assertEquals(5000L, beforeUse.point()); // 사용 전 포인트 확인

        System.out.println("유저의 포인트 잔액 : " + pointController.point(userId).point());

        // when
        Assertions.assertThrows(Exception.class , ()-> pointController.use(userId, 6000L));

        // then
        UserPoint afterUse = pointController.point(userId);
        Assertions.assertEquals(5000L, afterUse.point()); // 사용 후 잔액 확인
        System.out.println("유저의 포인트 잔액 : " + pointController.point(userId).point());
    }


}
