package com.kenzie.unit.four.ticketsystem.service;

import com.kenzie.unit.four.ticketsystem.repositories.PurchaseTicketRepository;
import com.kenzie.unit.four.ticketsystem.repositories.model.PurchasedTicketRecord;
import com.kenzie.unit.four.ticketsystem.service.model.PurchasedTicket;

import com.kenzie.unit.four.ticketsystem.service.model.ReservedTicket;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PurchasedTicketService {
    private PurchaseTicketRepository purchaseTicketRepository;
    private ReservedTicketService reservedTicketService;

    public PurchasedTicketService(PurchaseTicketRepository purchaseTicketRepository,
                                  ReservedTicketService reservedTicketService) {
        this.purchaseTicketRepository = purchaseTicketRepository;
        this.reservedTicketService = reservedTicketService;
    }

    public PurchasedTicket purchaseTicket(String reservedTicketId, Double pricePaid) {
        // Your code here
        ReservedTicket reservedTicket = reservedTicketService.findByReserveTicketId(reservedTicketId);

        if(reservedTicket == null || (reservedTicket.getReservationClosed() != null && reservedTicket.getReservationClosed() == true)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Actor not found");
        }
        PurchasedTicketRecord purchasedTicketRecord = new PurchasedTicketRecord();
        purchasedTicketRecord.setTicketId(reservedTicket.getTicketId());
        purchasedTicketRecord.setDateOfPurchase(LocalDateTime.now().toString());
        purchasedTicketRecord.setPricePaid(pricePaid);
        purchasedTicketRecord.setConcertId(reservedTicket.getConcertId());

        purchaseTicketRepository.save(purchasedTicketRecord);

        ReservedTicket reservedTicket1 = new ReservedTicket(reservedTicket.getConcertId(), reservedTicket.getTicketId(),
                reservedTicket.getDateOfReservation(), true, reservedTicket.getDateReservationClosed(),
                true);

        reservedTicketService.updateReserveTicket(reservedTicket1);

        PurchasedTicket purchasedTicket = new PurchasedTicket(reservedTicket1.getConcertId(),
                reservedTicket1.getTicketId(), LocalDateTime.now().toString(), pricePaid);


        return purchasedTicket;
    }

    public List<PurchasedTicket> findByConcertId(String concertId) {
        List<PurchasedTicketRecord> purchasedTicketRecords = purchaseTicketRepository
                .findByConcertId(concertId);

        List<PurchasedTicket> purchasedTickets = new ArrayList<>();

        for (PurchasedTicketRecord purchasedTicketRecord : purchasedTicketRecords) {
            purchasedTickets.add(new PurchasedTicket(purchasedTicketRecord.getConcertId(),
                    purchasedTicketRecord.getTicketId(),
                    purchasedTicketRecord.getDateOfPurchase(),
                    purchasedTicketRecord.getPricePaid()));
        }

        return purchasedTickets;
    }
}
