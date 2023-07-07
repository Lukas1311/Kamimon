package de.uniks.stpmon.k.models;

import java.util.List;

@SuppressWarnings("unused")
public record ValidationErrorResponse(
        Integer statusCode, // example: 404
        String error, // example: Bad Request
        List<String> message
) {

}
