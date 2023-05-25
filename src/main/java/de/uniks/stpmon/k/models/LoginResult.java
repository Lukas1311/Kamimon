package de.uniks.stpmon.k.models;

import java.util.ArrayList;

public record LoginResult(
        String _id,
        String name,
        String status,
        String avatar,
        ArrayList<String> friends,
        String accessToken,
        String refreshToken
) {

}
