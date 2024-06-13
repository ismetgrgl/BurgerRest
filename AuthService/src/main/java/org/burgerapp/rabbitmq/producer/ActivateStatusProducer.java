package org.burgerapp.rabbitmq.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivateStatusProducer {
    private final RabbitTemplate rabbitTemplate;
    public void convertAndSend(Long authId){
        rabbitTemplate.convertAndSend("exchange.direct","activestatus.user.key", authId);
    }
}
