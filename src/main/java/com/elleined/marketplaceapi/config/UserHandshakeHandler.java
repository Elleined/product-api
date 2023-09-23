package com.elleined.marketplaceapi.config;

import com.elleined.marketplaceapi.model.user.User;
import com.sun.security.auth.UserPrincipal;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserHandshakeHandler extends DefaultHandshakeHandler {
    @Override
     protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        final String username = UUID.randomUUID().toString();
        log.debug("User with generated username of {} connected to private chat", username);
        return new StompPrincipal(username);
    }
}