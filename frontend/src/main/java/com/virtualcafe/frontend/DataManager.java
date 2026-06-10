package com.virtualcafe.frontend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Instant;

public class DataManager {
    
    // Custom adapter for java.time.Instant
    private static class InstantAdapter extends TypeAdapter<Instant> {
        @Override
        public void write(JsonWriter out, Instant value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.toString());
            }
        }

        @Override
        public Instant read(JsonReader in) throws IOException {
            String value = in.nextString();
            return value == null ? null : Instant.parse(value);
        }
    }
    
    // Gson for network communication (compact, single line - NO pretty printing)
    private static final Gson NETWORK_GSON = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .create();

    // This is used by all network communication (ClientApi, BaristaServer)
    public static Gson gson() { 
        return NETWORK_GSON; 
    }
}
