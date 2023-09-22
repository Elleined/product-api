package com.elleined.marketplaceapi.service.message;

import com.elleined.marketplaceapi.dto.Message;
import com.elleined.marketplaceapi.dto.PrivateMessage;
import com.elleined.marketplaceapi.exception.field.NotValidBodyException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.user.NoLoggedInUserException;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.user.UserService;
import com.elleined.marketplaceapi.utils.StringUtil;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final UserService userService;

    @Autowired
    private HttpSession session;

    @Override
    public PrivateMessage sendPrivateMessage(int recipientId, String message)
            throws NoLoggedInUserException, NotValidBodyException, ResourceNotFoundException {

        if (!userService.existsById(recipientId)) throw new ResourceNotFoundException("Recipient does not exists!");
        if (StringUtil.isNotValid(message)) throw new NotValidBodyException("Body cannot be null, empty, or blank");

        // alias for currentUser
        User sender = (User) session.getAttribute("currentUser");
        if (sender == null) throw new NoLoggedInUserException("Please login first before sending private message. Thank you very much...");

        PrivateMessage responseMessage = PrivateMessage.builder()
                .message(HtmlUtils.htmlEscape(message))
                .recipientId(recipientId)
                .senderId(sender.getId())
                .build();

        simpMessagingTemplate.convertAndSendToUser(String.valueOf(recipientId),"/private-chat", responseMessage);

        log.debug("Sender with id of {} send a message in recipient with id of {}", sender.getId(), recipientId);
        return responseMessage;
    }

    @Override
    public Message sendPublicMessage(String message)
            throws NotValidBodyException, NoLoggedInUserException {

        if (StringUtil.isNotValid(message)) throw new NotValidBodyException("Body cannot be null, empty, or blank");

        // alias for currentUser
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) throw new NoLoggedInUserException("Please login first before sending private message. Thank you very much...");

        Message responseMessage = new Message(HtmlUtils.htmlEscape(message), currentUser.getId());
        simpMessagingTemplate.convertAndSend("/public-chat/topic", responseMessage);
        return responseMessage;
    }
}
