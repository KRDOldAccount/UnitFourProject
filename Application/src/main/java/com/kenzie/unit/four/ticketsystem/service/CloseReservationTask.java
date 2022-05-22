package com.kenzie.unit.four.ticketsystem.service;

import com.kenzie.unit.four.ticketsystem.repositories.model.ReserveTicketRecord;
import com.kenzie.unit.four.ticketsystem.service.model.ReservedTicket;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CloseReservationTask implements Runnable {

    private final Integer durationToPay;
    private final ConcurrentLinkedQueue<ReservedTicket> reservedTicketsQueue;
    private final ReservedTicketService reservedTicketService;

    public CloseReservationTask(Integer durationToPay,
                                ReservedTicketService reservedTicketService,
                                ConcurrentLinkedQueue<ReservedTicket> reservedTicketsQueue) {
        this.durationToPay = durationToPay;
        this.reservedTicketService = reservedTicketService;
        this.reservedTicketsQueue = reservedTicketsQueue;
    }

    @Override
    public void run() {
       // Your code here
        ReservedTicket reservedTicket = reservedTicketsQueue.poll();
        if(reservedTicket == null) {
            return;
        }
        reservedTicket = reservedTicketService.findByReserveTicketId(reservedTicket.getTicketId());

        Duration duration = Duration.between(LocalDateTime.parse(reservedTicket.getDateOfReservation()),
                LocalDateTime.now());

        if(duration.getSeconds() > durationToPay && !reservedTicket.getTicketPurchased()) {
            reservedTicket = new ReservedTicket(reservedTicket.getConcertId(), reservedTicket.getTicketId(),
                    reservedTicket.getDateOfReservation(), true, String.valueOf(LocalDateTime.now()),
                    false);
            reservedTicketService.updateReserveTicket(reservedTicket);
        } else if(reservedTicket.getTicketPurchased() && duration.getSeconds() < durationToPay) {
            reservedTicketsQueue.add(reservedTicket);
        }
    }
}
