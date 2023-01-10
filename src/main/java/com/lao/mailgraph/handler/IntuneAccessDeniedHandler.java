package com.lao.mailgraph.handler;

import com.testco.iw.models.AccessDenied;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

import static com.testco.intunewebapp.util.ResponseWriterUtil.writeErrorResponse;

@Component
public class IntuneAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger LOGGER = Logger.getLogger(IntuneAccessDeniedHandler.class.getName());

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) {

        LOGGER.severe(request.getHeader("Authorization"));
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        writeErrorResponse(response, new AccessDenied());
        LOGGER.severe(String.format("Access from %s denied. ", request.getRemoteAddr()) + accessDeniedException.getMessage());

    }
}
