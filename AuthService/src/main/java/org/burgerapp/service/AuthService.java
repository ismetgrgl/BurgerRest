package org.burgerapp.service;

import lombok.RequiredArgsConstructor;
import org.burgerapp.dto.request.ActivateCodeRequestDto;
import org.burgerapp.dto.request.LoginRequestDto;
import org.burgerapp.dto.request.RegisterRequestDto;
import org.burgerapp.dto.request.UpdatePasswordRequestDto;
import org.burgerapp.dto.response.RegisterResponseDto;
import org.burgerapp.entity.Auth;

import org.burgerapp.entity.enums.EStatus;
import org.burgerapp.exception.AuthServiceException;
import org.burgerapp.exception.ErrorType;
import org.burgerapp.mapper.AuthMapper;
import org.burgerapp.rabbitmq.model.MailModel;
import org.burgerapp.rabbitmq.producer.ActivateStatusProducer;
import org.burgerapp.rabbitmq.producer.MailProducer;
import org.burgerapp.repository.AuthRepository;
import org.burgerapp.utility.CodeGenerator;
import org.burgerapp.utility.JwtTokenManager;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final MailProducer mailProducer;
    private final RabbitTemplate rabbitTemplate;
    private final JwtTokenManager jwtTokenManager;
    private final ActivateStatusProducer activateStatusProducer;

    @Transactional
    public RegisterResponseDto registerWithRabbit(RegisterRequestDto dto) {
        //password ve repassword eşitliği kontrol edilir:
        if (!dto.getPassword().equals(dto.getRepassword())) {
            throw new AuthServiceException(ErrorType.PASSWORDS_NOT_MATCHED);
        }
        //username daha önce alınmış mı kontrol edilir:
        if (authRepository.existsByUsername(dto.getUsername())) {
            throw new AuthServiceException(ErrorType.USERNAME_ALREADY_TAKEN);
        }
        //Kontrollerden başarılı bir şekilde geçildiyse dto'dan gelen bilgilerle Auth nesnesi oluşturulur.
        Auth auth = AuthMapper.INSTANCE.registerRequestDtoToAuth(dto);

        // bu kısım ile activasyonu kodu auth nesnesine aktarılıyor.
        auth.setCode(CodeGenerator.generateActivationCode());
        // burada email ile activasyon kodu gönderebiliriz:
        // aslında activasyon codunu rabbitmqya göndereceğiz.
        mailProducer.convertAndSendToRabbit(MailModel.builder()
                .email(auth.getEmail())
                .code(auth.getCode())
                .build());
        System.out.println(auth.getCode());
        //Bu auth nesnesi repository save metodu ile kaydedilir.
        authRepository.save(auth);
        //Rabbit ile microservice arası iletişim:
        Boolean gonderildiMi = (Boolean) rabbitTemplate.convertSendAndReceive("exchange.direct",
                "update.user.key",
                AuthMapper.INSTANCE.toUserSaveRequestDto(auth));
        System.out.println(gonderildiMi);
        return AuthMapper.INSTANCE.authToDto(auth);
    }

    public String doLogin(LoginRequestDto dto) {
        Auth auth = authRepository.findOptionalByEmailAndPassword(dto.getEmail(),
                        dto.getPassword())
                .orElseThrow(() -> new AuthServiceException(ErrorType.EMAIL_OR_PASSWORD_WRONG));

        if (!auth.getEStatus().equals(EStatus.ACTIVE)) {
            throw new AuthServiceException(ErrorType.ACCOUNT_NOT_ACTIVE);
        }

        return jwtTokenManager.createToken(auth.getId(),
                        auth.getERole())
                .orElseThrow(() -> new AuthServiceException(ErrorType.TOKEN_CREATION_FAILED));

    }

    public String activateCode(ActivateCodeRequestDto dto) {
        Auth auth = authRepository.findById(dto.getId())
                .orElseThrow(() -> new AuthServiceException(ErrorType.USER_NOT_FOUND));
        if (!auth.getCode().equals(dto.getActivationCode())) {
            throw new AuthServiceException(ErrorType.ACTIVATION_CODE_MISMATCH);
        }

        return statusControl(auth);
    }

    @Transactional
    public String statusControl(Auth auth) {
        switch (auth.getEStatus()) {
            case ACTIVE -> throw new AuthServiceException(ErrorType.ACCOUNT_ALREADY_ACTIVE);
            case PENDING -> {
                auth.setEStatus(EStatus.ACTIVE);
                authRepository.save(auth);
                //OpenFeign ile
                //userManager.updateUserStatus(auth.getId());
                //Rabbit ile:
                activateStatusProducer.convertAndSend(auth.getId());
                return "Aktivasyon Başarılı! Sisteme giriş yapabilirsiniz.";
            }
            case BANNED -> throw new AuthServiceException(ErrorType.ACCOUNT_BANNED);
            case DELETED -> throw new AuthServiceException(ErrorType.ACCOUNT_DELETED);
            default -> throw new AuthServiceException(ErrorType.INTERNAL_SERVER_ERROR);
        }
    }

    public String sifremiUnuttum(String email) {
        //banlanmış veya delete edilmiş birisi şifre yenileyebilir mi?
        MailModel mailModel = MailModel.builder().code(CodeGenerator.generateResetPasswordCode())
                .email(email).build();
        rabbitTemplate.convertAndSend("exchange.direct",
                "resetpassword.email.key",
                mailModel);

        Auth auth =
                authRepository.findByEmail(email).orElseThrow(() -> new AuthServiceException(ErrorType.USER_NOT_FOUND));
        auth.setCode(mailModel.getCode());
        authRepository.save(auth);
        return "Şifreme yenileme kodunuz " + email + " adresine gönderildi.";
    }

    public String updatePassword(UpdatePasswordRequestDto dto) {
        Auth auth = authRepository.findByEmailAndCode(dto.getEmail(),
                        dto.getPasswordResetCode())
                .orElseThrow(() -> new AuthServiceException(ErrorType.USER_NOT_FOUND));

        auth.setPassword(dto.getNewPassword());
        authRepository.save(auth);
        return "Şifreniz başarı ile değiştirildi.";
    }


}
