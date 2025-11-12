import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.*;

public class BaristaServer {
    private final int port;
    private final CafeState state;
    private volatile boolean running = true;
    private final ExecutorService pool = Executors.newCachedThreadPool();

    public BaristaServer(int port) {
        this.port = port;
        this.state = DataManager.loadCafeState();

        // Auto-save on shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n[Server] Shutdown hook: saving...");
            DataManager.saveCafeState(state);
        }));
    }

    public void start() throws IOException {
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("[Server] Listening on port " + port);
            while (running) {
                Socket client = server.accept();
                pool.submit(() -> handleClient(client));
            }
        }
    }

    private void handleClient(Socket socket) {
        String remote = socket.getRemoteSocketAddress().toString();
        System.out.println("[Server] Client connected: " + remote);
        try (socket;
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            String line;
            while ((line = in.readLine()) != null) {
                if (line.trim().isEmpty()) continue;  // 🛠️ skip blank input
                try {
                    Message req = DataManager.gson().fromJson(line, Message.class);
                    if (req == null) continue; // 🛠️ invalid JSON, ignore
                    Message resp = process(req);
                    out.write(DataManager.gson().toJson(resp));
                    out.write("\n");
                    out.flush();
                } catch (Exception ex) {
                    System.err.println("[Server] JSON parse error: " + ex.getMessage());
                }
            }

        } catch (Exception e) {
            System.err.println("[Server] Client error " + remote + ": " + e.getMessage());
        } finally {
            System.out.println("[Server] Client disconnected: " + remote);
        }
    }

    private Message process(Message req) {
        try {
            switch (req.action) {
                case "PING":
                    return new Message("PONG", "{}");

                case "LIST_ORDERS": {
                    String json = DataManager.gson().toJson(state.orders);
                    return new Message("OK", json);
                }

                case "PLACE_ORDER": {
                    JsonObject obj = JsonParser.parseString(req.payload).getAsJsonObject();
                    String customer = obj.get("customerName").getAsString();
                    String item = obj.get("item").getAsString();
                    int qty = obj.get("quantity").getAsInt();
                    double price = obj.get("priceEach").getAsDouble();

                    Order o = state.addOrder(customer, item, qty, price);
                    DataManager.saveCafeState(state);
                    String json = DataManager.gson().toJson(o);
                    return new Message("CREATED", json);
                }

                case "UPDATE_STATUS": {
                    JsonObject obj = JsonParser.parseString(req.payload).getAsJsonObject();
                    int id = obj.get("id").getAsInt();
                    String status = obj.get("status").getAsString();
                    boolean ok = state.updateStatus(id, status);
                    if (ok) {
                        DataManager.saveCafeState(state);
                        return new Message("OK", "{\"updated\":true}");
                    } else {
                        return new Message("ERROR", "{\"reason\":\"not_found\"}");
                    }
                }

                default:
                    return new Message("ERROR", "{\"reason\":\"unknown_action\"}");
            }
        } catch (Exception e) {
            return new Message("ERROR", "{\"reason\":\"" + e.getMessage().replace("\"","'") + "\"}");
        }
    }

    public static void main(String[] args) throws Exception {
        int port = (args.length > 0) ? Integer.parseInt(args[0]) : 5050;
        new BaristaServer(port).start();
    }
}
