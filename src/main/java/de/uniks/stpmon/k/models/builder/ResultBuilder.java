package de.uniks.stpmon.k.models.builder;

import de.uniks.stpmon.k.models.MonsterStatus;
import de.uniks.stpmon.k.models.Result;

@SuppressWarnings("unused")
public class ResultBuilder {

    public static ResultBuilder builder() {
        return new ResultBuilder();
    }

    public static ResultBuilder builder(String type) {
        return builder().setType(type);
    }

    public static ResultBuilder builder(Result result) {
        return builder().apply(result);
    }

    private String type = "";
    private Integer ability = null;
    private String effectiveness = "";
    private MonsterStatus status = null;
    private Integer item = null;

    private ResultBuilder() {
    }

    public ResultBuilder setAbility(Integer ability) {
        this.ability = ability;
        return this;
    }

    public ResultBuilder setEffectiveness(String effectiveness) {
        this.effectiveness = effectiveness;
        return this;
    }

    public ResultBuilder setItem(Integer item) {
        this.item = item;
        return this;
    }

    public ResultBuilder setType(String type) {
        this.type = type;
        return this;
    }

    public ResultBuilder setStatus(MonsterStatus status) {
        this.status = status;
        return this;
    }

    public Result create() {
        return new Result(type, ability, effectiveness, status, item);
    }

    private ResultBuilder apply(Result result) {
        return setAbility(result.ability())
                .setEffectiveness(result.effectiveness())
                .setItem(result.item())
                .setStatus(result.status())
                .setType(result.type());
    }
}
