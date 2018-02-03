package com.github.nmorel.gwtjackson.client.deser.jackson;

import com.github.nmorel.gwtjackson.client.JsonDeserializationContext;
import com.github.nmorel.gwtjackson.client.JsonDeserializer;
import com.github.nmorel.gwtjackson.client.JsonDeserializerParameters;
import com.github.nmorel.gwtjackson.client.stream.JsonReader;

public abstract class JacksonJsonDeserializer<T> extends JsonDeserializer<T> {

    com.fasterxml.jackson.databind.JsonDeserializer<T> jacksonDeserializer;

    public JacksonJsonDeserializer(com.fasterxml.jackson.databind.JsonDeserializer<T> jacksonDeserializer) {
        this.jacksonDeserializer = jacksonDeserializer;
    }

    @Override
    protected T doDeserialize(JsonReader reader, JsonDeserializationContext ctx, JsonDeserializerParameters params) {
        // TODO Auto-generated method stub
        jacksonDeserializer.deserialize(new JacksonJsonParser(reader,ctx,params), new JacksonDeserializationContext(ctx,params))
        return null;
    }

}
