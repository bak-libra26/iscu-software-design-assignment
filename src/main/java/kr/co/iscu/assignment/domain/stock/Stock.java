package kr.co.iscu.assignment.domain.stock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 현재 재고 상태를 단순화하여 표현하는 도메인 객체.
 *
 * <p>설명:
 * 각 상품별 현재 보유 수량을 나타냅니다. 보통 별도의 `stock` 테이블이나
 * 상품 테이블의 재고 컬럼과 매핑됩니다. 재고 증감은 입고/출고 처리 로직에서
 * 동기화되어야 합니다.
 *
 * <p>필드:
 * - productId: 대상 상품의 PK
 * - quantity: 현재 보유 수량
 *
 * <p>예시 사용:
 * <pre>
 * Stock s = new Stock(1L, 250);
 * </pre>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stock {

    /** 대상 상품 ID (product.id) */
    private Long productId;

    /** 현재 재고 수량 */
    private Integer quantity;

}
