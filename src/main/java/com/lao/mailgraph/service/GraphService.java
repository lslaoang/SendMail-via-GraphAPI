package com.lao.mailgraph.service;


import com.lao.mailgraph.model.MailBody;

public interface GraphService {
    void verifyRights();
    void sendMail(MailBody mailBody);
}
