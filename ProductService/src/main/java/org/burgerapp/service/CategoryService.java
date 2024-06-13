package org.burgerapp.service;

import lombok.RequiredArgsConstructor;

import org.burgerapp.dto.categorydto.request.CategorySaveRequestDto;
import org.burgerapp.entity.Category;

import org.burgerapp.entity.enums.ERole;
import org.burgerapp.exception.ErrorType;
import org.burgerapp.exception.ProductServiceException;
import org.burgerapp.repository.CategoryRepository;
import org.burgerapp.utility.JwtTokenManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final JwtTokenManager jwtTokenManager;
    private final CategoryRepository categoryRepository;

    public void save(CategorySaveRequestDto dto) {

        String roleControl = jwtTokenManager.getRoleFromToken(dto.getToken())
                .orElseThrow(() -> new ProductServiceException(ErrorType.TOKEN_ARGUMENT_NOTVALID));


        if (!roleControl.equals(ERole.ADMIN.name())) {
            throw new ProductServiceException(ErrorType.ROLE_CONTROL_FAILED);
        }


        Category category = Category.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
        categoryRepository.save(category);
    }

    public void delete(Long id) {
        boolean existsById = categoryRepository.existsById(id);
        if (!existsById) {
            throw new ProductServiceException(ErrorType.CATEGORY_NOT_FOUND);
        }
        categoryRepository.deleteById(id);
    }
}
