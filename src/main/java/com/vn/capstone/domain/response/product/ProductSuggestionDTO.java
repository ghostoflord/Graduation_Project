package com.vn.capstone.domain.response.product;

public class ProductSuggestionDTO {
    private Long id;
    private String name;
    private String price; // đã xử lý final price
    private String image;
    private String slug;

    public ProductSuggestionDTO(Long id, String name, String slug, String price, String image) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.price = price;
        this.image = image;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

}
