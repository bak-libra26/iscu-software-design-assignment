package kr.co.iscu.assignment.controller;

import kr.co.iscu.assignment.domain.product.Product;
import kr.co.iscu.assignment.service.product.ProductService;
import kr.co.iscu.assignment.service.product.dto.ProductCreateDto;
import kr.co.iscu.assignment.service.product.dto.ProductUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 상품 관리 REST API 컨트롤러
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 새로운 상품을 등록합니다.
     *
     * @param createDto 상품 생성 정보
     * @return 생성된 상품 정보
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody ProductCreateDto createDto) {
        Product product = productService.createProduct(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    /**
     * 모든 상품 목록을 조회합니다.
     *
     * @return 상품 목록
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * ID로 특정 상품을 조회합니다.
     *
     * @param id 상품 ID
     * @return 상품 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    /**
     * 상품 정보를 수정합니다.
     *
     * @param id        상품 ID
     * @param updateDto 상품 수정 정보
     * @return 수정된 상품 정보
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductUpdateDto updateDto) {
        Product product = productService.updateProduct(id, updateDto);
        return ResponseEntity.ok(product);
    }

    /**
     * 상품을 삭제합니다.
     * 재고가 0인 경우에만 삭제 가능합니다.
     *
     * @param id 상품 ID
     * @return 응답 상태
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
