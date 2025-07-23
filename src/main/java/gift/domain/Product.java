package gift.domain;


import gift.exception.DuplicateOptionNameException;
import gift.exception.ProductOptionEmptyException;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 15, nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> options = new ArrayList<>();

    protected Product(){

    }

    public Product(String name, int price, String imageUrl, List<Option> options) {
        validateOptions(options);
        validateDuplicateOptionNames(options);
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.options = options;
    }


    private void validateOptions(List<Option> options) {
        if (options == null || options.isEmpty()) {
            throw new ProductOptionEmptyException();
        }
    }

    private void validateDuplicateOptionNames(List<Option> options) {
        Set<String> seen = new HashSet<>();
        for (Option option : options) {
            if (!seen.add(option.getName())) {
                throw new DuplicateOptionNameException(option.getName());
            }
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<Option> getOptions() {
        return options;
    }
}
