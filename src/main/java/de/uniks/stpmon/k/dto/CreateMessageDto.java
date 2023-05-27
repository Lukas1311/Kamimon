package de.uniks.stpmon.k.dto;

public record CreateMessageDto(
        String body // maxLength: 16384
) {

}
