package de.uniks.stpmon.k.world.rules;

/**
 * Represents the result of a rule
 */
public enum RuleResult {
    /**
     * No match, continue with next rule
     */
    NO_MATCH,
    /**
     * No match, stop with connection rules
     */
    NO_MATCH_STOP,
    /**
     * Match, stop with rules
     */
    MATCH_SINGLE,
    MATCH_CONNECTION,
}
