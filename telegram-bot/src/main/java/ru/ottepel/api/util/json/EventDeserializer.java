package ru.ottepel.api.util.json;

import com.asana.models.Event;
import com.asana.models.User;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.api.client.util.DateTime;

import java.io.IOException;

public class EventDeserializer extends JsonDeserializer<Event> {

    @Override
    public Event deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        Event event = new Event();
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        event.type = node.get("type").asText();
        String dateTime = node.get("created_at").asText();
        event.createdAt = new DateTime(dateTime);
        event.action = node.get("action").asText();
        event.resource = new Event(). new Entity();
        event.parent = new Event(). new Entity();
        event.user = new User();
        event.user.id = node.get("user").asText();
        event.resource.id = node.get("resource").asText();
        event.parent.id = node.get("parent").asText();
        return  event;
    }
}