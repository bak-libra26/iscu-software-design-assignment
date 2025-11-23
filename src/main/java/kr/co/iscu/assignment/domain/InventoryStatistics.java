package kr.co.iscu.assignment.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 특정 기간 동안의 재고 통계 정보를 담는 DTO.
 */
@Data
@Builder
public class InventoryStatistics {

    /** 상품 ID */
    private Long productId;

    /** 조회 시작일 */
    private LocalDateTime startDate;

    /** 조회 종료일 */
    private LocalDateTime endDate;

    /** 기간 내 총 입고량 */
    private int totalInbound;

    /** 기간 내 총 출고량 */
    private int totalOutbound;

    /** 현재 재고량 */
    private int currentQuantity;

    /** 재고 회전율 (총출고량 / 현재 재고량) */
    private double turnoverRate;
}
