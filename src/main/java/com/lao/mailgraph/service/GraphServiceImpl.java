package com.lao.mailgraph.service;

import com.lao.mailgraph.model.MailBody;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.net.URISyntaxException;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;

@Service
public class GraphServiceImpl implements GraphService {

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
    public void sendMail(MailBody mailBody) {

    }
}
