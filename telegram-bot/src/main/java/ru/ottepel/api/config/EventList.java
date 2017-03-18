package ru.ottepel.api.config;

import com.asana.models.Event;
import org.springframework.stereotype.Component;

/**
 * Created by vovcyan on 18.03.17.
 */
public class EventList {
    private Event[] events;

    public EventList() {
    }

    public Event[] getEvents() {
        return events;
    }

    public void setEvents(Event[] events) {
        this.events = events;
    }
}
