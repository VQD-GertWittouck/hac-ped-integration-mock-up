package com.vqd.ped.pointofsalesservice.service;

import com.vqd.ped.electronicpaymentservice.model.ped.cardRequest.CardRequestType;
import com.vqd.ped.electronicpaymentservice.model.ped.cardRequest.CardServiceRequest;
import com.vqd.ped.electronicpaymentservice.model.ped.serviceRequest.ServiceRequest;
import com.vqd.ped.electronicpaymentservice.model.ped.serviceRequest.ServiceRequestType;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.xml.datatype.DatatypeFactory;
import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.Optional.of;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.APPLICATION_XML;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointOfSalesService {

    private final RestTemplate rest = new RestTemplate();

    @SneakyThrows
    public boolean sendCardRequest() {
        CardServiceRequest cardServiceRequest = new CardServiceRequest();
        cardServiceRequest.setRequestID(UUID.randomUUID().toString());
        cardServiceRequest.setWorkstationID("localhost:8911");
        cardServiceRequest.setRequestType(CardRequestType.CARD_PAYMENT);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(APPLICATION_XML);
        HttpEntity<CardServiceRequest> requestEntity = new HttpEntity<>(cardServiceRequest, httpHeaders);

        ResponseEntity<?> response = rest.postForEntity("http://localhost:8910/channel/pos", requestEntity, ResponseEntity.class);
        log.info("Card service request send out with response status {}", of(response).map(ResponseEntity::getStatusCode).orElse(INTERNAL_SERVER_ERROR));
        return true;
    }

    @SneakyThrows
    public boolean sendServiceRequest() {
        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setRequestID(UUID.randomUUID().toString());
        serviceRequest.setWorkstationID("localhost:8911");
        serviceRequest.setRequestType(ServiceRequestType.RECONCILIATION);
        ServiceRequest.POSdata posData = new ServiceRequest.POSdata();
        posData.setPOSTimeStamp(DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDateTime.now().toString()));
        serviceRequest.setPOSdata(posData);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(APPLICATION_XML);
        HttpEntity<ServiceRequest> requestEntity = new HttpEntity<>(serviceRequest, httpHeaders);

        ResponseEntity<?> response = rest.postForEntity("http://localhost:8910/channel/pos", requestEntity, ResponseEntity.class);
        log.info("Service request send out with response status {}", of(response).map(ResponseEntity::getStatusCode).orElse(INTERNAL_SERVER_ERROR));
        return true;
    }
}
