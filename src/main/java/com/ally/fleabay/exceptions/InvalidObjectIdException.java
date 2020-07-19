package com.ally.fleabay.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidObjectIdException extends RuntimeException {
    public InvalidObjectIdException(){
        super();
    }
}
