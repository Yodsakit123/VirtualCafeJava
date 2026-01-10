import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class BaristaDashboard extends Application {
    private ClientApi api;
    private VBox pendingBox;
    private VBox inProgressBox;
    private VBox servedBox;
    private Label statsLabel;
    private Timeline autoRefresh;
    
    private static final Type ORDER_LIST_TYPE = new TypeToken<List<Order>>(){}.getType();
    private static final DateTimeFormatter TIME_FORMATTER = 
        DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault());

    @Override
    public void start(Stage primaryStage) {
        try {
            api = new ClientApi("127.0.0.1", 5050);
            
            primaryStage.setTitle("☕ Barista Dashboard");
            
            BorderPane root = new BorderPane();
            root.setPadding(new Insets(10));
            root.setStyle("-fx-background-color: #f5f5f5;");
            
            // Top: Title and stats
            VBox topBox = new VBox(10);
            topBox.setAlignment(Pos.CENTER);
            
            Label titleLabel = new Label("☕ BARISTA DASHBOARD");
            titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #6f4e37;");
            
            statsLabel = new Label("Loading...");
            statsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
            
            Button refreshBtn = new Button("🔄 Refresh Now");
            refreshBtn.setStyle("-fx-font-size: 12px;");
            refreshBtn.setOnAction(e -> loadOrders());
            
            topBox.getChildren().addAll(titleLabel, statsLabel, refreshBtn);
            
            // Center: Order columns
            HBox columnsBox = new HBox(15);
            columnsBox.setAlignment(Pos.TOP_CENTER);
            columnsBox.setPadding(new Insets(10));
            
            // Pending column
            VBox pendingColumn = createColumn("⏳ PENDING", "#FFF3CD");
            pendingBox = (VBox) ((ScrollPane) pendingColumn.getChildren().get(1)).getContent();
            
            // In Progress column
            VBox inProgressColumn = createColumn("🔥 IN PROGRESS", "#CCE5FF");
            inProgressBox = (VBox) ((ScrollPane) inProgressColumn.getChildren().get(1)).getContent();
            
            // Served column  
            VBox servedColumn = createColumn("✅ SERVED", "#D4EDDA");
            servedBox = (VBox) ((ScrollPane) servedColumn.getChildren().get(1)).getContent();
            
            columnsBox.getChildren().addAll(pendingColumn, inProgressColumn, servedColumn);
            HBox.setHgrow(pendingColumn, Priority.ALWAYS);
            HBox.setHgrow(inProgressColumn, Priority.ALWAYS);
            HBox.setHgrow(servedColumn, Priority.ALWAYS);
            
            root.setTop(topBox);
            root.setCenter(columnsBox);
            
            Scene scene = new Scene(root, 1200, 700);
            primaryStage.setScene(scene);
            primaryStage.show();
            
            // Initial load
            loadOrders();
            
            // Auto-refresh every 5 seconds
            autoRefresh = new Timeline(new KeyFrame(Duration.seconds(5), e -> loadOrders()));
            autoRefresh.setCycleCount(Timeline.INDEFINITE);
            autoRefresh.play();
            
        } catch (Exception e) {
            showError("Connection Error", "Could not connect to server: " + e.getMessage());
            Platform.exit();
        }
    }

    private VBox createColumn(String title, String bgColor) {
        VBox column = new VBox(10);
        column.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-background-radius: 5;");
        column.setMinWidth(350);
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: black;");
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setAlignment(Pos.CENTER);
        
        VBox orderContainer = new VBox(10);
        orderContainer.setPadding(new Insets(5));
        
        ScrollPane scroll = new ScrollPane(orderContainer);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: " + bgColor + ";");
        scroll.setPrefHeight(500);
        
        column.getChildren().addAll(titleLabel, scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);
        
        return column;
    }

    private void loadOrders() {
        try {
            Message resp = api.send(new Message("LIST_ORDERS", "{}"));
            if ("OK".equals(resp.action)) {
                List<Order> orders = DataManager.gson().fromJson(resp.payload, ORDER_LIST_TYPE);
                
                // Clear columns
                pendingBox.getChildren().clear();
                inProgressBox.getChildren().clear();
                servedBox.getChildren().clear();
                
                // Sort orders into columns
                List<Order> pending = orders.stream()
                    .filter(o -> "PENDING".equals(o.status))
                    .collect(Collectors.toList());
                
                List<Order> inProgress = orders.stream()
                    .filter(o -> "IN_PROGRESS".equals(o.status))
                    .collect(Collectors.toList());
                
                List<Order> served = orders.stream()
                    .filter(o -> "SERVED".equals(o.status))
                    .collect(Collectors.toList());
                
                // Populate columns
                pending.forEach(o -> pendingBox.getChildren().add(createOrderCard(o, "#FFF3CD")));
                inProgress.forEach(o -> inProgressBox.getChildren().add(createOrderCard(o, "#CCE5FF")));
                served.forEach(o -> servedBox.getChildren().add(createOrderCard(o, "#D4EDDA")));
                
                // Update stats
                int cancelled = (int) orders.stream().filter(o -> "CANCELLED".equals(o.status)).count();
                statsLabel.setText(String.format(
                    "📊 Total: %d | Pending: %d | In Progress: %d | Served: %d | Cancelled: %d",
                    orders.size(), pending.size(), inProgress.size(), served.size(), cancelled
                ));
                
            } else {
                showError("Error", "Failed to load orders: " + resp.payload);
            }
        } catch (Exception e) {
            statsLabel.setText("❌ Error loading orders: " + e.getMessage());
        }
    }

    private VBox createOrderCard(Order order, String bgColor) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(15));
        card.setStyle(String.format(
            "-fx-background-color: %s; -fx-border-color: #ddd; " +
            "-fx-border-width: 1; -fx-background-radius: 5; -fx-border-radius: 5;",
            bgColor
        ));
        
        // Order number and time
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label orderNum = new Label("Order #" + order.id);
        orderNum.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: black;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        String timeStr = TIME_FORMATTER.format(order.createdAt);
        Label timeLabel = new Label(timeStr);
        timeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #333;");
        header.getChildren().addAll(orderNum, spacer, timeLabel);
        
        // Customer name
        Label customerLabel = new Label("👤 " + order.customerName);
        customerLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: black;");
        
        // Item details
        Label itemLabel = new Label(String.format("%dx %s", order.quantity, order.item));
        itemLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: black;");
        
        // Price
        Label priceLabel = new Label(String.format("£%.2f", order.total()));
        priceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #28a745; -fx-font-weight: bold;");
        
        card.getChildren().addAll(header, new Separator(), customerLabel, itemLabel, priceLabel);
        
        // Action buttons based on status
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        if ("PENDING".equals(order.status)) {
            Button startBtn = new Button("▶️ Start");
            startBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-weight: bold;");
            startBtn.setOnAction(e -> updateStatus(order.id, "IN_PROGRESS"));
            
            Button cancelBtn = new Button("❌ Cancel");
            cancelBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
            cancelBtn.setOnAction(e -> updateStatus(order.id, "CANCELLED"));
            
            buttonBox.getChildren().addAll(startBtn, cancelBtn);
        } else if ("IN_PROGRESS".equals(order.status)) {
            Button completeBtn = new Button("✅ Complete");
            completeBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;");
            completeBtn.setOnAction(e -> updateStatus(order.id, "SERVED"));
            
            buttonBox.getChildren().add(completeBtn);
        }
        
        if (!buttonBox.getChildren().isEmpty()) {
            card.getChildren().add(buttonBox);
        }
        
        return card;
    }

    private void updateStatus(int orderId, String newStatus) {
        try {
            JsonObject json = new JsonObject();
            json.addProperty("id", orderId);
            json.addProperty("status", newStatus);
            
            Message resp = api.send(new Message("UPDATE_STATUS", json.toString()));
            if ("OK".equals(resp.action)) {
                loadOrders(); // Refresh display
            } else {
                showError("Error", "Failed to update status: " + resp.payload);
            }
        } catch (Exception e) {
            showError("Error", "Failed to update status: " + e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void stop() {
        if (autoRefresh != null) {
            autoRefresh.stop();
        }
        try {
            if (api != null) {
                api.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}