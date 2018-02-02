package com.github.nmorel.gwtjackson.client.ser.jackson;

import com.github.nmorel.gwtjackson.client.JsonSerializationContext;
import com.github.nmorel.gwtjackson.client.JsonSerializer;
import com.github.nmorel.gwtjackson.client.JsonSerializerParameters;
import com.github.nmorel.gwtjackson.client.stream.JsonWriter;

/**
 * Serializes a value by using a supplied Jackson JsonSerializer 
 *
 * @param <T>
 */
public class JacksonJsonSerializer<T> extends JsonSerializer<T> {

    com.fasterxml.jackson.databind.JsonSerializer<T> jacksonSerializer;

    @Override
    protected void doSerialize(JsonWriter writer, T value, JsonSerializationContext ctx,
            JsonSerializerParameters params) {
        // TODO Auto-generated method stub

    }

}
