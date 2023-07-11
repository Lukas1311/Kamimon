package de.uniks.stpmon.k.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(value = AbilityMove.class, name = "ability"),
        @JsonSubTypes.Type(value = ChangeMonsterMove.class, name = "change-monster")})
public interface IMove {

}
