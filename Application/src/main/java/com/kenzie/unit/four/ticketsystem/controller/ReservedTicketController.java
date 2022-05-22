package com.kenzie.unit.four.ticketsystem.controller;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.kenzie.unit.four.ticketsystem.controller.model.ConcertResponse;
import com.kenzie.unit.four.ticketsystem.controller.model.ReservedTicketCreateRequest;
import com.kenzie.unit.four.ticketsystem.controller.model.ReservedTicketResponse;
import com.kenzie.unit.four.ticketsystem.service.ReservedTicketService;
import com.kenzie.unit.four.ticketsystem.service.model.Concert;
import com.kenzie.unit.four.ticketsystem.service.model.ReservedTicket;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.UUID.randomUUID;

@RestController
@RequestMapping("/reservedtickets")
public class ReservedTicketController {

    private ReservedTicketService reservedTicketService;

    ReservedTicketController(ReservedTicketService reservedTicketService) {
        this.reservedTicketService = reservedTicketService;
    }

    // TODO - Task 2: reserveTicket() - POST
    // Add the correct annotation
    @PostMapping
    public ResponseEntity<ReservedTicketResponse> reserveTicket(
            @RequestBody ReservedTicketCreateRequest reservedTicketCreateRequest) {

//        // Add your code here
        ReservedTicket reservedTicket = new ReservedTicket(reservedTicketCreateRequest.getConcertId(),
                randomUUID().toString(), LocalDateTime.now().toString());
        reservedTicketService.reserveTicket(reservedTicket);
        ReservedTicketResponse reservedTicketResponse = new ReservedTicketResponse();
        reservedTicketResponse.setConcertId(reservedTicket.getConcertId());
        reservedTicketResponse.setTicketId(reservedTicket.getTicketId());
        reservedTicketResponse.setDateOfReservation(reservedTicket.getDateOfReservation());
        reservedTicketResponse.setPurchasedTicket(reservedTicket.getTicketPurchased());
        reservedTicketResponse.setReservationClosed(reservedTicket.getReservationClosed());

        // Return your ReservedTicketResponse instead of null
        return ResponseEntity.ok(reservedTicketResponse);
    }

    // TODO - Task 3: getAllReserveTicketsByConcertId() - GET `/concerts/{concertId}`
    // Add the correct annotation
    @GetMapping("concerts/{concertId}")
    public ResponseEntity<List<ReservedTicketResponse>> getAllReserveTicketsByConcertId(
            @PathVariable("concertId") String concertId) {

        // Add your code here
        List<ReservedTicket> reservedTicketList = reservedTicketService.findByConcertId(concertId);

        if (reservedTicketList == null) {
            return ResponseEntity.notFound().build();
        }

        List<ReservedTicketResponse> response = new ArrayList<>();
        for (ReservedTicket rt : reservedTicketList) {
            ReservedTicketResponse rtr = new ReservedTicketResponse();
            rtr.setConcertId(rt.getConcertId());
            rtr.setTicketId(rt.getTicketId());
            rtr.setDateOfReservation(rt.getDateOfReservation());
            rtr.setReservationClosed(rt.getReservationClosed());
            rtr.setDateReservationClosed(rt.getDateReservationClosed());
            rtr.setPurchasedTicket(rt.getTicketPurchased());
            response.add(rtr);
        }

        // Return your List<ReservedTicketResponse> instead of null
        return ResponseEntity.ok(response);
    }

}
