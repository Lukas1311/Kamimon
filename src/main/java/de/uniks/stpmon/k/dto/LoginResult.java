package de.uniks.stpmon.k.dto;

public record LoginResult(
    String accessToken,
    String refreshToken
) {
    
}
