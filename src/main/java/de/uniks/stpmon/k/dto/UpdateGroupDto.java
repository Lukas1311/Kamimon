package de.uniks.stpmon.k.dto;

import java.util.ArrayList;

public record UpdateGroupDto(
    String name, // minLength: 1, maxLength: 32
    ArrayList<String> members // maxItems: 100
) {
    
}
