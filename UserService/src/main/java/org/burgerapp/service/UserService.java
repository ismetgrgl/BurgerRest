package org.burgerapp.service;

import lombok.RequiredArgsConstructor;

import org.apache.hc.core5.http.Message;
import org.burgerapp.dto.request.OrderMessageDto;
import org.burgerapp.dto.request.UserSaveRequestDto;
import org.burgerapp.dto.request.UserUpdateRequestDto;
import org.burgerapp.entity.EStatus;
import org.burgerapp.entity.UserProfile;
import org.burgerapp.exception.ErrorType;
import org.burgerapp.exception.UserServiceException;
import org.burgerapp.mapper.UserMapper;
import org.burgerapp.repository.UserRepository;
import org.burgerapp.utility.JwtTokenManager;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
private final UserRepository userRepository;
private final JwtTokenManager jwtTokenManager;
private final RabbitTemplate rabbitTemplate;

    public Boolean save(UserSaveRequestDto dto) {
        UserProfile save = userRepository.save(UserMapper.INSTANCE.toUserProfile(dto));
        if (save == null)
            throw new UserServiceException(ErrorType.INTERNAL_SERVER_ERROR);
        return true;
    }
    @RabbitListener(queues = "queue.burgerrest.auth")
    public Boolean saveWithRabbit(UserSaveRequestDto dto) {
        UserProfile save = userRepository.save(UserMapper.INSTANCE.toUserProfile(dto));
        if (save == null)
            throw new UserServiceException(ErrorType.INTERNAL_SERVER_ERROR);
        return true;
    }

    public void updateStatus(Long authId) {
        UserProfile foundUserProfile = userRepository.findByAuthId(authId)
                .orElseThrow(() -> new UserServiceException(ErrorType.USER_NOT_FOUND));
        foundUserProfile.setStatus(EStatus.ACTIVE);
        UserProfile savedUserProfile = userRepository.save(foundUserProfile);
        if (!savedUserProfile.getStatus().equals(EStatus.ACTIVE)) {
            throw new UserServiceException(ErrorType.USERSERVICE_UPDATE_STATUS_FAILED);
        }
    }

    public void updateUserProfile(UserUpdateRequestDto dto) {
        Long authId =
                jwtTokenManager.getIdFromToken(dto.getToken())
                        .orElseThrow(() -> new UserServiceException(ErrorType.USER_NOT_FOUND));

        UserProfile userProfile = userRepository.findByAuthId(authId)
                .orElseThrow(() -> new UserServiceException(ErrorType.USER_NOT_FOUND));

        if (dto.getPhone() != null) {
            userProfile.setPhone(dto.getPhone());
        }
        if (dto.getAddress() != null) {
            userProfile.setAddress(dto.getAddress());
        }
        if (dto.getName() != null) {
            userProfile.setName(dto.getName());
        }
        if (dto.getSurname() != null) {
            userProfile.setSurname(dto.getSurname());
        }
        if (dto.getBalance()!= null) {
            userProfile.setBalance(dto.getBalance());
        }

        userRepository.save(userProfile);

    }

    @RabbitListener(queues = "queue.burgerrest.order")
    public void handleOrderMessage(OrderMessageDto orderMessageDto, Message message) {
        Long authId = orderMessageDto.getAuthId();
        Double totalPrice = orderMessageDto.getTotalPrice();

        UserProfile user = userRepository.findByAuthId(authId)
                .orElseThrow(() -> new UserServiceException(ErrorType.USER_NOT_FOUND));

        if (user.getBalance() < totalPrice) {
            // Kullanıcının bakiyesi yetersiz ise istisna fırlat
            throw new UserServiceException(ErrorType.BAD_REQUEST_ERROR);
        }

        try {
            // Balance yeterliyse, bakiyeyi düş ve siparişi işle
            user.setBalance(user.getBalance() - totalPrice);
            userRepository.save(user);

            // Order bilgilerini sipariş servisine ilet
            OrderMessageDto newOrderMessage = new OrderMessageDto();
            newOrderMessage.setCartId(orderMessageDto.getCartId());
            newOrderMessage.setAuthId(authId);
            newOrderMessage.setTotalPrice(0.0);

            rabbitTemplate.convertAndSend("exchange.burgerrest", "routing.key.order.payment", newOrderMessage);
        } catch (Exception e) {
            // İşlem sırasında bir hata oluşursa, mesajı yeniden kuyruğa gönder
            // Bu şekilde RabbitMQ, mesajı tekrar işlemeye çalışabilir
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }
}
