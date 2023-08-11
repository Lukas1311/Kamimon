package de.uniks.stpmon.k.world.rules;

import de.uniks.stpmon.k.models.map.DecorationLayer;
import de.uniks.stpmon.k.utils.Direction;

import java.util.*;

public class RuleRegistry {
    private final Map<String, TilesetProperties> properties = new HashMap<>();
    private final List<ConnectionRule> connectionRules = new ArrayList<>();
    private final List<LoneRule> loneRules = new ArrayList<>();
    private final List<CandidateRule> candidateRules = new ArrayList<>();

    public void markDecoration(String tileSet, int... sources) {
        markDecoration(tileSet, Arrays.stream(sources).boxed().toList());
    }

    public void markDecoration(String tileSet, IdSource... sources) {
        markDecoration(tileSet, Arrays.stream(sources).flatMap(s -> s.get().stream()).toList());
    }

    public void markDecoration(String tileSet, Collection<Integer> c) {
        TilesetProperties tileset = properties.computeIfAbsent(tileSet, (k) -> new TilesetProperties());
        for (Integer tileId : c) {
            tileset.setProperty(tileId, TileProperties.DECORATION);
        }
    }

    public void markEntangled(String tileSet, IdSource... sources) {
        markEntangled(tileSet, Arrays.stream(sources).flatMap(s -> s.get().stream()).toList());
    }

    public void markEntangled(String tileSet, Collection<Integer> c) {
        TilesetProperties tileset = properties.computeIfAbsent(tileSet, (k) -> new TilesetProperties());
        tileset.markEntangled(c);
    }

    public void markBottom(String tileSet, IdSource... sources) {
        markBottom(tileSet, Arrays.stream(sources).flatMap(s -> s.get().stream()).toList());
    }

    public void markBottom(String tileSet, Collection<Integer> c) {
        TilesetProperties tileset = properties.computeIfAbsent(tileSet, (k) -> new TilesetProperties());
        for (Integer tileId : c) {
            tileset.setProperty(tileId, TileProperties.BOTTOM);
        }
    }

    public void markBlend(String tileSet, IdSource... sources) {
        markBlend(tileSet, Arrays.stream(sources).flatMap(s -> s.get().stream()).toList());
    }

    public void markBlend(String tileSet, Collection<Integer> c) {
        TilesetProperties tileset = properties.computeIfAbsent(tileSet, (k) -> new TilesetProperties());
        for (Integer tileId : c) {
            tileset.setProperty(tileId, TileProperties.BLEND);
        }
    }

    public boolean isDecoration(TileInfo info) {
        TilesetProperties tileset = properties.get(info.tileSet());
        if (tileset == null) {
            return false;
        }
        return tileset.getProperty(info.tileId(), TileProperties.DECORATION);
    }

    public boolean isBottom(TileInfo info) {
        TilesetProperties tileset = properties.get(info.tileSet());
        if (tileset == null) {
            return false;
        }
        return tileset.getProperty(info.tileId(), TileProperties.BOTTOM);
    }

    public boolean isBlending(TileInfo info) {
        TilesetProperties tileset = properties.get(info.tileSet());
        if (tileset == null) {
            return false;
        }
        return tileset.getProperty(info.tileId(), TileProperties.BLEND);
    }

    public int getEntangledGroup(String tileSet, int id) {
        TilesetProperties tileset = properties.get(tileSet);
        if (tileset == null) {
            return 0;
        }
        return tileset.getEntangled(id);
    }

    public void addLone(LoneRule rule) {
        loneRules.add(rule);
    }

    public void addConnection(ConnectionRule rule) {
        connectionRules.add(rule);
    }

    public void addCandidate(CandidateRule rule) {
        addCandidate(rule, true);
    }

