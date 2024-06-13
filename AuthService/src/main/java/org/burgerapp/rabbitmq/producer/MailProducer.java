package org.burgerapp.rabbitmq.producer;

import lombok.RequiredArgsConstructor;
import org.burgerapp.rabbitmq.model.MailModel;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailProducer {
    private final RabbitTemplate rabbitTemplate;

    public void convertAndSendToRabbit(MailModel model) {
        rabbitTemplate.convertAndSend("exchange.direct",
                "activationCode.email.key",
                model);
    }
}
