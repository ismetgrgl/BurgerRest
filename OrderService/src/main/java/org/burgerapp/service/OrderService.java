package org.burgerapp.service;

import lombok.RequiredArgsConstructor;
import org.burgerapp.dto.request.OrderMessageDto;
import org.burgerapp.entity.Order;
import org.burgerapp.exception.ErrorType;
import org.burgerapp.exception.OrderServiceException;
import org.burgerapp.repository.OrderRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;



    @RabbitListener(queues = "queue.burgerrest.order.payment")
    public void handleOrderPaymentMessage(OrderMessageDto orderMessageDto) {
        Order order = new Order();
        order.setCartId(orderMessageDto.getCartId());
        order.setAuthId(orderMessageDto.getAuthId());  // Kullanıcı kimliği olarak authId'yi kullanıyoruz
        order.setTotalPrice(orderMessageDto.getTotalPrice());
        order.setDescription("Order created");

        orderRepository.save(order);

    }

    public Order findById(Long id) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isEmpty()) {
            throw new OrderServiceException(ErrorType.USER_NOT_FOUND);
        }

        return order.get();
    }
}
