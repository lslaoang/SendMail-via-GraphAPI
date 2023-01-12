package com.lao.mailgraph.service;

import com.lao.mailgraph.model.MailBody;
import com.lao.mailgraph.model.feedback.BodyMessage;
import com.lao.mailgraph.model.feedback.EmailRecipient;
import com.lao.mailgraph.model.feedback.FeedBackModel;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Objects;
import java.util.logging.Logger;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;

@Service
public class GraphServiceImpl implements GraphService {

    private final String feedBackMailBox = "neo.sophos.dev@gmail.com";
    private final Logger LOGGER = Logger.getLogger(GraphService.class.getName());
    private final WebClient webClient;
    private OkHttpClient okHttpClient = new OkHttpClient();

    @Value("${azure.activedirectory.tenant-id}")
    private String tenantId;
    @Value("${azure.activedirectory.client-id}")
    private String clientId;
    @Value("${azure.activedirectory.client-secret}")
    private String clientSecret;

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


        Response response = null;
        try {
            String ACCESS_TOKEN = getAccessToken();
            LOGGER.warning(ACCESS_TOKEN);
            response = webClient
                    .post()
                    .uri(new URI("https://graph.microsoft.com/v1.0/me/sendmail"))
                    //TODO: Investigate the problem with token exchange
//                    .attributes(clientRegistrationId("graph"))
                    .header("Authorization", "Bearer " + ACCESS_TOKEN)
                    .bodyValue(prepareMessageBody(mailBody))
                    .retrieve()
                    .bodyToMono(Response.class)
                    .block();
        } catch (Exception e) {
            LOGGER.info(Objects.requireNonNull(response.body()).toString());
            e.printStackTrace();
            throw new RuntimeException("Expected value is missing.");
        }

        if (response == null) {
            throw new RuntimeException("Expected value is missing.");
        }

    }


    public void sendMailviaOkHttp(MailBody mailBody) {

        Response response = null;
        try {
            String ACCESS_TOKEN = getAccessToken();
            LOGGER.warning(ACCESS_TOKEN);

            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody body = RequestBody.create(prepareMessageBody(mailBody));
            Request request = new Request.Builder()
                    .url("https://graph.microsoft.com/v1.0/me/sendmail")
                    .post(body)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();
            response = okHttpClient
                    .post()
                    .uri(new URI("https://graph.microsoft.com/v1.0/me/sendmail"))
                    //TODO: Investigate the problem with token exchange
//                    .attributes(clientRegistrationId("graph"))
                    .header("Authorization", "Bearer " + ACCESS_TOKEN)
                    .bodyValue(prepareMessageBody(mailBody))
                    .retrieve()
                    .bodyToMono(Response.class)
                    .block();
        } catch (Exception e) {
            LOGGER.info(Objects.requireNonNull(response.body()).toString());
            e.printStackTrace();
            throw new RuntimeException("Expected value is missing.");
        }

        if (response == null) {
            throw new RuntimeException("Expected value is missing.");
        }

    }

    private Object prepareMessageBody(MailBody mailBody) {
        LOGGER.info("Preparing email body ...");
        return FeedBackModel.builder()
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
    }

    private String getAccessToken() throws IOException, JSONException {
        LOGGER.info("Fetching access token ...");

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret + "&scope=https%3A%2F%2Fgraph.microsoft.com%2F.default");
        Request request = new Request.Builder()
                .url("https://login.microsoftonline.com/" + tenantId + "/oauth2/v2.0/token")
                .post(body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        Response response = okHttpClient.newCall(request).execute();

        String json = Objects.requireNonNull(response.body()).string();
        JSONObject jsonObject = new JSONObject(json);
        LOGGER.info("Token successfully generated!");
        return jsonObject.getString("access_token");

    }
}
