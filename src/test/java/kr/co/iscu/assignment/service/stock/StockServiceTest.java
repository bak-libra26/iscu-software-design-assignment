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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class StockServiceTest {

    @Autowired
    private StockService stockService;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StockHistoryRepository stockHistoryRepository;

    @Autowired
    private ProductRepository productRepository;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        // 테스트용 상품 데이터 생성
        product1 = Product.builder().name("테스트 상품 1").category("테스트").unitPrice(new BigDecimal("1000")).safetyStock(10).build();
        product2 = Product.builder().name("테스트 상품 2").category("테스트").unitPrice(new BigDecimal("2000")).safetyStock(5).build();
        productRepository.insert(product1);
        productRepository.insert(product2);
    }

    @Test
    @DisplayName("상품을 입고하면 재고가 증가하고 입고 이력이 남는다.")
    void inbound() {
        // given
        int quantity = 50;

        // when
        stockService.inbound(product1.getId(), quantity);

        // then
        Stock stock = stockRepository.findByProductId(product1.getId()).orElseThrow();
        assertThat(stock.getQuantity()).isEqualTo(quantity);

        List<StockHistory> histories = stockHistoryRepository.findByProductId(product1.getId());
        assertThat(histories).hasSize(1);
        assertThat(histories.get(0).getEventType()).isEqualTo(StockEventType.INBOUND);
        assertThat(histories.get(0).getQuantity()).isEqualTo(quantity);
    }

    @Test
    @DisplayName("0개 이하의 수량으로 입고를 시도하면 예외가 발생한다.")
    void inbound_with_invalid_quantity() {
        // when & then
        assertThatThrownBy(() -> stockService.inbound(product1.getId(), 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("입고 수량은 0보다 커야 합니다.");
    }

    @Test
    @DisplayName("재고가 있는 상품을 출고하면 재고가 감소하고 출고 이력이 남는다.")
    void outbound() {
        // given
        stockService.inbound(product1.getId(), 100); // 초기 재고 100

        // when
        boolean isBelowSafety = stockService.outbound(product1.getId(), 20);

        // then
        Stock stock = stockRepository.findByProductId(product1.getId()).orElseThrow();
        assertThat(stock.getQuantity()).isEqualTo(80);
        assertThat(isBelowSafety).isFalse(); // 80 > 10

        List<StockHistory> histories = stockHistoryRepository.findByProductId(product1.getId());
        assertThat(histories).hasSize(2); // 입고 1, 출고 1
        assertThat(histories.get(0).getEventType()).isEqualTo(StockEventType.OUTBOUND);
    }

    @Test
    @DisplayName("출고 후 재고가 안전 재고 미만이 되면 true를 반환한다.")
    void outbound_and_below_safety_stock() {
        // given
        stockService.inbound(product1.getId(), 15); // 초기 재고 15, 안전 재고 10

        // when
        boolean isBelowSafety = stockService.outbound(product1.getId(), 6);

        // then
        Stock stock = stockRepository.findByProductId(product1.getId()).orElseThrow();
        assertThat(stock.getQuantity()).isEqualTo(9);
        assertThat(isBelowSafety).isTrue(); // 9 < 10
    }

    @Test
    @DisplayName("재고보다 많은 수량을 출고하려고 하면 예외가 발생한다.")
    void outbound_fail_with_insufficient_stock() {
        // given
        stockService.inbound(product1.getId(), 10);

        // when & then
        assertThatThrownBy(() -> stockService.outbound(product1.getId(), 11))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고가 부족합니다.");
    }

    @Test
    @DisplayName("존재하지 않는 상품을 입출고하려고 하면 예외가 발생한다.")
    void stock_change_fail_with_non_existent_product() {
        // given
        Long nonExistentProductId = 9999L;

        // when & then
        assertThatThrownBy(() -> stockService.inbound(nonExistentProductId, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 상품입니다.");

        assertThatThrownBy(() -> stockService.outbound(nonExistentProductId, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 상품입니다.");
    }

    @Test
    @DisplayName("전체 상품의 재고 현황을 올바르게 조회한다.")
    void getStockStatusList() {
        // given
        stockService.inbound(product1.getId(), 100); // 재고 100, 안전재고 10
        stockService.inbound(product2.getId(), 3);   // 재고 3, 안전재고 5

        // when
        List<StockStatusDto> statusList = stockService.getStockStatusList();

        // then
        assertThat(statusList).hasSize(2);
        StockStatusDto status1 = statusList.stream().filter(s -> s.getProductId().equals(product1.getId())).findFirst().orElseThrow();
        StockStatusDto status2 = statusList.stream().filter(s -> s.getProductId().equals(product2.getId())).findFirst().orElseThrow();

        assertThat(status1.getCurrentQuantity()).isEqualTo(100);
        assertThat(status1.isBelowSafetyStock()).isFalse();

        assertThat(status2.getCurrentQuantity()).isEqualTo(3);
        assertThat(status2.isBelowSafetyStock()).isTrue();
    }

    @Test
    @DisplayName("안전 재고 미만인 상품 목록만 정확히 조회한다.")
    void getProductsBelowSafetyStock() {
        // given
        stockService.inbound(product1.getId(), 100); // 재고 100, 안전재고 10
        stockService.inbound(product2.getId(), 3);   // 재고 3, 안전재고 5

        // when
        List<StockStatusDto> belowList = stockService.getProductsBelowSafetyStock();

        // then
        assertThat(belowList).hasSize(1);
        assertThat(belowList.get(0).getProductId()).isEqualTo(product2.getId());
        assertThat(belowList.get(0).getCurrentQuantity()).isEqualTo(3);
    }

    @Test
    @DisplayName("특정 기간 동안의 재고 통계 및 회전율을 정확히 계산한다.")
    void getInventoryStatistics() {
        // given
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1); // 기간을 넓게 설정하여 모든 데이터 포함
        stockService.inbound(product1.getId(), 100);
        stockService.outbound(product1.getId(), 20);
        stockService.inbound(product1.getId(), 30);
        stockService.outbound(product1.getId(), 15);
        // 최종 재고: 100 - 20 + 30 - 15 = 95
        // 기간 내 입고: 130, 출고: 35

        // when
        InventoryStatistics stats = stockService.getInventoryStatistics(product1.getId(), start, end);

        // then
        assertThat(stats.getProductId()).isEqualTo(product1.getId());
        assertThat(stats.getTotalInbound()).isEqualTo(130);
        assertThat(stats.getTotalOutbound()).isEqualTo(35);
        assertThat(stats.getCurrentQuantity()).isEqualTo(95);
        assertThat(stats.getTurnoverRate()).isEqualTo((double) 35 / 95);
    }

    @Test
    @DisplayName("재고가 0일 때 재고 회전율은 0이 되어야 한다.")
    void getInventoryStatistics_with_zero_stock() {
        // given
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        stockService.inbound(product1.getId(), 50);
        stockService.outbound(product1.getId(), 50);
        LocalDateTime end = LocalDateTime.now();
        // 최종 재고: 0, 기간 내 출고: 50

        // when
        InventoryStatistics stats = stockService.getInventoryStatistics(product1.getId(), start, end);

        // then
        assertThat(stats.getCurrentQuantity()).isZero();
        assertThat(stats.getTurnoverRate()).isZero();
    }
}
