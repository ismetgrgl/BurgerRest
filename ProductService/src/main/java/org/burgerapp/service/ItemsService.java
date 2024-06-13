package org.burgerapp.service;

import lombok.RequiredArgsConstructor;
import org.burgerapp.dto.itemdto.request.ItemSaveRequestDto;
import org.burgerapp.entity.Items;
import org.burgerapp.entity.enums.ERole;
import org.burgerapp.exception.ErrorType;
import org.burgerapp.exception.ProductServiceException;
import org.burgerapp.repository.ItemsRepository;
import org.burgerapp.utility.JwtTokenManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemsService {

    private final ItemsRepository itemsRepository;
    private final JwtTokenManager jwtTokenManager;


    public Items createItems(ItemSaveRequestDto dto) {
        String roleControl = jwtTokenManager.getRoleFromToken(dto.getToken())
                .orElseThrow(() -> new ProductServiceException(ErrorType.TOKEN_ARGUMENT_NOTVALID));


        if (!roleControl.equals(ERole.ADMIN.name())) {
            throw new ProductServiceException(ErrorType.ROLE_CONTROL_FAILED);
        }
        Items items = Items.builder()
                .name(dto.getName())
                .type(dto.getType())
                .price(dto.getPrice()).build();
        return itemsRepository.save(items);

    }


    public List<Items> getAllItems() {
        return itemsRepository.findAll();
    }

    public List<Items> getItemsByType(String type) {
        return itemsRepository.findByType(type);
    }

}
