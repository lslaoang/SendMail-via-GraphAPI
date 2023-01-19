package com.lao.mailgraph.service;

import com.azure.spring.aad.AADOAuth2AuthenticatedPrincipal;
import com.lao.mailgraph.model.MailBody;
import com.lao.mailgraph.model.feedback.BodyMessage;
import com.lao.mailgraph.model.feedback.EmailRecipient;
import com.lao.mailgraph.model.feedback.FeedBackModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.logging.Logger;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;

@Service
public class GraphServiceImpl implements GraphService {

    private static final String CONTENT_TYPE_TEXT = "Text";
    private static final Logger LOGGER = Logger.getLogger(GraphService.class.getName());

    private final WebClient webClient;

    @Value(("${app.mailbox}"))
    private String feedBackMailBox;

    public GraphServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public void verifyRights() throws URISyntaxException {
        String response;
        try {
            response = webClient
                    .get()
                    .uri(new URI("https://graph.microsoft.com/v1.0/me"))
                    .attributes(clientRegistrationId("graph"))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new RuntimeException("Expected value is missing.");
        }

        if (response == null) {
            throw new RuntimeException("Expected value is missing.");
        }
        LOGGER.info("Authorization check passed!");
    }

    @Override
    public void sendMail(MailBody mailBody) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AADOAuth2AuthenticatedPrincipal user = (AADOAuth2AuthenticatedPrincipal) principal;
        String userEmail = user.getAttribute("preferred_username");

        try {
            Mono<String> apiResponse = webClient.post()
                    .uri(new URI("https://graph.microsoft.com/v1.0/users/"+userEmail+"/sendMail"))
                    .attributes(clientRegistrationId("graph"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(prepareMessageBody(mailBody))
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnError(error -> {
                        error.printStackTrace();
                        System.out.println("This is the error: " + error.getMessage());
                    })
                    ;
            apiResponse.subscribe(res -> System.out.println("Response: " + res));

            System.out.println("Email sent successfully.");
        } catch (WebClientResponseException ex) {
            System.out.println("Error sending email: " + ex.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException("Error sending email. " + e.getMessage());
        }
    }

    private Object prepareMessageBody(MailBody mailBody) {
        LOGGER.info("Preparing email body ...");
        return FeedBackModel.builder()
                .saveToSentItems(false)
                .message(FeedBackModel.Message.builder()
                        .subject(mailBody.getSubject())
                        .body(BodyMessage.builder()
                                .contentType(CONTENT_TYPE_TEXT)
                                .content(mailBody.getMessage())
                                .build())
                        .toRecipients(Collections.singletonList(EmailRecipient.builder()
                                .emailAddress(EmailRecipient.MailBoxAddress.builder()
                                        .address(feedBackMailBox)
                                        .build())
                                .build()))
                        .build())
                .build();
    }
}
