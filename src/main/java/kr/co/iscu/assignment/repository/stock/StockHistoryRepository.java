package kr.co.iscu.assignment.repository.stock;

import kr.co.iscu.assignment.domain.stock.StockEventType;
import kr.co.iscu.assignment.domain.stock.StockHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 재고 거래 이력 Mapper
 */
@Mapper
public interface StockHistoryRepository {

    /**
     * 재고 거래 이력 조회
     * @param productId 상품 ID
     * @return
     */
    List<StockHistory> findByProductId(Long productId);

    /**
     * 재고 거래 이력 등록
     * @param stockHistory
     */
    void save(StockHistory stockHistory);

    /**
     * 특정 기간 동안의 거래 유형별 수량 합계 조회
     * @param productId 상품 ID
     * @param eventType 거래 유형 (INBOUND or OUTBOUND)
     * @param startDate 시작일
     * @param endDate 종료일
     * @return
     */
    int sumQuantityByEventTypeBetweenDates(
            @Param("productId") Long productId,
            @Param("eventType") StockEventType eventType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * 상품 ID로 재고 이력 삭제
     * @param productId 상품 ID
     */
    void deleteByProductId(Long productId);
}
