package com.github.nmorel.gwtjackson.client.deser.jackson;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.UnresolvedForwardReference;
import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import com.fasterxml.jackson.databind.introspect.Annotated;

public class JacksonDeserializationContext extends DeserializationContext {

    protected JacksonDeserializationContext() {
        super((DeserializationContext) new ObjectMapper().getDeserializationContext());
    }

    @Override
    public ReadableObjectId findObjectId(Object id, ObjectIdGenerator<?> generator, ObjectIdResolver resolver) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void checkUnresolvedObjectId() throws UnresolvedForwardReference {
        // TODO Auto-generated method stub

    }

    @Override
    public JsonDeserializer<Object> deserializerInstance(Annotated annotated, Object deserDef) throws JsonMappingException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public KeyDeserializer keyDeserializerInstance(Annotated annotated, Object deserDef) throws JsonMappingException {
        // TODO Auto-generated method stub
        return null;
    }

}
