package de.uniks.stpmon.k.models;

public record Result(

        String type,
        // enum [ ability-success, target-defeated, monster-changed, monster-defeated, monster-levelup, monster-evolved,
        // monster-learned, monster-dead, ability-unknown, ability-no-uses, target-unknown, target-dead ]

        Integer ability, // For ability-* and monster-learned

        String effectiveness
        // For ability-success -> enum [ super-effective, effective, normal, ineffective, no-effect ]

) {

}
