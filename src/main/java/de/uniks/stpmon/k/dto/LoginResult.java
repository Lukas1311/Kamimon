package de.uniks.stpmon.k.dto;

import java.util.ArrayList;

public record LoginResult(
    String name,
    String status,
    String avatar,
    ArrayList<String> friends,
    String accessToken,
    String refreshToken
) {
    
}
