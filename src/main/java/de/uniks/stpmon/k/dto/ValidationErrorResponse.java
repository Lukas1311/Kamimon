package de.uniks.stpmon.k.dto;

import java.util.List;

public record ValidationErrorResponse(
    Integer statusCode, // example: 404
    String error, // example: Bad Request
    List<String> message
) {
    
}
