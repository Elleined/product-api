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

@Slf4j
@RequiredArgsConstructor
@Component
public class UserHandshakeHandler extends DefaultHandshakeHandler {

    @Autowired
    private HttpSession session;
    @Override
     protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        
        User currentUser = (User) session.getAttribute("currentUser");
        log.debug("User with id of {} connected to the private chat", currentUser.getId());
        return new UserPrincipal(String.valueOf(currentUser.getId()));
    }
}