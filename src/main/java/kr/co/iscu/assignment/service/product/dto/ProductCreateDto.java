package kr.co.iscu.assignment.service.product.dto;

import kr.co.iscu.assignment.domain.product.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 상품 생성을 위한 데이터 전송 객체(DTO).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateDto {
    private String name;
    private String category;
    private BigDecimal unitPrice;
    private Integer safetyStock;

    /**
     * DTO를 Product 엔티티로 변환합니다.
     * @return Product 엔티티
     */
    public Product toEntity() {
        return Product.builder()
                .name(name)
                .category(category)
                .unitPrice(unitPrice)
                .safetyStock(safetyStock != null ? safetyStock : 0) // null일 경우 기본값 0 설정
                .build();
    }
}
