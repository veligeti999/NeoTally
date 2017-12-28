package com.newtally.core.resource;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class JsonParser {

    ThreadLocal<ObjectMapper> parser = new ThreadLocal() {
        @Override
        protected Object initialValue() {
            return new ObjectMapper();
        }
    };

    public <T> T parseObject(Class<T> clazz, String str) {
        return parseObject(clazz, new ByteArrayInputStream(str.getBytes()));
    }

    public <T> T parseObject(Class<T> clazz, InputStream in) {
        try {
            return parser.get().readValue(in, clazz);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse entity of type " + clazz.getSimpleName(), e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                throw new RuntimeException("Failed to close inputstream", e);
            }
        }
    }
}
