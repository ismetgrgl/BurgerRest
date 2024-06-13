package org.burgerapp.rabbitmq.consumer;

import lombok.RequiredArgsConstructor;
import org.burgerapp.rabbitmq.model.MailModel;
import org.burgerapp.service.MailSenderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailConsumer {
    private final MailSenderService mailSenderService;

    @RabbitListener(queues = "activationcodequeue.burgerrest.auth")
    public void sendActivationCodeMail(MailModel mailModel){
        mailSenderService.sendMail(mailModel);
    }
}
