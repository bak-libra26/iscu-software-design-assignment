package kr.co.iscu.assignment.service.product;

import kr.co.iscu.assignment.domain.product.Product;
import kr.co.iscu.assignment.repository.product.ProductRepository;
import kr.co.iscu.assignment.repository.stock.StockRepository;
import kr.co.iscu.assignment.service.product.dto.ProductCreateDto;
import kr.co.iscu.assignment.service.product.dto.ProductUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * 상품 관련 비즈니스 로직을 처리하는 서비스 클래스.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final StockRepository stockRepository;

    /**
     * 새로운 상품을 등록합니다.
     *
     * @param createDto 상품 생성 정보 DTO
     * @return 생성된 상품 정보
     */
    @Transactional
    public Product createProduct(ProductCreateDto createDto) {
        Product product = createDto.toEntity();
        productRepository.insert(product);
        return product;
    }

    /**
     * 모든 상품 목록을 조회합니다.
     *
     * @return 상품 목록
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * ID로 특정 상품을 조회합니다.
     *
     * @param productId 조회할 상품의 ID
     * @return 조회된 상품 정보
     * @throws NoSuchElementException 해당 ID의 상품이 없을 경우
     */
    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다. ID: " + productId));
    }

    /**
     * 상품 정보를 수정합니다.
     *
     * @param productId 수정할 상품의 ID
     * @param updateDto 상품 수정 정보 DTO
     * @return 수정된 상품 정보
     */
    @Transactional
    public Product updateProduct(Long productId, ProductUpdateDto updateDto) {
        Product product = getProductById(productId);

        product.setName(updateDto.getName());
        product.setCategory(updateDto.getCategory());
        product.setUnitPrice(updateDto.getUnitPrice());
        product.setSafetyStock(updateDto.getSafetyStock());

        productRepository.update(product);
        return product;
    }

    /**
     * 상품을 삭제합니다.
     * <p>
     * 단, 해당 상품의 재고가 0인 경우에만 삭제할 수 있습니다.
     *
     * @param productId 삭제할 상품의 ID
     * @throws IllegalStateException 재고가 남아있을 경우
     */
    @Transactional
    public void deleteProduct(Long productId) {
        // 재고가 남아있는지 확인
        stockRepository.findByProductId(productId).ifPresent(stock -> {
            if (stock.getQuantity() > 0) {
                throw new IllegalStateException("재고가 남아있는 상품은 삭제할 수 없습니다. (재고: " + stock.getQuantity() + ")");
            }
        });

        productRepository.deleteById(productId);
    }
}
