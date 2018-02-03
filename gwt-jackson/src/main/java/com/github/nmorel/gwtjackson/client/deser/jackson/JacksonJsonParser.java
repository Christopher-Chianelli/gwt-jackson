package com.github.nmorel.gwtjackson.client.deser.jackson;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.Version;
import com.github.nmorel.gwtjackson.client.stream.JsonReader;

public class JacksonJsonParser extends JsonParser {

    JsonReader reader;
    Object value;
    String currentName;
    boolean hasClosed;
    Stack<String> fieldNameStack;
    Stack<JsonToken> tokenStack;
    Stack<List<Object>> arrayStack;
    Stack<Map<String, Object>> objectStack;
    com.github.nmorel.gwtjackson.client.stream.JsonToken first;

    @Override
    public void clearCurrentToken() {
        // TODO
    }

    @Override
    public void close() throws IOException {
        reader.close();
        hasClosed = true;
    }

    @Override
    public BigInteger getBigIntegerValue() throws IOException {
        return BigInteger.valueOf(value);
    }

    @Override
    public byte[] getBinaryValue(Base64Variant arg0) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ObjectCodec getCodec() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JsonLocation getCurrentLocation() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getCurrentName() throws IOException {
        return currentName;
    }

    @Override
    public JsonToken getCurrentToken() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getCurrentTokenId() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public BigDecimal getDecimalValue() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public double getDoubleValue() throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public float getFloatValue() throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getIntValue() throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public JsonToken getLastClearedToken() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getLongValue() throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public NumberType getNumberType() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Number getNumberValue() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JsonStreamContext getParsingContext() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getText() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public char[] getTextCharacters() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getTextLength() throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getTextOffset() throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public JsonLocation getTokenLocation() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getValueAsString(String arg0) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasCurrentToken() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean hasTextCharacters() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean hasToken(JsonToken arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean hasTokenId(int arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isClosed() {
        return hasClosed;
    }

    @Override
    public JsonToken nextToken() throws IOException {
        if (null == first) {
            first = reader.peek();
        }
        switch (reader.peek()) {
            case BEGIN_ARRAY:
                reader.beginArray();
                tokenStack.add(JsonToken.START_ARRAY);
                arrayStack.add(new ArrayList<Object>());
                return JsonToken.START_ARRAY;
            case BEGIN_OBJECT:
                reader.beginObject();
                tokenStack.add(JsonToken.START_OBJECT);
                objectStack.add(new HashMap<String, Object>());
                return JsonToken.START_OBJECT;
            case BOOLEAN:
                value = reader.nextBoolean();
                handleToken(value);
                if ((Boolean) value) {
                    return JsonToken.VALUE_TRUE;
                } else {
                    return JsonToken.VALUE_FALSE;
                }
            case END_ARRAY:
                value = arrayStack.pop();
                tokenStack.pop();
                handleToken(value);
                reader.endArray();
                return JsonToken.END_ARRAY;
            case END_DOCUMENT:
                return convertToJsonValue(first);
            case END_OBJECT:
                value = objectStack.pop();
                tokenStack.pop();
                handleToken(value);
                break;
            case NAME:
                fieldNameStack.push(reader.nextName());
                currentName = fieldNameStack.peek();
                tokenStack.push(JsonToken.FIELD_NAME);
                return JsonToken.FIELD_NAME;
            case NULL:
                value = null;
                handleToken(value);
                return JsonToken.VALUE_NULL;
            case NUMBER:
                // TODO
                break;
            case STRING:
                // TODO
                break;
            default:
                break;

        }
    }

    private JsonToken convertToJsonValue(com.github.nmorel.gwtjackson.client.stream.JsonToken token) {
        switch (token) {
            case BEGIN_ARRAY:
                return JsonToken.START_ARRAY;
            case BEGIN_OBJECT:
                return JsonToken.START_OBJECT;
            case BOOLEAN:
                if ((Boolean) value) {
                    return JsonToken.VALUE_TRUE;
                } else {
                    return JsonToken.VALUE_FALSE;
                }
            case END_ARRAY:
                return JsonToken.END_ARRAY;
            case END_DOCUMENT:
                return JsonToken.END_OBJECT;
            case END_OBJECT:
                return JsonToken.END_OBJECT;
            case NAME:
                return JsonToken.FIELD_NAME;
            case NULL:
                return JsonToken.VALUE_NULL;
            case NUMBER:
                if (value instanceof Integer) {
                    return JsonToken.VALUE_NUMBER_INT;
                } else {
                    return JsonToken.VALUE_NUMBER_FLOAT;
                }
            case STRING:
                return JsonToken.VALUE_STRING;
            default:
                return JsonToken.NOT_AVAILABLE;

        }
    }

    private void handleToken(Object value) {
        switch (tokenStack.peek()) {
            case START_ARRAY:
                arrayStack.peek().add(value);
                break;
            case FIELD_NAME:
                currentName = fieldNameStack.pop();
                objectStack.peek().put(currentName, value);
                break;
            case NOT_AVAILABLE:
            case VALUE_EMBEDDED_OBJECT:
            case VALUE_FALSE:
            case VALUE_NULL:
            case VALUE_NUMBER_FLOAT:
            case VALUE_NUMBER_INT:
            case VALUE_STRING:
            case VALUE_TRUE:
            case END_ARRAY:
            case END_OBJECT:
            default:
                break;

        }
    }

    @Override
    public JsonToken nextValue() throws IOException {
        JsonToken t = nextToken();
        if (t == JsonToken.FIELD_NAME) {
            t = nextToken();
        }
        return t;
    }

    @Override
    public void overrideCurrentName(String newName) {
        fieldNameStack.pop();
        fieldNameStack.push(newName);
        currentName = newName;
    }

    @Override
    public void setCodec(ObjectCodec arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public JsonParser skipChildren() throws IOException {
        reader.skipValue();
        return this;
    }

    @Override
    public Version version() {
        return Version.unknownVersion();
    }

}
