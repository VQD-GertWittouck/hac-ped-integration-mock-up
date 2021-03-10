package com.vqd.ped.electronicpaymentservice.service;

import com.vqd.ped.electronicpaymentservice.model.ped.cardRequest.CardServiceRequest;
import com.vqd.ped.electronicpaymentservice.model.ped.cardResponse.CardRequestType;
import com.vqd.ped.electronicpaymentservice.model.ped.cardResponse.CardServiceResponse;
import com.vqd.ped.electronicpaymentservice.model.ped.serviceRequest.ServiceRequest;
import com.vqd.ped.electronicpaymentservice.model.ped.serviceResponse.ServiceRequestType;
import com.vqd.ped.electronicpaymentservice.model.ped.serviceResponse.ServiceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ElectronicPaymentService {

    private final RestTemplate restTemplate = new RestTemplate();

    public void createDelayedCardServiceResponse(CardServiceRequest cardServiceRequest) {
        CardServiceResponse cardServiceResponse = new CardServiceResponse();
        cardServiceResponse.setRequestID(cardServiceRequest.getRequestID());
        cardServiceResponse.setWorkstationID(cardServiceRequest.getWorkstationID());
        cardServiceResponse.setRequestType(CardRequestType.fromValue(cardServiceRequest.getRequestType().value()));

        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8911/channel/pos", cardServiceResponse, String.class);
        log.info("Card service response send to POS, Status: {}", response.getStatusCode());
    }

    public void createDelayedServiceResponse(ServiceRequest serviceRequest) {
        ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setRequestID(serviceRequest.getRequestID());
        serviceResponse.setWorkstationID(serviceRequest.getWorkstationID());
        serviceResponse.setRequestType(ServiceRequestType.fromValue(serviceRequest.getRequestType().value()));

        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8911/channel/pos", serviceResponse, String.class);
        log.info("Service response send to POS, Status: {}", response.getStatusCode());
    }
}
