package de.uniks.stpmon.k.models;

public record ErrorResponse(
        Integer statusCode,
        // possible codes: 400, 401, 403, 404, 405, 406, 408, 409, 410, 412, 413, 415, 418, 422, 500, 501, 502, 503, 504
        String error,
        String message
) {

}
