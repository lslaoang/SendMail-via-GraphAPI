package com.lao.mailgraph.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/")
public class GraphController {

    @GetMapping("/verify")
    public ResponseEntity verifyUser(){
        return new ResponseEntity(HttpStatus.OK);
    }
}
