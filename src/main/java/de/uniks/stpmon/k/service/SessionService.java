package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.models.EncounterMember;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.Opponent;
import de.uniks.stpmon.k.service.storage.EncounterSession;
import de.uniks.stpmon.k.service.storage.EncounterStorage;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Facade for the encounter session. This can be used to access the current encounter session. This should be the only
 * way to access the encounter session. This helps to mock the encounter session in tests.
 */
@Singleton
public class SessionService extends DestructibleElement {

    @Inject
    EncounterStorage encounterStorage;

    @Inject
    public SessionService() {
    }

    public Monster getMonster(EncounterMember member) {
        EncounterSession session = encounterStorage.getEncounterSession();
        if (session == null) {
            throw new IllegalStateException("No encounter session available");
        }
        return session.getMonster(member);
    }

    public Observable<Monster> listenMonster(EncounterMember member) {
        EncounterSession session = encounterStorage.getEncounterSession();
        if (session == null) {
            throw new IllegalStateException("No encounter session available");
        }
        return session.listenMonster(member);
    }

    public Observable<Opponent> listenOpponent(EncounterMember member) {
        EncounterSession session = encounterStorage.getEncounterSession();
        if (session == null) {
            throw new IllegalStateException("No encounter session available");
        }
        return session.listenOpponent(member);
    }

    public Opponent getOpponent(EncounterMember member) {
        EncounterSession session = encounterStorage.getEncounterSession();
        if (session == null) {
            throw new IllegalStateException("No encounter session available");
        }
        return session.getOpponent(member);
    }

    public boolean hasMember(EncounterMember member) {
        EncounterSession session = encounterStorage.getEncounterSession();
        if (session == null) {
            throw new IllegalStateException("No encounter session available");
        }
        return session.hasMember(member);
    }

    public Collection<EncounterMember> getMembers() {
        EncounterSession session = encounterStorage.getEncounterSession();
        if (session == null) {
            throw new IllegalStateException("No encounter session available");
        }
        return session.getMembers();
    }


    public List<String> getAttackerTeam() {
        EncounterSession session = encounterStorage.getEncounterSession();
        if (session == null) {
            return Collections.emptyList();
        }
        return session.getAttackerTeam();
    }

    public List<String> getOwnTeam() {
        EncounterSession session = encounterStorage.getEncounterSession();
        if (session == null) {
            return Collections.emptyList();
        }
        return session.getOwnTeam();
    }

}
