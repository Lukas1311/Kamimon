package de.uniks.stpmon.k.service.dummies;


import de.uniks.stpmon.k.models.Event;
import de.uniks.stpmon.k.net.EventListener;
import io.reactivex.rxjava3.subjects.PublishSubject;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class MovementDummy {

    public static void addMovementDummy(EventListener listener) {
        PublishSubject<Event<Object>> subject = PublishSubject.create();
        when(listener.listen(any(), any(), any())).thenReturn(subject);
        when(listener.send(any(), any(), any())).thenAnswer((invocation) -> {
            subject.onNext(new Event<>(invocation.getArgument(1), invocation.getArgument(2)));
            return null;
        });
    }

}
