package de.uniks.stpmon.k.dto;

public record ErrorResponse(
    Number statusCode,
    String error,
    String message
) {

}
