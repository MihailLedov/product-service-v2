package com.ledok.spring.security.productservice.service;

import com.ledok.spring.security.productservice.advice.ProductNotFoundException;
import com.ledok.spring.security.productservice.controller.dto.ProductDto;
import com.ledok.spring.security.productservice.controller.dto.ProductFilter;
import com.ledok.spring.security.productservice.controller.dto.ProductStockReturnDto;
import com.ledok.spring.security.productservice.controller.dto.ProductStockUpdateDto;
import com.ledok.spring.security.productservice.jpa.entity.ProductEntity;
import com.ledok.spring.security.productservice.jpa.repository.ProductRepository;
import com.ledok.spring.security.productservice.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductEntity createProduct(ProductDto productDto) {
        ProductEntity productEntity = productMapper.toEntity(productDto);
        return productRepository.save(productEntity);
    }

    @Override
    @Transactional
    public List<ProductDto> getAllProducts() {
        List<ProductEntity> productEntities = productRepository.findAll();
        return productEntities.stream().map(productMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Page<ProductDto> getAllProductsFilter(ProductFilter filter, Pageable pageable) {
        Page<ProductEntity> products = productRepository.findAllWithFilter(
                filter.getName(),
                filter.getCategory(),
                filter.getMinPrice(),
                filter.getMaxPrice(),
                pageable);
        return products.map(productMapper::toDto);
    }

    @Override
    @Transactional
    public ProductDto getProductById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toDto)
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найден"));
    }

    @Override
    @Transactional
    public List<ProductDto> getProductsByIds(List<Long> ids) {
        return productRepository.findAllById(ids).stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean checkProductsAvailability(Map<Long, Integer> productQuantities) {
        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            ProductEntity product = productRepository.findById(entry.getKey())
                    .orElseThrow(() -> new ProductNotFoundException("Продукт не найден"));
            if (product.getStock() < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    @Override
    @Transactional
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Продукт c ID: " + id + " не найден");
        }
        Optional<ProductEntity> product = productRepository.findById(id);
        if (product.isPresent()) {
            ProductEntity productEntity = product.get();
            productEntity.setId(productDto.getId());
            productEntity.setName(productDto.getName());
            productEntity.setDescription(productDto.getDescription());
            productEntity.setCategory(productDto.getCategory());
            productEntity.setPrice(productDto.getPrice());
            productEntity.setStock(productDto.getStock());
            return productMapper.toDto(productRepository.save(productEntity));
        }
        throw new ProductNotFoundException("Продукт c ID: " + id + " не найден");
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        }
        throw new ProductNotFoundException("Продукт c ID: " + id + " не найден");
    }

    @Override
    @Transactional
    public void updateProductsStock(List<ProductStockUpdateDto> updates) {
        // Получаем все ID продуктов
        Set<Long> productIds = updates.stream()
                .map(ProductStockUpdateDto::getProductId)
                .collect(Collectors.toSet());

        // Получаем продукты
        Map<Long, ProductEntity> products = productRepository.findAllById(productIds)
                .stream()
                .collect(Collectors.toMap(ProductEntity::getId, Function.identity()));

        // Обновляем остатки
        updates.forEach(update -> {
            ProductEntity product = products.get(update.getProductId());
            if (product == null) {
                throw new ProductNotFoundException("Продукт c ID: " + update.getProductId() + " не найден");
            }

            int newStock = product.getStock() - update.getQuantityToSubtract();
            if (newStock < 0) {
                throw new ProductNotFoundException("Продукта с ID: " + product.getId() + " нет в наличии");
            }

            product.setStock(newStock);
        });

        productRepository.saveAll(products.values());
    }

    @Override
    @Transactional
    public void returnProductsStock(List<ProductStockReturnDto> returns) {
        // Получаем все ID продуктов
        Set<Long> productIds = returns.stream()
                .map(ProductStockReturnDto::getProductId)
                .collect(Collectors.toSet());

        // Получаем продукты
        Map<Long, ProductEntity> products = productRepository.findAllById(productIds)
                .stream()
                .collect(Collectors.toMap(ProductEntity::getId, Function.identity()));

        returns.forEach(update -> {
                ProductEntity product = products.get(update.getProductId());
        if (product == null) {
            throw new ProductNotFoundException("Продукт c ID: " + update.getProductId() + " не найден");
        }
            if (update.getQuantityToAdd() <= 0) {
                throw new IllegalArgumentException(
                        "Некорректное количество для продукта " + update.getProductId() + ": " + update.getQuantityToAdd()
                );
            }

        int newStock = product.getStock() + update.getQuantityToAdd();

        product.setStock(newStock);
    });
        productRepository.saveAll(products.values());
    }
}