package org.burgerapp.service;

import lombok.RequiredArgsConstructor;
import org.burgerapp.dto.productdto.request.ProductAddItemsDto;
import org.burgerapp.dto.productdto.request.ProductRemoveItemsDto;
import org.burgerapp.dto.productdto.request.ProductSaveRequestDto;
import org.burgerapp.entity.Items;
import org.burgerapp.entity.Product;
import org.burgerapp.entity.enums.ERole;
import org.burgerapp.exception.ErrorType;
import org.burgerapp.exception.ProductServiceException;
import org.burgerapp.repository.CategoryRepository;
import org.burgerapp.repository.ItemsRepository;
import org.burgerapp.repository.ProductRepository;
import org.burgerapp.utility.JwtTokenManager;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ItemsRepository itemsRepository;
    private final CategoryRepository categoryRepository;
    private final JwtTokenManager jwtTokenManager;


    public Product addItemsToProduct(ProductAddItemsDto dto) {

        String roleControl = jwtTokenManager.getRoleFromToken(dto.getToken())
                .orElseThrow(() -> new ProductServiceException(ErrorType.TOKEN_ARGUMENT_NOTVALID));


        if (!roleControl.equals(ERole.ADMIN.name())) {
            throw new ProductServiceException(ErrorType.ROLE_CONTROL_FAILED);
        }

        Optional<Product> productOpt = productRepository.findById(dto.getProductId());
        Optional<Items> itemsOpt = itemsRepository.findById(dto.getItemsId());

        if (productOpt.isPresent() && itemsOpt.isPresent()) {
            Product product = productOpt.get();
            Items items = itemsOpt.get();
            product.getItems().add(items);
            return productRepository.save(product);
        }
        throw new RuntimeException("Product or Option not found");
    }
/*
    public Product removeItemsFromProduct(ProductRemoveItemsDto dto) {

        String roleControl = jwtTokenManager.getRoleFromToken(dto.getToken())
                .orElseThrow(() -> new ProductServiceException(ErrorType.TOKEN_ARGUMENT_NOTVALID));

        if (!roleControl.equals(ERole.ADMIN.name())) {
            throw new ProductServiceException(ErrorType.ROLE_CONTROL_FAILED);
        }

        Optional<Product> productOpt = productRepository.findById(dto.getProductId());
        Optional<Items> itemsOpt = itemsRepository.findById(dto.getItemsId());

        if (productOpt.isPresent() && itemsOpt.isPresent()) {
            Product product = productOpt.get();
            Items items = itemsOpt.get();
            if (product.getItems().contains(items)) {
                product.getItems().remove(items);
                return productRepository.save(product);
            } else {
                throw new RuntimeException("Item not found in product");
            }
        }
        throw new RuntimeException("Product or Item not found");
    }

 */

    public Product createProduct(ProductSaveRequestDto dto) {

        String roleControl = jwtTokenManager.getRoleFromToken(dto.getToken())
                .orElseThrow(() -> new ProductServiceException(ErrorType.TOKEN_ARGUMENT_NOTVALID));


        if (!roleControl.equals(ERole.ADMIN.name())) {
            throw new ProductServiceException(ErrorType.ROLE_CONTROL_FAILED);
        }
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        categoryRepository.findById(dto.getCategoryId()).ifPresent(product::setCategory);

        Set<Items> items = new HashSet<>();

        for (Long itemsId : dto.getItemsIds()) {
            Optional<Items> itemsOpt = itemsRepository.findById(itemsId);
            itemsOpt.ifPresent(items::add);
        }
        product.setItems(items);
        return productRepository.save(product);
    }



}
