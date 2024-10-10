package ru.practicum.shareit.booking.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


public class InstantDeserializer extends JsonDeserializer<Instant> {
    @Override
    public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String time = p.getText();
        LocalDateTime localDateTime = LocalDateTime.parse(time);
        return localDateTime.toInstant(ZoneOffset.UTC);
    }
}
