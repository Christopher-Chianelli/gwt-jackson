package com.github.nmorel.gwtjackson.jackson.client.deser;

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
import com.fasterxml.jackson.core.json.JsonReadContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nmorel.gwtjackson.client.stream.JsonReader;

public class JacksonJsonParser extends JsonParser {

    JsonReader reader;
    Object value;
    String currentName;
    boolean hasClosed;
    boolean isCurrentTokenCleared;
    JsonReadContext parsingContext;
    ObjectCodec codec;

    Stack<String> fieldNameStack;
    Stack<JsonToken> tokenStack;
    Stack<List<Object>> arrayStack;
    Stack<Map<String, Object>> objectStack;
    JsonToken currentToken;
    JsonToken lastClearedToken;
    com.github.nmorel.gwtjackson.client.stream.JsonToken first;

    public JacksonJsonParser(JsonReader reader) {
        this.reader = reader;
        this.hasClosed = false;
        this.isCurrentTokenCleared = true;
        this.parsingContext = JsonReadContext.createRootContext(null);

        this.fieldNameStack = new Stack<String>();
        this.tokenStack = new Stack<JsonToken>();
        this.arrayStack = new Stack<List<Object>>();
        this.objectStack = new Stack<Map<String, Object>>();
        this.codec = new ObjectMapper();
    }

    @Override
    public void clearCurrentToken() {
        lastClearedToken = currentToken;
    }

    @Override
    public void close() throws IOException {
        reader.close();
        hasClosed = true;
    }

    @Override
    public BigInteger getBigIntegerValue() throws IOException {
        if (value instanceof Integer) {
            return BigInteger.valueOf((Integer) value);
        } else {
            return (BigInteger) value;
        }

    }

    @Override
    public byte[] getBinaryValue(Base64Variant base64Convertor) throws IOException {
        return base64Convertor.decode(getText());
    }

    @Override
    public ObjectCodec getCodec() {
        return codec;
    }

    @Override
    public JsonLocation getCurrentLocation() {
        return new JsonLocation(reader.getInput(), reader.getInput().length(), reader.getLineNumber(), reader
                .getColumnNumber());
    }

    @Override
    public String getCurrentName() throws IOException {
        return currentName;
    }

    @Override
    public JsonToken getCurrentToken() {
        if (isCurrentTokenCleared) {
            return null;
        }
        return currentToken;
    }

    @Override
    public int getCurrentTokenId() {
        return currentToken.id();
    }

    @Override
    public BigDecimal getDecimalValue() throws IOException {
        return BigDecimal.valueOf((Double) value);
    }

    @Override
    public double getDoubleValue() throws IOException {
        return (Double) value;
    }

    @Override
    public float getFloatValue() throws IOException {
        return (Float) value;
    }

    @Override
    public int getIntValue() throws IOException {
        return (Integer) value;
    }

    @Override
    public JsonToken getLastClearedToken() {
        return lastClearedToken;
    }

    @Override
    public long getLongValue() throws IOException {
        return (Long) value;
    }

    @Override
    public NumberType getNumberType() throws IOException {
        if (value instanceof Integer) {
            return NumberType.INT;
        } else if (value instanceof BigInteger) {
            return NumberType.BIG_INTEGER;
        } else if (value instanceof Double) {
            return NumberType.DOUBLE;
        }
        throw new IllegalStateException("The value (" + value.toString() + ") is not a number.");
    }

