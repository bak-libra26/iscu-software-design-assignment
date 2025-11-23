package kr.co.iscu.assignment.domain.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 상품 정보를 표현하는 도메인 객체 (Entity 성격).
 *
 * <p>설명:
 * 이 클래스는 재고 관리 시스템에서 상품(제품)의 기본 정보를 담습니다. 주로
 * DB의 `product` 테이블과 매핑되어 사용됩니다. 필드명과 DB 컬럼은 아래와 같이
 * 매핑을 가정합니다: `id`(PK), `name`, `category`, `unit_price`, `created_at`, `updated_at`.
 *
 * <p>용도:
 * - 상품 등록, 수정, 삭제 시 전달/저장되는 데이터 구조
 * - 재고 입출고 기록과 연관되어 상품 단가(unitPrice) 확인
 * - 안전재고(safetyStock) 기반 알림 처리
 *
 * <p>금액 단위:
 * - `unitPrice`는 원화(KRW) 기준으로 관리합니다(예: 1200원 -> new BigDecimal("1200"))
 *
 * <p>예시 사용:
 * <pre>
 * Product p = Product.builder()
 *     .name("볼펜")
 *     .category("문구")
 *     .unitPrice(new BigDecimal("1200"))
 *     .createdAt(LocalDateTime.now())
 *     .updatedAt(LocalDateTime.now())
 *     .build();
 * </pre>
 *
 * <p>제약 및 고려사항:
 * - `unitPrice`는 통화 금액을 표현하므로 BigDecimal 사용
 * - `createdAt`/`updatedAt`은 서버 시간대에 맞게 관리
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    /**
     * 상품 고유 식별자 (Primary Key)
     * DB 컬럼: product.id
     */
    private Long id;

    /**
     * 상품명.
     * DB 컬럼: product.name
     */
    private String name;

    /**
     * 상품 카테고리명(예: 문구, 전자제품 등).
     * DB 컬럼: product.category
     */
    private String category;

    /**
     * 상품의 단가(판매/관리용). 원화(KRW) 기준이며 소수점이 필요하지 않다면 정수(예: 1200)로 표현합니다.
     * DB 컬럼: product.unit_price
     */
    private BigDecimal unitPrice;

    /**
     * 안전 재고 수량. 이 수량 이하로 재고가 떨어지면 알림 대상이 됩니다.
     * DB 컬럼: product.safety_stock
     */
    private Integer safetyStock;

    /**
     * 생성 시각(레코드 생성 시점).
     * DB 컬럼: product.created_at
     */
    private LocalDateTime createdAt;

    /**
     * 최종 수정 시각(레코드 수정 시점).
     * DB 컬럼: product.updated_at
     */
    private LocalDateTime updatedAt;
}
