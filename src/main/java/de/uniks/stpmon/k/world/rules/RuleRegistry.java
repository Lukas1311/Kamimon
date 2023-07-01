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

    private Iterable<PropRule> getSingleRules(PropInfo info) {
        RegistryGroup group = groupByTileset.get(info.tileSet());
        if (group == null) {
            return singleRules;
        }
        return IterableUtils.concat((group.singleRules).getRules(info.tileId()), singleRules);
    }

    public RuleResult applySingleRule(PropInfo info, List<DecorationLayer> decorationLayers) {
        for (PropRule rule : getSingleRules(info)) {
            RuleResult result = rule.apply(info, decorationLayers);
            if (result != RuleResult.NO_MATCH) {
                return result;
            }
        }
        return RuleResult.NO_MATCH;
    }

    private Collection<PropRule> getConnectionRules(String tileset, int tileId) {
        RegistryGroup group = groupByTileset.get(tileset);
        if (group == null) {
            return Collections.emptyList();
        }
        return group.connectionRules.getRules(tileId);
    }

    public RuleResult applyRule(PropInfo info, List<DecorationLayer> decorationLayers) {
        Set<PropRule> rules = new LinkedHashSet<>(getConnectionRules(info.tileSet(), info.tileId()));
        rules.addAll(getConnectionRules(info.otherTileSet(), info.otherTileId()));
        rules.addAll(connectionRules);
        for (PropRule rule : rules) {
            RuleResult result = rule.apply(info, decorationLayers);
            if (result != RuleResult.NO_MATCH) {
                return result;
            }
        }
        return RuleResult.NO_MATCH;
    }

    public PropInfo getPropInfo(PropInfo info, List<PropInfo> candidates, List<DecorationLayer> decorationLayers) {
        if (candidates.size() <= 1) {
            return candidates.get(0);
        }
        PropInfo bestCandidate;
        Set<CandidateRule> rules = new LinkedHashSet<>(getCandidateRules(info));
        for (PropInfo candidateInfo : candidates) {
            rules.addAll(getCandidateRules(candidateInfo));
        }
        rules.addAll(candidateRules);
        for (CandidateRule rule : rules) {
            bestCandidate = rule.apply(candidates, decorationLayers);
            if (bestCandidate != null) {
                return bestCandidate;
            }
        }
        return null;
    }

    private Collection<CandidateRule> getCandidateRules(PropInfo info) {
        RegistryGroup group = groupByTileset.get(info.tileSet());
        if (group == null) {
            return Collections.emptyList();
        }
        return group.candidateRules.getRules(info.tileId());
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
