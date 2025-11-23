package kr.co.iscu.assignment.repository.stock;

import kr.co.iscu.assignment.domain.product.Product;
import kr.co.iscu.assignment.domain.stock.StockEventType;
import kr.co.iscu.assignment.domain.stock.StockHistory;
import kr.co.iscu.assignment.repository.product.ProductRepository;
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

@SpringBootTest
@Transactional
class StockHistoryRepositoryTest {

    @Autowired
    private StockHistoryRepository stockHistoryRepository;

    @Autowired
    private ProductRepository productRepository;

    private Product product;

    @BeforeEach
    void setUp() {
        // 이력 테스트를 위한 상품 데이터 생성
        product = Product.builder().name("이력 테스트용 상품").category("테스트").unitPrice(BigDecimal.ZERO).safetyStock(0).build();
        productRepository.insert(product);
    }

    @Test
    @DisplayName("재고 이력을 저장하고 ID로 조회할 수 있다.")
    void saveAndFindByProductId() {
        // given
        StockHistory inbound = StockHistory.builder()
                .productId(product.getId())
                .eventType(StockEventType.INBOUND)
                .quantity(100)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();
        StockHistory outbound = StockHistory.builder()
                .productId(product.getId())
                .eventType(StockEventType.OUTBOUND)
                .quantity(20)
                .createdAt(LocalDateTime.now())
                .build();

        // when
        stockHistoryRepository.save(inbound);
        stockHistoryRepository.save(outbound);
        List<StockHistory> histories = stockHistoryRepository.findByProductId(product.getId());

        // then
        assertThat(histories).hasSize(2);
        // 최신순 정렬 검증
        assertThat(histories.get(0).getEventType()).isEqualTo(StockEventType.OUTBOUND);
        assertThat(histories.get(1).getEventType()).isEqualTo(StockEventType.INBOUND);
    }

    @Test
    @DisplayName("특정 기간 동안의 거래 유형별 수량 합계를 정확히 계산한다.")
    void sumQuantityByEventTypeBetweenDates() {
        // given
        LocalDateTime start = LocalDateTime.now().minusDays(5);
        LocalDateTime end = LocalDateTime.now();

        // 기간 내 데이터
        stockHistoryRepository.save(StockHistory.builder().productId(product.getId()).eventType(StockEventType.INBOUND).quantity(100).createdAt(start.plusDays(1)).build());
        stockHistoryRepository.save(StockHistory.builder().productId(product.getId()).eventType(StockEventType.INBOUND).quantity(50).createdAt(start.plusDays(2)).build());
        stockHistoryRepository.save(StockHistory.builder().productId(product.getId()).eventType(StockEventType.OUTBOUND).quantity(30).createdAt(start.plusDays(3)).build());

        // 기간 외 데이터
        stockHistoryRepository.save(StockHistory.builder().productId(product.getId()).eventType(StockEventType.INBOUND).quantity(1000).createdAt(start.minusDays(1)).build());

        // when
        int totalInbound = stockHistoryRepository.sumQuantityByEventTypeBetweenDates(product.getId(), StockEventType.INBOUND, start, end);
        int totalOutbound = stockHistoryRepository.sumQuantityByEventTypeBetweenDates(product.getId(), StockEventType.OUTBOUND, start, end);

        // then
        assertThat(totalInbound).isEqualTo(150);
        assertThat(totalOutbound).isEqualTo(30);
    }

    @Test
    @DisplayName("기간 내 데이터가 없으면 합계는 0을 반환한다.")
    void sumQuantity_should_return_zero_if_no_data() {
        // given
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();

        // when
        int totalInbound = stockHistoryRepository.sumQuantityByEventTypeBetweenDates(product.getId(), StockEventType.INBOUND, start, end);

        // then
        assertThat(totalInbound).isZero();
    }
}
