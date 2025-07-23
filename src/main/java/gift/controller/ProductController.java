package gift.controller;


import gift.domain.Option;
import gift.dto.request.ProductRequest;
import gift.dto.request.ProductUpdateRequest;
import gift.dto.response.OptionResponse;
import gift.dto.response.PageResponse;
import gift.dto.response.ProductResponse;
import gift.service.OptionService;
import gift.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final OptionService optionService;

    public ProductController(ProductService productService, OptionService optionService) {

        this.productService = productService;
        this.optionService = optionService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> register(@RequestBody @Valid ProductRequest request) {
        ProductResponse response = productService.register(request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProduct(productId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<ProductResponse>> getAllProducts(Pageable pageable) {
        Page<ProductResponse> page = productService.getAllProducts(pageable);
        return ResponseEntity.ok(PageResponse.from(page));
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long productId, @RequestBody @Valid ProductUpdateRequest request){
        ProductResponse response = productService.updateProduct(productId,request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<ProductResponse>> searchByName(@RequestParam String name,
                                                                      Pageable pageable) {
        Page<ProductResponse> page = productService.searchByName(name, pageable);
        return ResponseEntity.ok(PageResponse.from(page));
    }

    @GetMapping ("/{productId}/options")
    public ResponseEntity<PageResponse<OptionResponse>> getOptions(
            @PathVariable Long productId,
            Pageable pageable
    ){
        return ResponseEntity.ok(
                PageResponse.from(optionService.getOptions(productId,pageable))
        );
    }
}