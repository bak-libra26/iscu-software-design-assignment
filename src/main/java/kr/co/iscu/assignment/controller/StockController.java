package kr.co.iscu.assignment.controller;

import kr.co.iscu.assignment.domain.InventoryStatistics;
import kr.co.iscu.assignment.domain.stock.StockHistory;
import kr.co.iscu.assignment.service.stock.StockService;
import kr.co.iscu.assignment.service.stock.dto.StockStatusDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 재고 관리 REST API 컨트롤러
 */
@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    /**
     * 상품 입고 처리
     *
     * @param productId 상품 ID
     * @param request   입고 수량
     * @return 응답 상태
     */
    @PostMapping("/{productId}/inbound")
    public ResponseEntity<Void> inbound(
            @PathVariable Long productId,
            @RequestBody InboundRequest request) {
        stockService.inbound(productId, request.getQuantity());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 상품 출고 처리
     *
     * @param productId 상품 ID
     * @param request   출고 수량
     * @return 출고 후 안전재고 미만 여부
     */
    @PostMapping("/{productId}/outbound")
    public ResponseEntity<OutboundResponse> outbound(
            @PathVariable Long productId,
            @RequestBody OutboundRequest request) {
        boolean isBelowSafety = stockService.outbound(productId, request.getQuantity());
        return ResponseEntity.ok(new OutboundResponse(isBelowSafety));
    }

    /**
     * 특정 상품의 재고 거래 이력 조회
     *
     * @param productId 상품 ID
     * @return 재고 이력 목록
     */
    @GetMapping("/{productId}/histories")
    public ResponseEntity<List<StockHistory>> getStockHistories(@PathVariable Long productId) {
        List<StockHistory> histories = stockService.getStockHistories(productId);
        return ResponseEntity.ok(histories);
    }

    /**
     * 전체 상품의 재고 현황 조회
     *
     * @return 재고 현황 목록
     */
    @GetMapping("/status")
    public ResponseEntity<List<StockStatusDto>> getStockStatusList() {
        List<StockStatusDto> statusList = stockService.getStockStatusList();
        return ResponseEntity.ok(statusList);
    }

    /**
     * 안전재고 미만인 상품 목록 조회
     *
     * @return 안전재고 미만 상품 목록
     */
    @GetMapping("/status/below-safety")
    public ResponseEntity<List<StockStatusDto>> getProductsBelowSafetyStock() {
        List<StockStatusDto> belowList = stockService.getProductsBelowSafetyStock();
        return ResponseEntity.ok(belowList);
    }

    /**
     * 특정 상품의 기간별 재고 통계 조회
     *
     * @param productId 상품 ID
     * @param startDate 시작일
     * @param endDate   종료일
     * @return 재고 통계
     */
    @GetMapping("/{productId}/statistics")
    public ResponseEntity<InventoryStatistics> getInventoryStatistics(
            @PathVariable Long productId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        InventoryStatistics statistics = stockService.getInventoryStatistics(productId, startDate, endDate);
        return ResponseEntity.ok(statistics);
    }

    /**
     * 입고 요청 DTO
     */
    @Data
    public static class InboundRequest {
        private Integer quantity;
    }

    /**
     * 출고 요청 DTO
     */
    @Data
    public static class OutboundRequest {
        private Integer quantity;
    }

    /**
     * 출고 응답 DTO
     */
    @Data
    @RequiredArgsConstructor
    public static class OutboundResponse {
        private final boolean belowSafetyStock;
    }
}
