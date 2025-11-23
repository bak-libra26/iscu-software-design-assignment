package kr.co.iscu.assignment.repository.product;

import kr.co.iscu.assignment.domain.product.Product;
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
@Transactional // 각 테스트 후 롤백하여 테스트 간 독립성 보장
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("상품을 저장하고 ID로 조회할 수 있다.")
    void insertAndFindById() {
        // given
        Product product = Product.builder()
                .name("새 상품")
                .category("가전")
                .unitPrice(new BigDecimal("150000"))
                .safetyStock(10)
                .build();

        // when
        productRepository.insert(product);
        Optional<Product> foundProductOpt = productRepository.findById(product.getId());

        // then
        assertThat(foundProductOpt).isPresent();
        Product foundProduct = foundProductOpt.get();
        assertThat(foundProduct.getName()).isEqualTo("새 상품");
        assertThat(foundProduct.getSafetyStock()).isEqualTo(10);
    }

    @Test
    @DisplayName("모든 상품을 조회할 수 있다.")
    void findAll() {
        // given
        Product product1 = Product.builder().name("상품1").category("A").unitPrice(BigDecimal.ONE).safetyStock(1).build();
        Product product2 = Product.builder().name("상품2").category("B").unitPrice(BigDecimal.TEN).safetyStock(2).build();
        productRepository.insert(product1);
        productRepository.insert(product2);

        // when
        List<Product> products = productRepository.findAll();

        // then
        // 기존에 다른 테스트에서 추가한 데이터가 있을 수 있으므로 2개 이상인지만 체크
        assertThat(products.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("상품 정보를 수정할 수 있다.")
    void update() {
        // given
        Product product = Product.builder().name("원본 상품").category("C").unitPrice(new BigDecimal("100")).safetyStock(5).build();
        productRepository.insert(product);

        // when
        product.setName("수정된 상품");
        product.setSafetyStock(20);
        productRepository.update(product);
        Product updatedProduct = productRepository.findById(product.getId()).orElseThrow();

        // then
        assertThat(updatedProduct.getName()).isEqualTo("수정된 상품");
        assertThat(updatedProduct.getSafetyStock()).isEqualTo(20);
    }

    @Test
    @DisplayName("상품을 ID로 삭제할 수 있다.")
    void deleteById() {
        // given
        Product product = Product.builder().name("삭제될 상품").category("D").unitPrice(new BigDecimal("999")).safetyStock(1).build();
        productRepository.insert(product);
        Long productId = product.getId();

        // when
        int deletedCount = productRepository.deleteById(productId);
        Optional<Product> foundProductOpt = productRepository.findById(productId);

        // then
        assertThat(deletedCount).isEqualTo(1);
        assertThat(foundProductOpt).isNotPresent();
    }
}
