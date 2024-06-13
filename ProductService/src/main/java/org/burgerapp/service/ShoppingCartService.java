package org.burgerapp.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.burgerapp.dto.cartdto.AddToCartRequestDto;
import org.burgerapp.dto.cartdto.OrderMessageDto;
import org.burgerapp.dto.cartdto.ViewCartRequestDto;
import org.burgerapp.entity.CartItem;
import org.burgerapp.entity.Items;
import org.burgerapp.entity.Product;
import org.burgerapp.entity.ShoppingCart;
import org.burgerapp.entity.enums.ERole;
import org.burgerapp.exception.ErrorType;
import org.burgerapp.exception.ProductServiceException;
import org.burgerapp.repository.CartItemRepository;
import org.burgerapp.repository.ItemsRepository;
import org.burgerapp.repository.ProductRepository;
import org.burgerapp.repository.ShoppingCartRepository;
import org.burgerapp.utility.JwtTokenManager;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ShoppingCartService {
    private final JwtTokenManager jwtTokenManager;

    private final ProductRepository productRepository;

    private final ItemsRepository itemsRepository;

    private final ShoppingCartRepository shoppingCartRepository;

    private final CartItemRepository cartItemRepository;

    private final RabbitTemplate rabbitTemplate;

    public ShoppingCart addToCart(AddToCartRequestDto dto) {
        String roleControl = jwtTokenManager.getRoleFromToken(dto.getToken())
                .orElseThrow(() -> new ProductServiceException(ErrorType.TOKEN_ARGUMENT_NOTVALID));

        if (!roleControl.equals(ERole.USER.name())) {
            throw new ProductServiceException(ErrorType.ROLE_CONTROL_FAILED);
        }

        Optional<Product> productOpt = productRepository.findById(dto.getProductId());

        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(dto.getQuantity());

            Set<Items> items = new HashSet<>();
            for (Long itemId : dto.getItemIds()) {
                itemsRepository.findById(itemId).ifPresent(items::add);
            }
            cartItem.setItems(items);
            Long authId = jwtTokenManager.getIdFromToken(dto.getToken()).orElseThrow(() -> new ProductServiceException(ErrorType.TOKEN_ARGUMENT_NOTVALID));

            ShoppingCart shoppingCart = shoppingCartRepository.findByAuthId(authId).orElseGet(() -> {
                ShoppingCart newCart = new ShoppingCart();
                newCart.setAuthId(authId);
                return shoppingCartRepository.save(newCart);
            });
           double productTotalPrice = (product.getPrice() * cartItem.getQuantity());
            double itemTotalPrice = 0.0;

            for (Items item : items) {
                itemTotalPrice += item.getPrice();
            }

            double cartTotalPrice = shoppingCart.getTotalPrice()+ itemTotalPrice + productTotalPrice;
            shoppingCart.setTotalPrice(cartTotalPrice);




            cartItem.setShoppingCart(shoppingCart);
            shoppingCart.getItems().add(cartItem);
            cartItemRepository.save(cartItem); // save the cart item separately
            shoppingCartRepository.save(shoppingCart);
            return shoppingCart;
        }
        throw new ProductServiceException(ErrorType.CART_NOT_FOUND);
    }

    public ShoppingCart viewCart(String token) {
        Long authId = jwtTokenManager.getIdFromToken(token).orElseThrow(() -> new ProductServiceException(ErrorType.TOKEN_ARGUMENT_NOTVALID));
        ShoppingCart shoppingCart = shoppingCartRepository.findByAuthId(authId).orElseThrow(() -> new ProductServiceException(ErrorType.CART_NOT_FOUND));

        List<Product> productList = shoppingCart.getProductList();
        List<Items> itemList = shoppingCart.getItemList();

        return shoppingCart;
    }

    public void checkout(Long cartId, Long userId) {
        OrderMessageDto orderMessage = new OrderMessageDto();
        orderMessage.setCartId(cartId);
        orderMessage.setUserId(userId);
        ShoppingCart shoppingCart = shoppingCartRepository.findById(cartId).orElseThrow(() -> new ProductServiceException(ErrorType.CART_NOT_FOUND));
        orderMessage.setTotalPrice(shoppingCart.getTotalPrice());

        try {
            rabbitTemplate.convertAndSend("exchange.burgerrest", "routing.key.order", orderMessage);
        } catch (AmqpException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send message to RabbitMQ", e);
        }
    }



}
