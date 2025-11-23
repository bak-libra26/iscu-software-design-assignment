package kr.co.iscu.assignment.service.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 상품 수정을 위한 데이터 전송 객체(DTO).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateDto {
    private String name;
    private String category;
    private BigDecimal unitPrice;
    private Integer safetyStock;
}
