package com.vqd.ped.pointofsalesservice.controller;

import com.vqd.ped.electronicpaymentservice.model.ped.cardResponse.CardServiceResponse;
import com.vqd.ped.electronicpaymentservice.model.ped.serviceResponse.ServiceResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

@RestController
@RequestMapping("/channel")
@RequiredArgsConstructor
@Slf4j
public class PointOfSalesCardServiceController {

    private final DocumentBuilder documentBuilder;

    @PostMapping(value = "/pos", consumes = APPLICATION_XML_VALUE, produces = APPLICATION_XML_VALUE)
    @SneakyThrows
    public ResponseEntity<?> processResponse(HttpServletRequest request) {
        InputStreamReader inputStreamReader = new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8);

        Document document = documentBuilder.parse(new InputSource(inputStreamReader));
        Element rootElement = document.getDocumentElement();

        // An enum should be used to validate the request type
        if ("CardServiceResponse".equalsIgnoreCase(rootElement.getTagName())) {
            return doProcessCardServiceResponse(document, JAXBContext.newInstance(CardServiceResponse.class));
        } else if ("ServiceResponse".equalsIgnoreCase(rootElement.getTagName())) {
            return doProcessServiceResponse(document, JAXBContext.newInstance(ServiceResponse.class));
        }
        return new ResponseEntity<>(BAD_REQUEST);
    }

    @SneakyThrows
    private ResponseEntity<?> doProcessCardServiceResponse(Document document, JAXBContext jaxbContext) {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        CardServiceResponse response = (CardServiceResponse) unmarshaller.unmarshal(new DOMSource(document.getDocumentElement()));
        log.info("Received card service response {}", response.toString());
        return new ResponseEntity<>(OK);
    }

    @SneakyThrows
    private ResponseEntity<?> doProcessServiceResponse(Document document, JAXBContext jaxbContext) {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        ServiceResponse response = (ServiceResponse) unmarshaller.unmarshal(new DOMSource(document.getDocumentElement()));
        log.info("Received service response {}", response.toString());
        return new ResponseEntity<>(OK);
    }
}
