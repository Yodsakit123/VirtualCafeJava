package com.virtualcafe.frontend;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ClientApi implements Closeable {
    private final HttpClient httpClient;
    private final String baseUrl;

    public ClientApi(String host, int port) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        // Since we are running on Spring Boot backend, redirect requests to standard port 8080
        this.baseUrl = "http://localhost:8080/api/orders";
    }

    public Message send(Message req) throws IOException {
        try {
            switch (req.action) {
                case "LIST_ORDERS": {
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(baseUrl))
                            .GET()
                            .build();
                    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                    if (response.statusCode() == 200) {
                        return new Message("OK", response.body());
                    } else {
                        return new Message("ERROR", "Failed to retrieve orders. Status: " + response.statusCode());
                    }
                }
                case "PLACE_ORDER": {
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(baseUrl))
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(req.payload))
                            .build();
                    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                    if (response.statusCode() == 201) {
                        return new Message("CREATED", response.body());
                    } else {
                        return new Message("ERROR", "Failed to place order. Status: " + response.statusCode());
                    }
                }
                case "UPDATE_STATUS": {
                    JsonObject obj = JsonParser.parseString(req.payload).getAsJsonObject();
                    int id = obj.get("id").getAsInt();
                    String status = obj.get("status").getAsString();
                    
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(baseUrl + "/" + id + "/status?status=" + status))
                            .PUT(HttpRequest.BodyPublishers.noBody())
                            .build();
                    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                    if (response.statusCode() == 200) {
                        return new Message("OK", "{\"updated\":true}");
                    } else {
                        return new Message("ERROR", "Failed to update order status. Status: " + response.statusCode());
                    }
                }
                default:
                    return new Message("ERROR", "Unknown action: " + req.action);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("HTTP request interrupted", e);
        }
    }

    @Override
    public void close() {
        // HttpClient doesn't need explicit closing in Java 21/25
    }
}