    public void addCandidate(CandidateRule rule, boolean prioritised) {
        if (rule instanceof BaseTilesetRule baseTile) {
            TilesetProperties group = properties.computeIfAbsent(baseTile.getTileSet(), (k) -> new TilesetProperties());
            RuleMap<CandidateRule> candidates = prioritised ? group.prioritisedCandidates : group.candidates;
            for (Integer tileId : baseTile.getTileIds()) {
                candidates.addRule(tileId, rule);
            }
            return;
        }
        candidateRules.add(rule);
    }

    public RuleResult tryToExtract(TileInfo current, List<DecorationLayer> decorationLayers) {
        for (LoneRule rule : loneRules) {
            RuleResult result = rule.apply(current, decorationLayers);
            if (result != RuleResult.NO_MATCH) {
                return result;
            }
        }
        return RuleResult.NO_MATCH;
    }

    public RuleResult tryToConnect(TileInfo current, TileInfo other,
                                   Direction currentDir, Direction otherDir,
                                   List<DecorationLayer> decorationLayers) {
        for (ConnectionRule rule : connectionRules) {
            RuleResult result = rule.apply(current, other, currentDir, otherDir, decorationLayers);
            if (result != RuleResult.NO_MATCH) {
                return result;
            }
        }
        return RuleResult.NO_MATCH;
    }

    public TileInfo getPropInfo(TileInfo info, List<TileInfo> candidates, List<DecorationLayer> decorationLayers) {
        if (candidates.size() <= 1) {
            return candidates.get(0);
        }
        TileInfo bestCandidate;
        Set<CandidateRule> rules = new LinkedHashSet<>(getPrioritisedCandidateRules(info));
        for (TileInfo candidateInfo : candidates) {
            rules.addAll(getPrioritisedCandidateRules(candidateInfo));
        }
        rules.addAll(candidateRules);
        rules.addAll(getCandidateRules(info));
        for (TileInfo candidateInfo : candidates) {
            rules.addAll(getCandidateRules(candidateInfo));
        }
        for (CandidateRule rule : rules) {
            bestCandidate = rule.apply(info, candidates, decorationLayers);
            if (bestCandidate != null) {
                return bestCandidate;
            }
            // Break if no candidates are left
            if (candidates.isEmpty()) {
                return null;
            }
        }
        return null;
    }

    private Collection<CandidateRule> getPrioritisedCandidateRules(TileInfo info) {
        TilesetProperties group = properties.get(info.tileSet());
        if (group == null) {
            return Collections.emptyList();
        }
        return group.prioritisedCandidates.getRules(info.tileId());
    }

    private Collection<CandidateRule> getCandidateRules(TileInfo info) {
        TilesetProperties group = properties.get(info.tileSet());
        if (group == null) {
            return Collections.emptyList();
        }
        return group.candidates.getRules(info.tileId());
    }

    private static class TilesetProperties {
        private final Map<Integer, Integer> properties = new HashMap<>();
        private final RuleMap<CandidateRule> prioritisedCandidates = new RuleMap<>();
        private final RuleMap<CandidateRule> candidates = new RuleMap<>();
        private int groupId = 1;

        public void markEntangled(Collection<Integer> c) {
            int id = groupId++;
            for (Integer tileId : c) {
                setEntangled(tileId, id);
            }
        }

        private void setProperty(int tileId, TileProperties property) {
            int prop = properties.getOrDefault(tileId, 0);
            properties.put(tileId, prop | (1 << property.ordinal()));
        }

        public boolean getProperty(int tileId, TileProperties property) {
            return (properties.getOrDefault(tileId, 0) & (1 << property.ordinal())) != 0;
        }

        public void setEntangled(int tileId, int groupId) {
            int prop = properties.getOrDefault(tileId, 0);
            properties.put(tileId, prop | groupId << 16);
        }

        public int getEntangled(int tileId) {
            return properties.getOrDefault(tileId, 0) >> 16 & 0xFFFF;
        }
    }

    public enum TileProperties {
        DECORATION,
        BOTTOM,
        BLEND
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
