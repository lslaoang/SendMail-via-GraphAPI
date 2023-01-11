package com.lao.mailgraph.model.feedback;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class FeedBackModel {
    Message message;
    Boolean saveToSentItems;

    @Getter
    @Setter
    @Builder
    public static class Message {
        String subject;
        BodyMessage body;
        List<EmailRecipient> toRecipients;
    }

}

