package com.elleined.marketplaceapi.service.message;

import com.elleined.marketplaceapi.dto.Message;
import com.elleined.marketplaceapi.exception.NoLoggedInUserException;
import com.elleined.marketplaceapi.exception.NotValidBodyException;
import com.elleined.marketplaceapi.exception.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.user.PrincipalService;
import com.elleined.marketplaceapi.service.user.UserService;
import com.elleined.marketplaceapi.utils.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

@Slf4j
@Service
@RequiredArgsConstructor
@Primary
public class PrivateMessageService implements MessageService {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final PrincipalService principalService;
    private final UserService userService;

    @Override
    public Message sendPrivateMessage(int recipientId, String message)
            throws NoLoggedInUserException, NotValidBodyException, ResourceNotFoundException {

        if (!userService.existsById(recipientId)) throw new ResourceNotFoundException("Recipient with id of " + recipientId +  " does not exists!");
        if (StringUtil.isNotValid(message)) throw new NotValidBodyException("Body cannot be null, empty, or blank");
        if (principalService.getPrincipal() == null) throw new NoLoggedInUserException("Please login first before sending private message. Thank you very much...");

        User sender = principalService.getPrincipal();
        Message responseMessage = Message.builder()
                .message(HtmlUtils.htmlEscape(message))
                .recipientId(recipientId)
                .senderId(sender.getId())
                .build();
        simpMessagingTemplate.convertAndSendToUser(String.valueOf(recipientId), "/private-chat", responseMessage);
        return responseMessage;
    }
}
