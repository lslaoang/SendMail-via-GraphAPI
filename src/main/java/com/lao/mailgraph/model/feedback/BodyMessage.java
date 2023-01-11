package com.lao.mailgraph.model.feedback;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BodyMessage {
    String contentType;
    String content;
}
