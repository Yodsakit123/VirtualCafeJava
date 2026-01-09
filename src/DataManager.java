import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

public class DataManager {
    private static final String FILE = "data.json";
    
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
    
    // Gson for file storage (with pretty printing for readability)
    private static final Gson FILE_GSON = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .create();
    
    // Gson for network communication (compact, single line - NO pretty printing)
    private static final Gson NETWORK_GSON = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .create();

    public static synchronized void saveCafeState(CafeState state) {
        try (FileWriter w = new FileWriter(FILE)) {
            FILE_GSON.toJson(state, w);
            System.out.println("[Data] Saved to " + FILE);
        } catch (Exception e) {
            System.err.println("[Data] Save failed: " + e.getMessage());
        }
    }

    public static synchronized CafeState loadCafeState() {
        try {
            if (!Files.exists(Path.of(FILE))) {
                System.out.println("[Data] No existing data, starting fresh.");
                return new CafeState();
            }
            try (FileReader r = new FileReader(FILE)) {
                CafeState state = FILE_GSON.fromJson(r, CafeState.class);
                if (state == null) state = new CafeState();
                System.out.println("[Data] Loaded from " + FILE);
                return state;
            }
        } catch (Exception e) {
            System.err.println("[Data] Load failed, starting fresh: " + e.getMessage());
            return new CafeState();
        }
    }

    // This is used by all network communication (ClientApi, BaristaServer)
    // Returns compact JSON without newlines
    public static Gson gson() { 
        return NETWORK_GSON; 
    }
}
