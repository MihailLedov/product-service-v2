package com.ledok.spring.security.productservice.mapper;

import com.ledok.spring.security.productservice.controller.dto.ProductDto;
import com.ledok.spring.security.productservice.jpa.entity.ProductEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductDto toDto(ProductEntity entity) {
        return ProductDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .stock(entity.getStock())
                .category(entity.getCategory())
                .build();
    }

    public ProductEntity toEntity(ProductDto dto) {
        return ProductEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .category(dto.getCategory())
                .build();
    }
}