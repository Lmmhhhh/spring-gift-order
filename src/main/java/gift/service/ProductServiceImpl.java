package gift.service;

import gift.domain.Option;
import gift.domain.Product;
import gift.dto.request.ProductRequest;
import gift.dto.request.ProductUpdateRequest;
import gift.dto.response.ProductResponse;
import gift.exception.ProductNotFoundException;
import gift.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public ProductResponse register(ProductRequest request) {
        List<Option> options = request.options().stream()
                .map(o -> Option.of(null, o.name(), o.quantity()))
                .toList();

        Product product = new Product(
                request.name(),
                request.price(),
                request.imageUrl(),
                options
        );

        Product saved = productRepository.save(product);
        return new ProductResponse(saved);
    }

    @Override
    public ProductResponse getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        return new ProductResponse(product);
    }

    @Override
    public Page<ProductResponse> getAllProducts (Pageable pageable){
        return  productRepository.findAll(pageable)
                .map(ProductResponse::from);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long productId, ProductUpdateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(()->new ProductNotFoundException(productId));

        String name = product.getName();
        if (request.name() != null && !request.name().isBlank()){
            name = request.name();
        }

        int price = product.getPrice();
        if (request.price() != null && request.price() > 0){
            price = request.price();
        }

        String imageUrl = product.getImageUrl();
        if (request.imageUrl() != null && !request.imageUrl().isBlank()){
            imageUrl = request.imageUrl();
        }

        return new ProductResponse(product);
    }

    @Override
    public void deleteProduct(Long productId) {
        productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        productRepository.deleteById(productId);
    }

    @Override
    public Page<ProductResponse> searchByName(String keyword, Pageable pageable) {
        return productRepository.findByNameContaining(keyword, pageable)
                .map(ProductResponse::from);
    }
}
