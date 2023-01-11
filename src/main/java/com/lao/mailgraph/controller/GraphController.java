package com.lao.mailgraph.controller;

import com.lao.mailgraph.model.MailBody;
import com.lao.mailgraph.service.GraphService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class GraphController {

    private final GraphService graphService;

    public GraphController(GraphService graphService) {
        this.graphService = graphService;
    }

    @GetMapping("/verify")
    public ResponseEntity verifyUser() {
        try {
            graphService.verifyRights();
            return new ResponseEntity("Status verified!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity("Failed to verify status. " + e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/send-mail")
    public ResponseEntity sendEmail(@RequestBody MailBody mailBody) {
        try {
            graphService.sendMail(mailBody);
            return new ResponseEntity("Email sent!", HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity("Email sending failed. " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
