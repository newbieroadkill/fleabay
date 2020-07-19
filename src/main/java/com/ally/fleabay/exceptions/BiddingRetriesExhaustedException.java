package com.ally.fleabay.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
public class BiddingRetriesExhaustedException extends RuntimeException {
    public BiddingRetriesExhaustedException(String message) {
        super(message);
    }
}
