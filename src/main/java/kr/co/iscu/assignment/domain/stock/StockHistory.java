package kr.co.iscu.assignment.domain.stock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 재고 거래 이력 (입고/출고) 도메인 객체.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockHistory {

    /** 거래 ID (PK) */
    private Long historyId;

    /** 상품 ID (FK) */
    private Long productId;

    /** 거래 유형 (INBOUND, OUTBOUND) */
    private StockEventType eventType;

    /** 거래 수량 */
    private Integer quantity;

    /** 거래일시 */
    private LocalDateTime createdAt;
}
