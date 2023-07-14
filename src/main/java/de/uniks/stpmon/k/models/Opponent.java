package de.uniks.stpmon.k.models;

import de.uniks.stpmon.k.dto.IMove;
import de.uniks.stpmon.k.dto.IMoves;

import java.util.List;

public record Opponent(

        String _id, // objectId: example: 507f191e810c19729de860ea

        String encounter, // objectId: example: 507f191e810c19729de860ea

        String trainer, // objectId: example: 507f191e810c19729de860ea

        Boolean isAttacker, // Whether the opponent started the encounter. Allows grouping opponents into two teams.

        Boolean isNPC, // Whether the opponent is an NPC. Handled by the server.

        String monster,
        // Can be patched when set to undefined/null. This happens after the monster died. You then have to patch a new monster ID to change the monster without expending your move.

        IMove move,

        List<Result> results, // The results of the last round.

        Integer coins // The number of coins that will be earned when the encounter is won.

) implements IMoves {

}
