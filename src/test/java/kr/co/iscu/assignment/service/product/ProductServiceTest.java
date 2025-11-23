package kr.co.iscu.assignment.service.product;

import kr.co.iscu.assignment.domain.product.Product;
import kr.co.iscu.assignment.repository.stock.StockHistoryRepository;
import kr.co.iscu.assignment.repository.stock.StockRepository;
import kr.co.iscu.assignment.service.product.dto.ProductCreateDto;
import kr.co.iscu.assignment.service.product.dto.ProductUpdateDto;
import kr.co.iscu.assignment.service.stock.StockService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private StockService stockService;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StockHistoryRepository stockHistoryRepository;

    private final List<Long> productIdsToDelete = new ArrayList<>();

    @AfterEach
    void tearDown() {
        productIdsToDelete.forEach(productId -> {
            stockHistoryRepository.deleteByProductId(productId);
            stockRepository.deleteByProductId(productId);
        });
        productIdsToDelete.clear();
    }


    @Test
    @DisplayName("새로운 상품을 성공적으로 생성한다.")
    void createProduct() {
        // given
        ProductCreateDto createDto = ProductCreateDto.builder()
                .name("신규 서비스 상품")
                .category("서비스 테스트")
                .unitPrice(new BigDecimal("9900"))
                .safetyStock(5)
                .build();

        // when
        Product createdProduct = productService.createProduct(createDto);
        productIdsToDelete.add(createdProduct.getId());


        // then
        assertThat(createdProduct.getId()).isNotNull();
        assertThat(createdProduct.getName()).isEqualTo("신규 서비스 상품");
    }

    @Test
    @DisplayName("상품 정보를 성공적으로 수정한다.")
    void updateProduct() {
        // given
        ProductCreateDto createDto = ProductCreateDto.builder().name("원본").category("원본").unitPrice(BigDecimal.ONE).safetyStock(1).build();
        Product product = productService.createProduct(createDto);
        productIdsToDelete.add(product.getId());

        ProductUpdateDto updateDto = ProductUpdateDto.builder()
                .name("수정본")
                .category("수정본")
                .unitPrice(BigDecimal.TEN)
                .safetyStock(10)
                .build();

        // when
        Product updatedProduct = productService.updateProduct(product.getId(), updateDto);

        // then
        assertThat(updatedProduct.getName()).isEqualTo("수정본");
        assertThat(updatedProduct.getCategory()).isEqualTo("수정본");
        assertThat(updatedProduct.getSafetyStock()).isEqualTo(10);
    }

    @Test
    @DisplayName("재고가 없는 상품은 성공적으로 삭제된다.")
    void deleteProduct_success_when_stock_is_zero() {
        // given
        ProductCreateDto createDto = ProductCreateDto.builder()
                .name("삭제될 상품")
                .category("테스트")
                .unitPrice(BigDecimal.ONE)
                .safetyStock(0)
                .build();
        Product product = productService.createProduct(createDto);
        productIdsToDelete.add(product.getId());


        // when & then
        productService.deleteProduct(product.getId());

        // 삭제 확인
        assertThatThrownBy(() -> productService.getProductById(product.getId()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("재고가 남아있는 상품을 삭제하려고 하면 예외가 발생한다.")
    void deleteProduct_fail_when_stock_exists() {
        // given
        ProductCreateDto createDto = ProductCreateDto.builder()
                .name("재고 있는 상품")
                .category("테스트")
                .unitPrice(BigDecimal.ONE)
                .safetyStock(0)
                .build();
        Product product = productService.createProduct(createDto);
        productIdsToDelete.add(product.getId());
        stockService.inbound(product.getId(), 10); // 재고 추가

        // when & then
        assertThatThrownBy(() -> productService.deleteProduct(product.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("재고가 남아있는 상품은 삭제할 수 없습니다.");
    }

    @Test
    @DisplayName("재고가 있다가 0이 된 상품은 성공적으로 삭제된다.")
    void deleteProduct_success_when_stock_becomes_zero() {
        // given
        ProductCreateDto createDto = ProductCreateDto.builder()
                .name("재고 소진 상품")
                .category("테스트")
                .unitPrice(BigDecimal.ONE)
                .safetyStock(0)
                .build();
        Product product = productService.createProduct(createDto);
        productIdsToDelete.add(product.getId());
        stockService.inbound(product.getId(), 10);
        stockService.outbound(product.getId(), 10);

        // when
        stockHistoryRepository.deleteByProductId(product.getId());
        stockRepository.deleteByProductId(product.getId()); // 재고 레코드도 삭제
        productService.deleteProduct(product.getId());

        // then
        assertThat(stockRepository.findByProductId(product.getId())).isNotPresent();
        assertThatThrownBy(() -> productService.getProductById(product.getId()))
                .isInstanceOf(NoSuchElementException.class);
    }
}
