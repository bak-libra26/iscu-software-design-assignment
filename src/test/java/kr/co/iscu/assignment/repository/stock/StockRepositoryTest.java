package kr.co.iscu.assignment.repository.stock;

import kr.co.iscu.assignment.domain.product.Product;
import kr.co.iscu.assignment.domain.stock.Stock;
import kr.co.iscu.assignment.repository.product.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class StockRepositoryTest {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private ProductRepository productRepository;

    private Product product;

    @BeforeEach
    void setUp() {
        // 재고 테스트를 위한 상품 데이터 생성
        product = Product.builder().name("재고 테스트용 상품").category("테스트").unitPrice(BigDecimal.ZERO).safetyStock(0).build();
        productRepository.insert(product);
    }

    @Test
    @DisplayName("재고 정보를 새로 저장(INSERT)할 수 있다.")
    void save_insert() {
        // given
        Stock newStock = new Stock(product.getId(), 100);

        // when
        stockRepository.save(newStock);
        Optional<Stock> foundStockOpt = stockRepository.findByProductId(product.getId());

        // then
        assertThat(foundStockOpt).isPresent();
        assertThat(foundStockOpt.get().getQuantity()).isEqualTo(100);
    }

    @Test
    @DisplayName("기존 재고 정보를 수정(UPDATE)할 수 있다.")
    void save_update() {
        // given
        Stock initialStock = new Stock(product.getId(), 50);
        stockRepository.save(initialStock);

        // when
        Stock updatedStock = new Stock(product.getId(), 120);
        stockRepository.save(updatedStock);
        Optional<Stock> foundStockOpt = stockRepository.findByProductId(product.getId());

        // then
        assertThat(foundStockOpt).isPresent();
        assertThat(foundStockOpt.get().getQuantity()).isEqualTo(120);
    }

    @Test
    @DisplayName("모든 재고 정보를 조회할 수 있다.")
    void findAll() {
        // given
        Product anotherProduct = Product.builder().name("추가 상품").category("테스트").unitPrice(BigDecimal.ZERO).safetyStock(0).build();
        productRepository.insert(anotherProduct);

        stockRepository.save(new Stock(product.getId(), 10));
        stockRepository.save(new Stock(anotherProduct.getId(), 20));

        // when
        List<Stock> allStocks = stockRepository.findAll();

        // then
        assertThat(allStocks.size()).isGreaterThanOrEqualTo(2);
        assertThat(allStocks).extracting(Stock::getProductId).contains(product.getId(), anotherProduct.getId());
    }
}
