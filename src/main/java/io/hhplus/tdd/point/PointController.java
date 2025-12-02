package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.function.Executable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/point")
public class PointController {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;



    public PointController(UserPointTable userPointTable, PointHistoryTable pointHistoryTable)
    {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;

    }

    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}")
    public UserPoint point(
            @PathVariable long id
    ) {
//        return new UserPoint(0, 0, 0);
        return userPointTable.selectById(id);
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}/histories")
    public List<PointHistory> history(
            @PathVariable long id
    ) {
//        return List.of();
          return pointHistoryTable.selectAllByUserId(id);
    }


    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/charge")
    public UserPoint charge
    (
            @PathVariable long id,
            @RequestBody long amount

    ) {
        long NowPoint = userPointTable.selectById(id).point(); // 현재 포인트
        long TotalPoint = NowPoint + amount; // 충전 후 현재 포인트 (현재 포인트 + 충전액)

        UserPoint InsetTotalPoint = userPointTable.insertOrUpdate(id, TotalPoint); // 유저포인트DB에 충전 후 현재 포인트 기록
        pointHistoryTable.insert(id, amount, TransactionType.CHARGE, InsetTotalPoint.updateMillis()); // 히스토리DB에 이번 충전액 기록


        return new UserPoint(id, TotalPoint, InsetTotalPoint.updateMillis());
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    public UserPoint use(
            @PathVariable long id,
            @RequestBody long amount
    ) throws Exception {
        long NowPoint = userPointTable.selectById(id).point(); // 현재 포인트
//        long TotalPoint = NowPoint - amount; // 사용 후 현재 포인트 (현재 포인트 - 충전액)

        if (NowPoint >= amount) {
            long TotalPoint = NowPoint - amount; // 사용 후 현재 포인트 (현재 포인트 - 충전액)

            UserPoint InsertTotalPoint = userPointTable.insertOrUpdate(id, TotalPoint); // 유저포인트DB에 사용 후 현재 포인트 기록
            pointHistoryTable.insert(id, amount, TransactionType.USE, InsertTotalPoint.updateMillis()); // 히스토리DB에 이번 사용액 기록

            return new UserPoint(id, TotalPoint, InsertTotalPoint.updateMillis());
        } else {
            throw new Exception("잔액 보다 사용 포인트가 큽니다.");
        }

    }
}
