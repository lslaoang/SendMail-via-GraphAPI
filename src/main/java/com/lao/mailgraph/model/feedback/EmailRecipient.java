package com.lao.mailgraph.model.feedback;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EmailRecipient {
    MailBoxAddress emailAddress;

    @Getter
    @Setter
    @Builder
    public static class MailBoxAddress {
        String address;
    }
}