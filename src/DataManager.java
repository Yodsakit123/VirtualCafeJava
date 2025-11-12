import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class DataManager {
    private static final String FILE = "data.json";
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    public static synchronized void saveCafeState(CafeState state) {
        try (FileWriter w = new FileWriter(FILE)) {
            gson.toJson(state, w);
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
                CafeState state = gson.fromJson(r, CafeState.class);
                if (state == null) state = new CafeState();
                System.out.println("[Data] Loaded from " + FILE);
                return state;
            }
        } catch (Exception e) {
            System.err.println("[Data] Load failed, starting fresh: " + e.getMessage());
            return new CafeState();
        }
    }

    public static Gson gson() { return gson; }
}
