package gift.service;

import gift.dto.request.ProductRequest;
import gift.dto.request.ProductUpdateRequest;
import gift.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    ProductResponse register(ProductRequest request);

    ProductResponse getProduct(Long productId);

    Page<ProductResponse> getAllProducts(Pageable pageable);

    Page<ProductResponse> searchByName(String keyword, Pageable pageable);

    ProductResponse updateProduct(Long productId, ProductUpdateRequest request);

    void deleteProduct(Long productId);
}