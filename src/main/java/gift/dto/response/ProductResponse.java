package gift.dto.response;

import gift.domain.Product;

import java.util.List;

public record ProductResponse(Long id, String name, int price, String imageUrl, List<OptionResponse> options) {
    public ProductResponse(Product product) {
        this(
            product.getId(),
            product.getName(),
            product.getPrice(),
            product.getImageUrl(),
                product.getOptions().stream()
                        .map(OptionResponse::from)
                        .toList()
        );
    }

    public static ProductResponse from(Product product){
        return new ProductResponse(product);
    }
}
