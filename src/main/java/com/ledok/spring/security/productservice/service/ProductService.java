package com.ledok.spring.security.productservice.service;

import com.ledok.spring.security.productservice.controller.dto.ProductDto;
import com.ledok.spring.security.productservice.controller.dto.ProductFilter;
import com.ledok.spring.security.productservice.controller.dto.ProductStockUpdateDto;
import com.ledok.spring.security.productservice.jpa.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ProductService {

    ProductEntity createProduct(ProductDto productDto);

    List<ProductDto> getAllProducts();

    Page<ProductDto> getAllProductsFilter(ProductFilter filter, Pageable pageable);

    ProductDto getProductById(Long id);

    List<ProductDto> getProductsByIds(List<Long> ids);

    boolean checkProductsAvailability(Map<Long, Integer> productQuantities);

    ProductDto updateProduct(Long id, ProductDto productDto);

    void deleteProduct(Long id);

    void updateProductsStock(List<ProductStockUpdateDto> updates);
}