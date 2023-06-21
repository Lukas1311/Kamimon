package de.uniks.stpmon.k.di;

import dagger.Module;
import dagger.Provides;
import de.uniks.stpmon.k.dto.UpdateOpponentDto;
import de.uniks.stpmon.k.models.Encounter;
import de.uniks.stpmon.k.models.Opponent;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.rest.EncounterApiService;
import de.uniks.stpmon.k.rest.RegionApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Singleton;
import java.util.*;

@Module
public class EncounterTestModule {

    @Provides
    @Singleton
    static EncounterApiService encounterApiService(RegionApiService regionApiService) {
        return new EncounterApiService() {
            final Map<String, List<Encounter>> encounterHashMap = new HashMap<>();
            final List<Opponent> encounterOpponents = new ArrayList<>();
            final List<Region> regions = regionApiService.getRegions().blockingFirst();
            final List<Opponent> trainerOpponents = new ArrayList<>();


            @Override
            public Observable<List<Encounter>> getEncounters(String regionId) {
                if (regionId.isEmpty()) {
                    return Observable.error(new Throwable(regionId + "does not exist"));
                }
                List<Encounter> encounterList = encounterHashMap.get(regionId);
                if (encounterList != null) {
                    return Observable.just(encounterList);
                }
                return Observable.empty();
            }

            @Override
            public Observable<Encounter> getEncounter(String regionId, String id) {
                if (regionId.isEmpty()) {
                    return Observable.error(new Throwable(regionId + "does not exist"));
                }
                List<Encounter> encounterList = encounterHashMap.get(regionId);
                if (encounterList == null || encounterList.isEmpty()) {
                    return Observable.error(new Throwable("404 Not found"));
                }
                Optional<Encounter> encounterOptional = encounterList.stream().filter(a -> a._id().equals(id)).findFirst();
                return encounterOptional.map(Observable::just).orElseGet(Observable::empty);
            }

            @Override
            public Observable<List<Opponent>> getTrainerOpponents(String regionId, String trainerId) {
                return Observable.just(trainerOpponents.stream().filter(m -> m.trainer().equals(trainerId)).toList());
            }

            @Override
            public Observable<List<Opponent>> getEncounterOpponents(String regionId, String encounterId) {
                return Observable.just(encounterOpponents.stream().filter(m -> m.encounter().equals(encounterId)).toList());
            }

            @Override
            public Observable<Opponent> getEncounterOpponent(String regionId, String encounterId, String id) {
                Optional<Opponent> opponentOptional = encounterOpponents
                        .stream().filter(m -> m._id().equals(id)).findFirst();
                return opponentOptional.map(m -> Observable.just(opponentOptional.get())).orElseGet(()
                        -> Observable.error(new Throwable("404 Not found")));
            }

            @Override
            public Observable<Opponent> makeMove(String regionId, String encounterId, String id, UpdateOpponentDto opponentDto) {
                return null;
            }

            @Override
            public Observable<Opponent> fleeEncounter(String regionId, String encounterId, String id) {
                return null;
            }
        };
    }
}
