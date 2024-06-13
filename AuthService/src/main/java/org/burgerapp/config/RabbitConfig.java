package org.burgerapp.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    String exchange = "exchange.direct";
    String registerQueueName = "queue.burgerrest.auth";
    String registerBindingKey = "update.user.key";

    String activeStatusQueueName = "activestatusqueue.burgerrest.auth";
    String activeStatusBindingKey = "activestatus.user.key";

    String activationCodeQueueName = "activationcodequeue.burgerrest.auth";
    String activationCodeBindingKey = "activationCode.email.key";

    String resetPasswordQueueName = "resetpasswordqueue.burgerrest.auth";
    String resetPasswordBindingKey = "resetpassword.email.key";

    @Bean
    DirectExchange exchangeDirect() {
        return new DirectExchange(exchange);
    }

    // Register:
    @Bean
    Queue registerQueue() {
        return new Queue(registerQueueName);
    }

    @Bean
    Binding bindingRegister(Queue registerQueue, DirectExchange exchangeDirect) {
        return BindingBuilder.bind(registerQueue).to(exchangeDirect).with(registerBindingKey);
    }

    // ActivateStatus:
    @Bean
    Queue activateStatusQueue() {
        return new Queue(activeStatusQueueName);
    }

    @Bean
    Binding bindingActivateStatus(Queue activateStatusQueue, DirectExchange exchangeDirect) {
        return BindingBuilder.bind(activateStatusQueue).to(exchangeDirect).with(activeStatusBindingKey);
    }

    // ActivationCode
    @Bean
    Queue activationCodeQueue() {
        return new Queue(activationCodeQueueName);
    }

    @Bean
    Binding bindingActivationCode(Queue activationCodeQueue, DirectExchange exchangeDirect) {
        return BindingBuilder.bind(activationCodeQueue).to(exchangeDirect).with(activationCodeBindingKey);
    }

    // ResetPassword
    @Bean
    Queue resetPasswordQueue() {
        return new Queue(resetPasswordQueueName);
    }

    @Bean
    Binding bindingResetPassword(Queue resetPasswordQueue, DirectExchange exchangeDirect) {
        return BindingBuilder.bind(resetPasswordQueue).to(exchangeDirect).with(resetPasswordBindingKey);
    }


    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}