    @Override
    public Number getNumberValue() throws IOException {
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof BigInteger) {
            return (BigInteger) value;
        } else if (value instanceof Double) {
            return (Double) value;
        }
        throw new IllegalStateException("The value (" + value.toString() + ") is not a number.");
    }

    @Override
    public JsonStreamContext getParsingContext() {
        return parsingContext;
    }

    @Override
    public String getText() throws IOException {
        switch (currentToken) {
            case FIELD_NAME:
                return currentName;
            case VALUE_NUMBER_FLOAT:
                return getDecimalValue().toPlainString();
            case VALUE_NUMBER_INT:
                return getBigIntegerValue().toString();
            case VALUE_STRING:
                return (String) value;
            case VALUE_TRUE:
                return "true";
            case VALUE_FALSE:
                return "false";
            case NOT_AVAILABLE:
            case VALUE_NULL:
            case START_ARRAY:
            case START_OBJECT:
            case VALUE_EMBEDDED_OBJECT:
            case END_ARRAY:
            case END_OBJECT:
                return null;
            default:
                throw new IllegalStateException("The token (" + reader.peek().name()
                        + ") does not have a case in JacksonJsonParser.getText().");
        }
    }

    @Override
    public char[] getTextCharacters() throws IOException {
        return getText().toCharArray();
    }

    @Override
    public int getTextLength() throws IOException {
        return getText().length();
    }

    @Override
    public int getTextOffset() throws IOException {
        return 0;
    }

    @Override
    public JsonLocation getTokenLocation() {
        return getCurrentLocation();
    }

    @Override
    public String getValueAsString(String defaultValue) throws IOException {
        String text = getText();
        return (null != text) ? text : defaultValue;
    }

    @Override
    public boolean hasCurrentToken() {
        return !isCurrentTokenCleared;
    }

    @Override
    public boolean hasTextCharacters() {
        return true;
    }

    @Override
    public boolean hasToken(JsonToken token) {
        return currentToken() == token;
    }

    @Override
    public boolean hasTokenId(int tokenId) {
        return currentTokenId() == tokenId;
    }

    @Override
    public boolean isClosed() {
        return hasClosed;
    }

    @Override
    public JsonToken nextToken() throws IOException {
        isCurrentTokenCleared = false;
        if (null == first) {
            first = reader.peek();
        }
        switch (reader.peek()) {
            case BEGIN_ARRAY:
                reader.beginArray();
                parsingContext = parsingContext.createChildArrayContext(reader.getLineNumber(), reader
                        .getColumnNumber());
                tokenStack.add(JsonToken.START_ARRAY);
                arrayStack.add(new ArrayList<Object>());
                currentToken = JsonToken.START_ARRAY;
                break;
            case BEGIN_OBJECT:
                reader.beginObject();
                parsingContext = parsingContext.createChildObjectContext(reader.getLineNumber(), reader
                        .getColumnNumber());
                tokenStack.add(JsonToken.START_OBJECT);
                objectStack.add(new HashMap<String, Object>());
                currentToken = JsonToken.START_OBJECT;
                break;
            case BOOLEAN:
                value = reader.nextBoolean();
                handleToken(value);
                if ((Boolean) value) {
                    currentToken = JsonToken.VALUE_TRUE;
                } else {
                    currentToken = JsonToken.VALUE_FALSE;
                }
                break;
            case END_ARRAY:
                parsingContext = parsingContext.clearAndGetParent();
                value = arrayStack.pop();
                tokenStack.pop();
                handleToken(value);
                reader.endArray();
                currentToken = JsonToken.END_ARRAY;
                break;
            case END_DOCUMENT:
                return convertToJsonValue(first);
            case END_OBJECT:
                parsingContext = parsingContext.clearAndGetParent();
                value = objectStack.pop();
                tokenStack.pop();
                handleToken(value);
                currentToken = JsonToken.END_OBJECT;
                break;
            case NAME:
                fieldNameStack.push(reader.nextName());
                currentName = fieldNameStack.peek();
                parsingContext.setCurrentName(currentName);
                tokenStack.push(JsonToken.FIELD_NAME);
                currentToken = JsonToken.FIELD_NAME;
                break;
            case NULL:
                value = null;
                handleToken(value);
                currentToken = JsonToken.VALUE_NULL;
                break;
            case NUMBER:
                value = reader.nextNumber();
                handleToken(value);
                if (value instanceof Integer || value instanceof BigInteger) {
                    currentToken = JsonToken.VALUE_NUMBER_INT;
                } else {
                    currentToken = JsonToken.VALUE_NUMBER_FLOAT;
                }
                break;
            case STRING:
                value = reader.nextString();
                handleToken(value);
                currentToken = JsonToken.VALUE_STRING;
                break;
            default:
                throw new IllegalStateException("The token (" + reader.peek().name()
                        + ") does not have a case in JacksonJsonParser.nextToken().");

        }
        return currentToken;
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
        parsingContext.setCurrentValue(value);
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
    public void setCodec(ObjectCodec codec) {
        this.codec = codec;
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
