package com.example.bankcards.exception;

public class TicketAlreadyResolvedException extends RuntimeException {
    public TicketAlreadyResolvedException(String message) {
        super(message);
    }
}
