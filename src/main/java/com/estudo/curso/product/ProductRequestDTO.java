package com.estudo.curso.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ProductRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String name;
    private String description;
    @NotNull(message = "Preço é obrigatório")
    @Positive(message = "Preço deve ser maior que zero")
    private Double price;
    private String imgUrl;

    public ProductRequestDTO() {}

    public String getName()        { return name; }
    public void   setName(String name)        { this.name = name; }

    public String getDescription() { return description; }
    public void   setDescription(String description) { this.description = description; }

    public Double getPrice()       { return price; }
    public void   setPrice(Double price)      { this.price = price; }

    public String getImgUrl()      { return imgUrl; }
    public void   setImgUrl(String imgUrl)    { this.imgUrl = imgUrl; }
}