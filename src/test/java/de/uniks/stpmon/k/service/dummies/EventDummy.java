package de.uniks.stpmon.k.service.dummies;

import de.uniks.stpmon.k.dto.MoveTrainerDto;
import de.uniks.stpmon.k.dto.TalkTrainerDto;
import de.uniks.stpmon.k.models.Event;
import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.net.Socket;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Singleton
public class EventDummy {

    final Subject<Event<?>> events = PublishSubject.create();
    private boolean initialized = false;
    private final List<Consumer<Event<?>>> messageHandlers = new CopyOnWriteArrayList<>();
    @Inject
    EventListener listener;

    @Inject
    public EventDummy() {
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "unchecked", "rawtypes"})
    public void ensureMock() {
        if (initialized) {
            return;
        }
        // Result can be ignored, because it is a mock and has not to be disposed
        events.subscribe((event) -> {
            for (Consumer<Event<?>> handler : messageHandlers) {
                handler.accept(event);
            }
        });
        when(listener.listen(any(), any(), any())).thenAnswer((invocation) -> {
            String pattern = invocation.getArgument(1);
            //noinspection ReactiveStreamsUnusedPublisher
            return Observable.create(emitter -> {
                // suppresses unchecked and raw types - we know that the emitter is of type ObservableEmitter<Event<T>>
                // suppresses observable result - is never disposed fo the test time
                Consumer<Event<?>> handler = createPatternHandler(pattern, (ObservableEmitter) emitter);
                messageHandlers.add(handler);
                emitter.setCancellable(() -> messageHandlers.remove(handler));
            });
        });
        when(listener.send(any(), any(), any())).thenAnswer((invocation) -> {
            String pattern = invocation.getArgument(1);
            MoveTrainerDto dto = invocation.getArgument(2);
            sendEvent(new Event<>(pattern, dto));
            return null;
        });
        when(listener.sendTalk(any(), any(), any())).thenAnswer((invocation) -> {
            String pattern = invocation.getArgument(1);
            TalkTrainerDto dto = invocation.getArgument(2);
            sendEvent(new Event<>(pattern, dto));
            return null;
        });
        initialized = true;
    }

    private <T> Consumer<Event<T>> createPatternHandler(String pattern, ObservableEmitter<Event<T>> emitter) {
        // pattern of form like 'regions.*.created'
        final Pattern regex = Pattern.compile(pattern.replace(".", "\\.").replace("*", "[^.]*"));
        return eventModel -> {
            if (!regex.matcher(eventModel.event()).matches()) {
                return;
            }
            emitter.onNext(eventModel);
        };
    }

    public <T> Observable<Event<T>> listen(Socket socket, String pattern, Class<T> type) {
        ensureMock();
        return listener.listen(socket, pattern, type);
    }

    public void sendEvent(Event<?> event) {
        ensureMock();
        events.onNext(event);
    }
}