package org.burgerapp.service;

import org.burgerapp.rabbitmq.model.MailModel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class MailSenderService {
    private final JavaMailSender javaMailSender;

    public MailSenderService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendMail(MailModel mailModel){

        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();

        simpleMailMessage.setTo(mailModel.getEmail());
        simpleMailMessage.setSubject("BurgerRest Aktivasyon İşlemleri");
        simpleMailMessage.setText("Aktivasyon Kodu: "+mailModel.getCode());
        simpleMailMessage.setCc("berkaybilgeadam0@gmail.com");


        javaMailSender.send(simpleMailMessage);
    }
/*
    @RabbitListener(queues = "resetpasswordqueue.socialmedia.auth")
    public void sendPasswordMail(MailModel mailModel){
        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();

        simpleMailMessage.setTo(mailModel.getEmail());
        simpleMailMessage.setSubject("Social Media Password Reset");
        simpleMailMessage.setText("Şifre yenileme için Aktivasyon Kodu: "+mailModel.getCode());
        simpleMailMessage.setCc("ismetmustafa068@gmailcom");


        javaMailSender.send(simpleMailMessage);
    }

    */
}
