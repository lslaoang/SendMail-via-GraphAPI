package com.lao.mailgraph.service;

import com.lao.mailgraph.model.MailBody;
import com.lao.mailgraph.model.feedback.BodyMessage;
import com.lao.mailgraph.model.feedback.EmailRecipient;
import com.lao.mailgraph.model.feedback.FeedBackModel;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;

@Service
public class GraphServiceImpl implements GraphService {

    private final String feedBackMailBox = "neo.sophos.dev@gmail.com";


    private final WebClient webClient;

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
        }catch (RuntimeException e){
            e.printStackTrace();
            throw new RuntimeException("Expected value is missing.");
        }

        if(response == null){
            throw new RuntimeException("Expected value is missing.");
        }

    }

    @Override
    public void sendMail(MailBody mailBody) throws URISyntaxException {

        String response;
        try {
            response = webClient
                    .post()
                    .uri(new URI("https://graph.microsoft.com/v1.0/me/sendMail"))
                    .attributes(clientRegistrationId("graph"))
                    .bodyValue(prepareMessageBody(mailBody))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Expected value is missing.");
        }

        if(response == null){
            throw new RuntimeException("Expected value is missing.");
        }

    }

    private Object prepareMessageBody(MailBody mailBody){
        FeedBackModel messageBody =  FeedBackModel.builder()
                .saveToSentItems(false)
                .message(FeedBackModel.Message.builder()
                        .subject(mailBody.getSubject())
                        .body(BodyMessage.builder()
                                .contentType("Text")
                                .content(mailBody.getMessage())
                                .build())
                        .toRecipients(Collections.singletonList(EmailRecipient.builder()
                                .emailAddress(EmailRecipient.MailBoxAddress.builder()
                                        .address(feedBackMailBox)
                                        .build())
                                .build()))
                        .build())
                .build();
        return messageBody;
    }
}
