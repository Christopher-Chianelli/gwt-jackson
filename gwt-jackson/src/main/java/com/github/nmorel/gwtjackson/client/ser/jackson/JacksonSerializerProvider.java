package com.github.nmorel.gwtjackson.client.ser.jackson;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.ser.impl.WritableObjectId;
import com.github.nmorel.gwtjackson.client.JsonSerializationContext;
import com.github.nmorel.gwtjackson.client.JsonSerializerParameters;

public class JacksonSerializerProvider extends SerializerProvider {

    JsonSerializationContext context;
    JsonSerializerParameters parameters;

    public JacksonSerializerProvider(JsonSerializationContext context, JsonSerializerParameters parameters) {
        this.context = context;
        this.parameters = parameters;
    }

    @Override
    public WritableObjectId findObjectId(Object forPojo, ObjectIdGenerator<?> generatorType) {
        ObjectIdGenerator<?> generator = context.findObjectIdGenerator(generatorType);
        return new WritableObjectId(generator);
    }

    @Override
    public JsonSerializer<Object> serializerInstance(Annotated annotated, Object serDef) throws JsonMappingException {
        // TODO: Later
        return null;
    }

}
