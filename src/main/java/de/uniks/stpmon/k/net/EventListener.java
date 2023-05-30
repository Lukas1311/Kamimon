package de.uniks.stpmon.k.net;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stpmon.k.Main;
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
    private final Map<Socket, ClientEndpoint> endpoints = new HashMap<>();

    @Inject
    public EventListener(TokenStorage tokenStorage, ObjectMapper mapper) {
        this.tokenStorage = tokenStorage;
        this.mapper = mapper;
    }

    private ClientEndpoint ensureOpen(Socket key) {
        ClientEndpoint instance = endpoints.get(key);
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
            case UDP -> instance = new UDPEndpoint();
        }
        instance.open();
        endpoints.put(key, instance);
        return instance;
    }

    public <T> Observable<Event<T>> listen(Socket endpoint, String pattern, Class<T> type) {
        return Observable.create(emitter -> {
            ClientEndpoint instance = this.ensureOpen(endpoint);
            send(instance, new Event<>("subscribe", pattern));
            final Consumer<String> handler = createPatternHandler(mapper, pattern, type, emitter);
            instance.addMessageHandler(handler);
            emitter.setCancellable(() -> removeEventHandler(instance, pattern, handler));
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
                e.printStackTrace();
            }
        };
    }

    private void removeEventHandler(ClientEndpoint instance, String pattern, Consumer<String> handler) {
        if (instance == null) {
            return;
        }

        send(instance, new Event<>("unsubscribe", pattern));
        instance.removeMessageHandler(handler);
        if (!instance.hasMessageHandlers()) {
            instance.close();
            endpoints.remove(instance instanceof WSEndpoint ? Socket.WS : Socket.UDP);
        }
    }

    private void send(ClientEndpoint instance, Object message) {
        try {
            final String msg = mapper.writeValueAsString(message);
            instance.sendMessage(msg);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void send(Socket endpoint, Object message) {
        ClientEndpoint instance = ensureOpen(endpoint);
        send(instance, message);
    }

    private void close() {
        for (ClientEndpoint endpoint : endpoints.values()) {
            endpoint.close();
        }
        endpoints.clear();
    }
}
