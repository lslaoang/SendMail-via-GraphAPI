package com.lao.mailgraph.controller;

import com.lao.mailgraph.service.GraphService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            return new ResponseEntity("Failed to verify status. " +e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }
}
