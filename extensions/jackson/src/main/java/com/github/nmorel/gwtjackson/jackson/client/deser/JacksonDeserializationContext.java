package com.github.nmorel.gwtjackson.jackson.client.deser;

import java.io.IOException;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerator.IdKey;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.UnresolvedForwardReference;
import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.github.nmorel.gwtjackson.client.AbstractObjectReader;
import com.github.nmorel.gwtjackson.client.JsonDeserializationContext;
import com.github.nmorel.gwtjackson.client.JsonDeserializerParameters;
import com.github.nmorel.gwtjackson.client.ObjectReader;
import com.github.nmorel.gwtjackson.client.stream.JsonReader;
import com.google.gwt.core.client.GWT;

public class JacksonDeserializationContext extends DeserializationContext {

    @SuppressWarnings("rawtypes")
    com.github.nmorel.gwtjackson.client.JsonDeserializer genericDeserializer;
    @SuppressWarnings("rawtypes")
    com.github.nmorel.gwtjackson.client.deser.map.key.KeyDeserializer genericKeyDeserializer;

    JsonReader reader;
    JsonDeserializationContext context;
    JsonDeserializerParameters params;

    static interface GenericJsonReader extends ObjectReader<Object> {
    }

    protected JacksonDeserializationContext(JsonDeserializationContext context, JsonDeserializerParameters params) {
        super((DeserializationContext) new ObjectMapper().getDeserializationContext());
        this.genericDeserializer = (GWT.<AbstractObjectReader> create(GenericJsonReader.class)).getDeserializer();
        this.genericKeyDeserializer = (GWT.<com.github.nmorel.gwtjackson.client.deser.map.key.KeyDeserializer> create(
                com.github.nmorel.gwtjackson.client.deser.map.key.KeyDeserializer.class));
        this.context = context;
        this.params = params;
    }

    @Override
    public ReadableObjectId findObjectId(Object id, ObjectIdGenerator<?> generator, ObjectIdResolver resolver) {
        IdKey key = generator.key(id);
        Object value = resolver.resolveId(key);
        if (null == value) {
            resolver.bindItem(key, id);
        }
        return new ReadableObjectId(key);
    }

    @Override
    public void checkUnresolvedObjectId() throws UnresolvedForwardReference {
        // TODO Auto-generated method stub

    }

    @Override
    public JsonDeserializer<Object> deserializerInstance(Annotated annotated, Object deserDef)
            throws JsonMappingException {
        return new JsonDeserializer<Object>() {

            @Override
            public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException,
                    JsonProcessingException {
                return genericDeserializer.deserialize(reader, context);
            }

        };
    }

    @Override
    public KeyDeserializer keyDeserializerInstance(Annotated annotated, Object deserDef) throws JsonMappingException {
        // TODO Auto-generated method stub
        return new KeyDeserializer() {

            @Override
            public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException,
                    JsonProcessingException {
                return genericKeyDeserializer.deserialize(key, context);
            }

        };
    }

}
