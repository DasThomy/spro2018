package de.sopro.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.LOCKED,reason="No access to combination")
public class NoAccessToRessourceException extends RuntimeException {

}