package kr.co.iscu.assignment.repository.stock;

import kr.co.iscu.assignment.domain.stock.Stock;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

/**
 * 재고 Mapper
 */
@Mapper
public interface StockRepository {
    /**
     * 상품 ID로 재고 조회
     * @param productId 상품 ID
     * @return
     */
    Optional<Stock> findByProductId(Long productId);

    /**
     * 모든 재고 정보 조회
     * @return
     */
    List<Stock> findAll();

    /**
     * 재고 정보 저장(insert or update)
     * @param stock
     */
    void save(Stock stock);

    /**
     * 상품 ID로 재고 정보 삭제
     * @param productId 상품 ID
     */
    void deleteByProductId(Long productId);
}
