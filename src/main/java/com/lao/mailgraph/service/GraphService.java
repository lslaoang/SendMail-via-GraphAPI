package com.lao.mailgraph.service;


import com.lao.mailgraph.model.MailBody;

import java.net.URISyntaxException;

public interface GraphService {
    void verifyRights() throws URISyntaxException;
    void sendMail(MailBody mailBody) throws URISyntaxException;
}
