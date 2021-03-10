package com.vqd.ped.pointofsalesservice.controller;

import com.vqd.ped.pointofsalesservice.service.PointOfSalesService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/card-service")
@RequiredArgsConstructor
public class CardServiceController {

    private final PointOfSalesService service;

    @PostMapping("/start")
    public ResponseEntity<Void> sendRequest(@RequestBody StartServiceRequest body) {
        doSendRequest(body.requestType);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private boolean doSendRequest(RequestType requestType) {
        switch (requestType) {
            case CARD_TRANSACTION:
                return service.sendCardRequest();
            case SERVICE_TRANSACTION:
                return service.sendServiceRequest();
            default:
                throw new IllegalArgumentException("Unknown request type");
        }
    }

    private enum RequestType {
        CARD_TRANSACTION,
        SERVICE_TRANSACTION
    }

    @Data
    private static class StartServiceRequest {
        private RequestType requestType;
    }
}
