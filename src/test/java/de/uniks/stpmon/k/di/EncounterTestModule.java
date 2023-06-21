package de.uniks.stpmon.k.di;

import dagger.Module;
import dagger.Provides;
import de.uniks.stpmon.k.dto.UpdateOpponentDto;
import de.uniks.stpmon.k.models.Encounter;
import de.uniks.stpmon.k.models.Event;
import de.uniks.stpmon.k.models.Message;
import de.uniks.stpmon.k.models.Opponent;
import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.net.Socket;
import de.uniks.stpmon.k.rest.EncounterApiService;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOperator;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import org.mockito.Mockito;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Module
public class EncounterTestModule {

    @Provides
    @Singleton
    static EncounterApiService encounterApiService() {
        return new EncounterApiService() {
            final Map<String, List<Encounter>> encounterHashMap = new HashMap<>();
            final List<Opponent> encounterOpponents = new ArrayList<>();
            final List<Opponent> trainerOpponents = new ArrayList<>();
            final Subject<Event<Opponent>> events = PublishSubject.create();
            @Inject
            EventListener eventListener;
            String opponentId = "0";


            private Encounter initDummyEncounter(String regionId) {
                return new Encounter(
                        "0",
                        regionId,
                        false
                );
            }

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
                if (regionId.isEmpty()) {
                    return Observable.error(new Throwable(regionId + "does not exist"));
                }
                return Observable.just(trainerOpponents.stream().filter(m -> m.trainer().equals(trainerId)).toList());
            }

            @Override
            public Observable<List<Opponent>> getEncounterOpponents(String regionId, String encounterId) {
                if (regionId.isEmpty()) {
                    return Observable.error(new Throwable(regionId + "does not exist"));
                }
                return Observable.just(encounterOpponents.stream().filter(m -> m.encounter().equals(encounterId)).toList());
            }

            @Override
            public Observable<Opponent> getEncounterOpponent(String regionId, String encounterId, String opponentId) {
                Optional<Opponent> opponentOptional = encounterOpponents
                        .stream().filter(m -> m._id().equals(opponentId)).findFirst();
                return opponentOptional.map(m -> Observable.just(opponentOptional.get())).orElseGet(()
                        -> Observable.error(new Throwable("404 Not found")));
            }

            public void mockEvents(String opponentId) {
                this.opponentId = opponentId;

                Mockito.when(eventListener.listen(Socket.WS, "encounter.%s.opponents.*.*".formatted(opponentId), Opponent.class))
                        .thenReturn(events);
            }

            @Override
            public Observable<Opponent> makeMove(String regionId, String encounterId, String opponentId, UpdateOpponentDto opponentDto) {
                if (regionId.isEmpty()) {
                    return Observable.error(new Throwable(regionId + "does not exist"));
                }
                Opponent opponent = new Opponent(opponentId,
                        encounterId,
                        "0",
                        false,
                        false,
                        "monster0",
                        null,
                        null,
                        0
                        );
                return null;
            }

            public Observable<Opponent> makeOpponentMove() {
                //fake ws
                return null;
            }

            @Override
            public Observable<Opponent> fleeEncounter(String regionId, String encounterId, String opponentId) {
                if (regionId.isEmpty()) {
                    return Observable.error(new Throwable(regionId + "does not exist"));
                }

                return null;
            }
        };
    }
}
