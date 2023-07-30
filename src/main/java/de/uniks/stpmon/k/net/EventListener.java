package de.uniks.stpmon.k.net;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stpmon.k.Main;
import de.uniks.stpmon.k.dto.MoveTrainerDto;
import de.uniks.stpmon.k.dto.TalkTrainerDto;
import de.uniks.stpmon.k.models.Event;
import de.uniks.stpmon.k.service.storage.TokenStorage;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;

@Singleton
public class EventListener {

    private final TokenStorage tokenStorage;
    private final ObjectMapper mapper;
    private final Map<Socket, SocketSender> adapters = new HashMap<>();

    @Inject
    public EventListener(TokenStorage tokenStorage, ObjectMapper mapper) {
        this.tokenStorage = tokenStorage;
        this.mapper = mapper;
    }

    private SocketSender ensureOpen(Socket key) {
        SocketSender instance = adapters.get(key);
        if (instance != null && instance.isOpen()) {
            return instance;
        }

        switch (key) {
            case WS -> {
                final URI endpointURI;
                try {
                    endpointURI = new URI(Main.WS_URL + "/events?authToken=" + tokenStorage.getToken());
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }

                instance = new WSEndpoint(endpointURI);
            }
            case UDP -> instance = new UDPSender();
        }
        instance.open();
        adapters.put(key, instance);
        return instance;
    }

    public <T> Observable<Event<T>> listen(Socket socket, String pattern, Class<T> type) {
        return Observable.create(emitter -> {
            SocketSender adapter = this.ensureOpen(socket);
            SocketReceiver endpoint = send(adapter, new Event<>("subscribe", pattern), true);
            if (endpoint == null) {
                emitter.onError(new RuntimeException("Could not subscribe to events"));
                return;
            }
            final Consumer<String> handler = createPatternHandler(mapper, pattern, type, emitter);
            endpoint.addMessageHandler(handler);
            emitter.setCancellable(() -> removeEventHandler(adapter, endpoint, pattern, handler));
        });
    }

    public static <T> Consumer<String> createPatternHandler(
            ObjectMapper mapper, String pattern, Class<T> type, ObservableEmitter<Event<T>> emitter
    ) {
        // pattern of form like 'regions.*.created'
        final Pattern regex = Pattern.compile(pattern.replace(".", "\\.").replace("*", "[^.]*"));
        return eventStr -> {
            try {
                final JsonNode node = mapper.readTree(eventStr);
                final String event = node.get("event").asText();
                if (!regex.matcher(event).matches()) {
                    return;
                }

                final T data = mapper.treeToValue(node.get("data"), type);
                emitter.onNext(new Event<>(event, data));
            } catch (JsonProcessingException e) {
                //noinspection CallToPrintStackTrace
                e.printStackTrace();
            }
        };
    }

    private void removeEventHandler(SocketSender adapter, SocketReceiver endpoint, String pattern, Consumer<String> handler) {
        send(adapter, new Event<>("unsubscribe", pattern), false);
        endpoint.removeMessageHandler(handler);
        adapter.removeReceiver(endpoint);
        if (adapter.canClose()) {
            adapter.close();
        }
    }

    private SocketReceiver send(SocketSender adapter, Object message, boolean openEndpoint) {
        try {
            final String msg = mapper.writeValueAsString(message);
            return adapter.sendMessage(msg, openEndpoint);
        } catch (JsonProcessingException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            return null;
        }
    }

    public Object send(Socket socket, String event, MoveTrainerDto moveTrainerDto) {
        SocketSender adapter = this.ensureOpen(socket);
        return send(adapter, new Event<>(event, moveTrainerDto), false);
    }

    public Object sendTalk(Socket socket, String event, TalkTrainerDto talkTrainerDto) {
        SocketSender adapter = this.ensureOpen(socket);
        return send(adapter, new Event<>(event, talkTrainerDto), false);
    }

}
