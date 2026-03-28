package com.estudo.curso.dto;

import com.estudo.curso.entities.Category;
import com.estudo.curso.entities.Product;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * DTO de resposta para Product.
 * Expõe somente os campos necessários, sem vazar a entidade JPA diretamente.
 */
public class ProductDTO {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private String imgUrl;
    // Retorna só os ids das categorias — evita carregar todo o grafo de objetos
    private Set<Long> categoryIds;

    // ── Construtor a partir da entidade ──────────────────────────────────────
    public ProductDTO(Product product) {
        this.id          = product.getId();
        this.name        = product.getName();
        this.description = product.getDescription();
        this.price       = product.getPrice();
        this.imgUrl      = product.getImgUrl();
        this.categoryIds = product.getCategory()
                                  .stream()
                                  .map(Category::getId)
                                  .collect(Collectors.toSet());
    }

    // ── Getters (sem setters — DTO de resposta é imutável) ───────────────────
    public Long getId()          { return id; }
    public String getName()      { return name; }
    public String getDescription(){ return description; }
    public Double getPrice()     { return price; }
    public String getImgUrl()    { return imgUrl; }
    public Set<Long> getCategoryIds() { return categoryIds; }
}