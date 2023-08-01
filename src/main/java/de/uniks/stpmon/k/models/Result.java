package de.uniks.stpmon.k.models;

/**
 * Represents a result of a battle move.
 *
 * @param type          Type of the result. <br>
 *                      For example: [ ability-success, ability-failed, item-success, status-added, status-removed, status-damage,
 *                      target-defeated, monster-changed, monster-defeated, monster-levelup, monster-evolved, monster-forgot,
 *                      monster-learned, monster-caught, monster-dead, ability-unknown, ability-no-uses, item-failed,
 *                      target-unknown, target-dead ]
 * @param ability       Ability that was used or was learned.
 * @param effectiveness The effectiveness of the ability. <br>
 *                      For example: [ super-effective, effective, normal, ineffective, no-effect ]
 * @param status        The status that was added, removed or damaged.
 * @param item          The item that was used.
 */
public record Result(
        String type,
        Integer ability,
        String effectiveness,
        MonsterStatus status,
        Integer item) {
}
