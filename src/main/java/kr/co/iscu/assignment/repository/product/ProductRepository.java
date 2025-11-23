package kr.co.iscu.assignment.repository.product;

import kr.co.iscu.assignment.domain.product.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Mapper
@Repository
public interface ProductRepository {

    int insert(Product product);

    int update(Product product);

    int deleteById(@Param("id") Long id);

    Optional<Product> findById(@Param("id") Long id);

    List<Product> findAll();
}
