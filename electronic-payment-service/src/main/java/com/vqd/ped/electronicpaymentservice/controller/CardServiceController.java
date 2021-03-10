package com.vqd.ped.electronicpaymentservice.controller;

import com.vqd.ped.electronicpaymentservice.model.ped.cardRequest.CardServiceRequest;
import com.vqd.ped.electronicpaymentservice.model.ped.serviceRequest.ServiceRequest;
import com.vqd.ped.electronicpaymentservice.service.ElectronicPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.dom.DOMSource;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("/channel")
@RequiredArgsConstructor
@Slf4j
public class CardServiceController {

    private final AsyncTaskExecutor taskExecutor;
    private final DocumentBuilder documentBuilder;
    private final ElectronicPaymentService service;

    @SneakyThrows
    @PostMapping(value = "/pos")
    public ResponseEntity<?> processRequest(HttpServletRequest request) {
        InputStreamReader inputStreamReader = new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8);

        Document document = documentBuilder.parse(new InputSource(inputStreamReader));
        Element rootElement = document.getDocumentElement();

        // An enum should be used to validate the request type
        if ("CardServiceRequest".equalsIgnoreCase(rootElement.getTagName())) {
            JAXBContext jaxbContext = JAXBContext.newInstance(CardServiceRequest.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            CardServiceRequest cardServiceRequest = (CardServiceRequest) unmarshaller.unmarshal(new DOMSource(document.getDocumentElement()));
            taskExecutor.execute(doProcessRequest(cardServiceRequest));
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } else if ("ServiceRequest".equalsIgnoreCase(rootElement.getTagName())) {
            JAXBContext jaxbContext = JAXBContext.newInstance(ServiceRequest.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            ServiceRequest serviceRequest = (ServiceRequest) unmarshaller.unmarshal(new DOMSource(document.getDocumentElement()));
            taskExecutor.execute(doProcessRequest(serviceRequest));
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(BAD_REQUEST);
    }

    private Runnable doProcessRequest(CardServiceRequest cardServiceRequest) {
        return () -> {
            try {
                Thread.sleep(10000);
                service.createDelayedCardServiceResponse(cardServiceRequest);
            } catch (InterruptedException e) {
                log.warn("Failed to send out Card service response: {}", e.getMessage());
            }
        };
    }

    private Runnable doProcessRequest(ServiceRequest serviceRequest) {
        return () -> {
            try {
                Thread.sleep(10000);
                service.createDelayedServiceResponse(serviceRequest);
            } catch (InterruptedException e) {
                log.warn("Failed to send out Card service response: {}", e.getMessage());
            }
        };
    }

}
