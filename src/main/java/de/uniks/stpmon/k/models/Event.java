package de.uniks.stpmon.k.models;

public record Event<T>(
        String event,
        T data) {

    public String suffix() {
        return event.substring(event.lastIndexOf('.') + 1);
    }

}
