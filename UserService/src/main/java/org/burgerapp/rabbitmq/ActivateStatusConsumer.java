package org.burgerapp.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.burgerapp.service.UserService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivateStatusConsumer {
    private final UserService userService;

    @RabbitListener(queues = "activestatusqueue.burgerrest.auth")
    public void activateStatus(Long authId){
        userService.updateStatus(authId);
    }
}