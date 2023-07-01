package de.uniks.stpmon.k.world.rules;

import de.uniks.stpmon.k.models.map.DecorationLayer;
import de.uniks.stpmon.k.utils.IterableUtils;

import java.util.*;

public class RuleRegistry {
    private final Map<String, RegistryGroup> groupByTileset = new HashMap<>();
    private final List<PropRule> connectionRules = new ArrayList<>();
    private final List<PropRule> singleRules = new ArrayList<>();
    private final List<CandidateRule> candidateRules = new ArrayList<>();

    public void addSingle(PropRule rule) {
        if (rule instanceof BaseTilesetRule baseTile) {
            RegistryGroup group = groupByTileset.computeIfAbsent(baseTile.getTileSet(), (k) -> new RegistryGroup());
            for (Integer tileId : baseTile.getTileIds()) {
                group.singleRules.addRule(tileId, rule);
            }
            return;
        }
        singleRules.add(rule);
    }

    public void addConnection(PropRule rule) {
        if (rule instanceof BaseTilesetRule baseTile) {
            RegistryGroup group = groupByTileset.computeIfAbsent(baseTile.getTileSet(), (k) -> new RegistryGroup());
            for (Integer tileId : baseTile.getTileIds()) {
                group.connectionRules.addRule(tileId, rule);
            }
            return;
        }
        connectionRules.add(rule);
    }

    public void addCandidate(CandidateRule rule) {
        if (rule instanceof BaseTilesetRule baseTile) {
            RegistryGroup group = groupByTileset.computeIfAbsent(baseTile.getTileSet(), (k) -> new RegistryGroup());
            for (Integer tileId : baseTile.getTileIds()) {
                group.candidateRules.addRule(tileId, rule);
            }
            return;
        }
        candidateRules.add(rule);
    }

    private Iterable<PropRule> getRules(boolean single, PropInfo info) {
        RegistryGroup group = groupByTileset.get(info.tileSet());
        if (group == null) {
            return single ? singleRules : connectionRules;
        }
        return IterableUtils.concat(
                (single ? group.singleRules : group.connectionRules).getRules(info.tileId()),
                single ? singleRules : connectionRules);
    }

    public RuleResult applyRule(boolean single, PropInfo info, List<DecorationLayer> decorationLayers) {
        for (PropRule rule : getRules(single, info)) {
            RuleResult result = rule.apply(info, decorationLayers);
            if (result != RuleResult.NO_MATCH) {
                return result;
            }
        }
        return RuleResult.NO_MATCH;
    }

    public PropInfo getPropInfo(PropInfo info, List<PropInfo> candidates, List<DecorationLayer> decorationLayers) {
        PropInfo bestCandidate = null;
        if (candidates.size() > 1) {
            RegistryGroup group = groupByTileset.get(info.tileSet());
            Iterable<CandidateRule> candidateRules = group != null ?
                    IterableUtils.concat(group.candidateRules.getRules(info.tileId()), this.candidateRules)
                    : this.candidateRules;
            for (CandidateRule rule : candidateRules) {
                bestCandidate = rule.apply(candidates, decorationLayers);
                if (bestCandidate != null) {
                    break;
                }
            }
        } else {
            bestCandidate = candidates.get(0);
        }
        return bestCandidate;
    }

    private static class RegistryGroup {
        private final RuleMap<PropRule> connectionRules = new RuleMap<>();
        private final RuleMap<CandidateRule> candidateRules = new RuleMap<>();
        private final RuleMap<PropRule> singleRules = new RuleMap<>();
    }

    private static class RuleMap<R> {
        private final Map<Integer, List<R>> rules = new HashMap<>();

        public void addRule(int tileId, R rule) {
            List<R> ruleList = rules.computeIfAbsent(tileId, (k) -> new LinkedList<>());
            ruleList.add(rule);
            rules.put(tileId, ruleList);
        }

        public List<R> getRules(int tileId) {
            return rules.getOrDefault(tileId, List.of());
        }
    }
}
