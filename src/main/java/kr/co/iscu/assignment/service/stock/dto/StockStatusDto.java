package kr.co.iscu.assignment.service.stock.dto;

import kr.co.iscu.assignment.domain.product.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 상품별 재고 현황을 표현하는 DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockStatusDto {
    private Long productId;
    private String name;
    private String category;
    private BigDecimal unitPrice;
    private Integer safetyStock;
    private Integer currentQuantity;

    public static StockStatusDto of(Product product, int currentQuantity) {
        return StockStatusDto.builder()
                .productId(product.getId())
                .name(product.getName())
                .category(product.getCategory())
                .unitPrice(product.getUnitPrice())
                .safetyStock(product.getSafetyStock())
                .currentQuantity(currentQuantity)
                .build();
    }

    public boolean isBelowSafetyStock() {
        Integer safety = this.safetyStock == null ? 0 : this.safetyStock;
        return this.currentQuantity < safety;
    }
}

