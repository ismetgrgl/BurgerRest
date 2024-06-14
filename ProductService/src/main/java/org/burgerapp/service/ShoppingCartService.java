package org.burgerapp.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.burgerapp.dto.cartdto.AddToCartRequestDto;
import org.burgerapp.dto.cartdto.OrderMessageDto;
import org.burgerapp.dto.cartdto.ViewCartRequestDto;
import org.burgerapp.dto.productdto.request.RemoveFromCartRequestDto;
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
            Optional<ShoppingCart> cart = shoppingCartRepository.findByAuthId(authId);
            if (cart.isPresent() && cart.get().getTotalPrice()==0.0) {
                shoppingCartRepository.deleteById(cart.get().getId());
            }

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

    public ShoppingCart removeFromCart(RemoveFromCartRequestDto dto) {
        String roleControl = jwtTokenManager.getRoleFromToken(dto.getToken())
                .orElseThrow(() -> new ProductServiceException(ErrorType.TOKEN_ARGUMENT_NOTVALID));

        if (!roleControl.equals(ERole.USER.name())) {
            throw new ProductServiceException(ErrorType.ROLE_CONTROL_FAILED);
        }

        Long authId = jwtTokenManager.getIdFromToken(dto.getToken())
                .orElseThrow(() -> new ProductServiceException(ErrorType.TOKEN_ARGUMENT_NOTVALID));

        Optional<ShoppingCart> cartOpt = shoppingCartRepository.findByAuthId(authId);

        if (cartOpt.isPresent()) {
            ShoppingCart shoppingCart = cartOpt.get();
            Optional<CartItem> cartItemOpt = shoppingCart.getItems().stream()
                    .filter(item -> item.getProduct().getId().equals(dto.getProductId()))
                    .findFirst();

            if (cartItemOpt.isPresent()) {
                CartItem cartItem = cartItemOpt.get();
                // Item'ları çıkar
                shoppingCart.getItems().remove(cartItem);

                // Total price'ı güncelle
                double itemTotalPrice = 0.0;
                double productTotalPrice = cartItem.getProduct().getPrice() * cartItem.getQuantity();
                for (Items item : cartItem.getItems()) {
                    itemTotalPrice += item.getPrice();
                }
                double cartTotalPrice = shoppingCart.getTotalPrice() - itemTotalPrice - productTotalPrice;
                shoppingCart.setTotalPrice(cartTotalPrice);

                // Eğer cart boşaldıysa, veritabanından da sil
                if (shoppingCart.getItems().isEmpty()) {
                    shoppingCartRepository.delete(shoppingCart);
                } else {
                    shoppingCartRepository.save(shoppingCart);
                }

                return shoppingCart;
            }
        }
        throw new ProductServiceException(ErrorType.CART_OR_ITEM_NOT_FOUND);
    }

    public ShoppingCart viewCart(String token) {
        Long authId = jwtTokenManager.getIdFromToken(token).orElseThrow(() -> new ProductServiceException(ErrorType.TOKEN_ARGUMENT_NOTVALID));
        ShoppingCart shoppingCart = shoppingCartRepository.findByAuthId(authId).orElseThrow(() -> new ProductServiceException(ErrorType.CART_NOT_FOUND));

        List<Product> productList = shoppingCart.getProductList();
        List<Items> itemList = shoppingCart.getItemList();

        return shoppingCart;
    }

    public void checkout(Long cartId) {
        ShoppingCart cart = shoppingCartRepository.findById(cartId)
                .orElseThrow(() -> new ProductServiceException(ErrorType.CART_NOT_FOUND));

        Long authId = cart.getAuthId();

        ShoppingCart shoppingCart = shoppingCartRepository.findById(cartId).orElseThrow(() -> new ProductServiceException(ErrorType.CART_NOT_FOUND));

        OrderMessageDto orderMessage = new OrderMessageDto();
        orderMessage.setCartId(cartId);
        orderMessage.setAuthId(authId);
        orderMessage.setTotalPrice(shoppingCart.getTotalPrice());

        try {
            rabbitTemplate.convertAndSend("exchange.burgerrest", "routing.key.order", orderMessage);

            cart.setTotalPrice(0.0);
            shoppingCartRepository.save(cart);
        } catch (AmqpException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send message to RabbitMQ", e);
        }
    }



}
