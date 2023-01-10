package com.lao.mailgraph.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

@Component
public class IntuneAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger LOGGER = Logger.getLogger(IntuneAuthenticationEntryPoint.class.getName());

    @ExceptionHandler(OAuth2AuthenticationException.class)
    public ResponseEntity<Object> handleInvalidAuth() {
        LOGGER.severe("Failed to authenticate request.");
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) {

        LOGGER.severe(request.getHeader("Authorization"));
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        LOGGER.severe(String.format("Access from %s denied. ", request.getRemoteAddr()) + authException.getMessage());
    }
}
