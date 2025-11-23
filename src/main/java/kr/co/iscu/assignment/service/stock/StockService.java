package kr.co.iscu.assignment.service.stock;

import kr.co.iscu.assignment.domain.InventoryStatistics;
import kr.co.iscu.assignment.domain.product.Product;
import kr.co.iscu.assignment.domain.stock.Stock;
import kr.co.iscu.assignment.domain.stock.StockEventType;
import kr.co.iscu.assignment.domain.stock.StockHistory;
import kr.co.iscu.assignment.repository.product.ProductRepository;
import kr.co.iscu.assignment.repository.stock.StockHistoryRepository;
import kr.co.iscu.assignment.repository.stock.StockRepository;
import kr.co.iscu.assignment.service.stock.dto.StockStatusDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 재고 관리 비즈니스 로직을 처리하는 서비스 클래스.
 */
@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final StockHistoryRepository stockHistoryRepository;
    private final ProductRepository productRepository;

    /**
     * 지정된 상품을 입고 처리합니다.
     * <p>
     * 재고 수량을 증가시키고, 입고 이력을 기록합니다.
     *
     * @param productId 입고할 상품의 ID
     * @param quantity  입고할 수량 (0보다 커야 함)
     * @throws IllegalArgumentException 존재하지 않는 상품이거나, 입고 수량이 0 이하일 경우 발생
     */
    @Transactional
    public void inbound(Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("입고 수량은 0보다 커야 합니다.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        Stock stock = stockRepository.findByProductId(productId)
                .orElse(new Stock(productId, 0));

        stock.setQuantity(stock.getQuantity() + quantity);
        stockRepository.save(stock);

        StockHistory history = StockHistory.builder()
                .productId(productId)
                .eventType(StockEventType.INBOUND)
                .quantity(quantity)
                .build();
        stockHistoryRepository.save(history);
    }

    /**
     * 지정된 상품을 출고 처리합니다.
     * <p>
     * 재고 수량을 감소시키고, 출고 이력을 기록합니다.
     * 출고 후 현재 재고가 상품의 안전 재고 수량 미만인지 여부를 반환합니다.
     *
     * @param productId 출고할 상품의 ID
     * @param quantity  출고할 수량 (0보다 커야 함)
     * @return 출고 후 재고가 안전 재고 미만이면 {@code true}, 아니면 {@code false}
     * @throws IllegalArgumentException 존재하지 않는 상품, 출고 수량이 0 이하, 또는 재고가 부족할 경우 발생
     */
    @Transactional
    public boolean outbound(Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("출고 수량은 0보다 커야 합니다.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));
        Stock stock = stockRepository.findByProductId(productId)
                .orElse(new Stock(productId, 0));

        if (stock.getQuantity() < quantity) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
        stock.setQuantity(stock.getQuantity() - quantity);
        stockRepository.save(stock);

        StockHistory history = StockHistory.builder()
                .productId(productId)
                .eventType(StockEventType.OUTBOUND)
                .quantity(quantity)
                .build();
        stockHistoryRepository.save(history);

        return stock.getQuantity() < product.getSafetyStock();
    }

    /**
     * 특정 상품의 모든 재고 거래 내역(입고/출고)을 조회합니다.
     *
     * @param productId 조회할 상품의 ID
     * @return 해당 상품의 거래 이력 목록 (최신순으로 정렬)
     */
    public List<StockHistory> getStockHistories(Long productId) {
        return stockHistoryRepository.findByProductId(productId);
    }

    /**
     * 모든 상품의 현재 재고 현황을 조회합니다.
     * <p>
     * 각 상품의 현재고, 안전재고, 안전재고 미만 여부 등의 정보를 포함합니다.
     *
     * @return 전체 상품의 재고 현황 DTO 목록
     */
    public List<StockStatusDto> getStockStatusList() {
        List<Product> products = productRepository.findAll();
        List<Stock> stocks = stockRepository.findAll();

        Map<Long, Integer> stockMap = stocks.stream()
                .collect(Collectors.toMap(Stock::getProductId, Stock::getQuantity));

        return products.stream()
                .map(product -> {
                    int currentQuantity = stockMap.getOrDefault(product.getId(), 0);
                    return StockStatusDto.of(product, currentQuantity);
                })
                .collect(Collectors.toList());
    }

    /**
     * 안전 재고 수량 미만인 상품 목록을 조회합니다.
     * <p>
     * 재고 부족 알림이나 긴급 발주 목록 생성에 사용될 수 있습니다.
     *
     * @return 안전 재고 미만인 상품의 재고 현황 DTO 목록
     */
    public List<StockStatusDto> getProductsBelowSafetyStock() {
        return getStockStatusList().stream()
                .filter(StockStatusDto::isBelowSafetyStock)
                .collect(Collectors.toList());
    }

    /**
     * 특정 상품의 지정된 기간 동안의 재고 통계 및 회전율을 분석합니다.
     *
     * @param productId 분석할 상품의 ID
     * @param startDate 분석 시작일
     * @param endDate   분석 종료일
     * @return 기간 내 총 입고량, 총 출고량, 현재 재고, 재고 회전율이 포함된 통계 객체
     */
    public InventoryStatistics getInventoryStatistics(Long productId, LocalDateTime startDate, LocalDateTime endDate) {
        // 1. 기간 내 총 입고량/출고량 계산
        int totalInbound = stockHistoryRepository.sumQuantityByEventTypeBetweenDates(productId, StockEventType.INBOUND, startDate, endDate);
        int totalOutbound = stockHistoryRepository.sumQuantityByEventTypeBetweenDates(productId, StockEventType.OUTBOUND, startDate, endDate);

        // 2. 현재 재고량 조회
        int currentQuantity = stockRepository.findByProductId(productId)
                .map(Stock::getQuantity)
                .orElse(0);

        // 3. 재고 회전율 계산 (출고량 / 현재 재고량)
        // 현재 재고가 0이면 회전율도 0으로 처리하여 0으로 나누는 오류 방지
        double turnoverRate = (currentQuantity == 0) ? 0 : (double) totalOutbound / currentQuantity;

        return InventoryStatistics.builder()
                .productId(productId)
                .startDate(startDate)
                .endDate(endDate)
                .totalInbound(totalInbound)
                .totalOutbound(totalOutbound)
                .currentQuantity(currentQuantity)
                .turnoverRate(turnoverRate)
                .build();
    }
}
