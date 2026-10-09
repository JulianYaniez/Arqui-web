package org.arqui.tpe_3.controllers.exceptions;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ErrorResponse (
   LocalDateTime timestamp,
   int status,
   HttpStatus error,
   String message,
   String path
) {}
