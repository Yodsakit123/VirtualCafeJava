import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

public class CustomerGUI extends Application {
    private ClientApi api;
    private TableView<Order> orderTable;
    private ObservableList<Order> orderList;
    private Label statusLabel;
    
    private static final Type ORDER_LIST_TYPE = new TypeToken<List<Order>>(){}.getType();

    @Override
    public void start(Stage primaryStage) {
        try {
            // Connect to server
            api = new ClientApi("127.0.0.1", 5050);
            
            primaryStage.setTitle("☕ Cafe Order System");
            
            // Main layout
            BorderPane root = new BorderPane();
            root.setPadding(new Insets(10));
            
            // Top: Title and controls
            VBox topBox = new VBox(10);
            topBox.setAlignment(Pos.CENTER);
            
            Label titleLabel = new Label("☕ Cafe Order Management");
            titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
            
            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(Pos.CENTER);
            
            Button placeOrderBtn = new Button("➕ Place Order");
            placeOrderBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10px 20px;");
            placeOrderBtn.setOnAction(e -> showPlaceOrderDialog());
            
            Button refreshBtn = new Button("🔄 Refresh");
            refreshBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10px 20px;");
            refreshBtn.setOnAction(e -> loadOrders());
            
            Button updateStatusBtn = new Button("✏️ Update Status");
            updateStatusBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10px 20px;");
            updateStatusBtn.setOnAction(e -> showUpdateStatusDialog());
            
            buttonBox.getChildren().addAll(placeOrderBtn, refreshBtn, updateStatusBtn);
            topBox.getChildren().addAll(titleLabel, buttonBox);
            
            // Center: Order table
            orderList = FXCollections.observableArrayList();
            orderTable = new TableView<>(orderList);
            
            TableColumn<Order, Integer> idCol = new TableColumn<>("ID");
            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            idCol.setPrefWidth(50);
            
            TableColumn<Order, String> customerCol = new TableColumn<>("Customer");
            customerCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
            customerCol.setPrefWidth(120);
            
            TableColumn<Order, String> itemCol = new TableColumn<>("Item");
            itemCol.setCellValueFactory(new PropertyValueFactory<>("item"));
            itemCol.setPrefWidth(150);
            
            TableColumn<Order, Integer> qtyCol = new TableColumn<>("Qty");
            qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            qtyCol.setPrefWidth(50);
            
            TableColumn<Order, Double> priceCol = new TableColumn<>("Price Each");
            priceCol.setCellValueFactory(new PropertyValueFactory<>("priceEach"));
            priceCol.setPrefWidth(90);
            
            TableColumn<Order, String> totalCol = new TableColumn<>("Total");
            totalCol.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(
                    String.format("£%.2f", cellData.getValue().total())
                )
            );
            totalCol.setPrefWidth(80);
            
            TableColumn<Order, String> statusCol = new TableColumn<>("Status");
            statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
            statusCol.setPrefWidth(120);
            
            // Color-code status
            statusCol.setCellFactory(column -> new TableCell<Order, String>() {
                @Override
                protected void updateItem(String status, boolean empty) {
                    super.updateItem(status, empty);
                    if (empty || status == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(status);
                        switch (status) {
                            case "PENDING":
                                setStyle("-fx-background-color: #FFF3CD; -fx-text-fill: #856404;");
                                break;
                            case "IN_PROGRESS":
                                setStyle("-fx-background-color: #CCE5FF; -fx-text-fill: #004085;");
                                break;
                            case "SERVED":
                                setStyle("-fx-background-color: #D4EDDA; -fx-text-fill: #155724;");
                                break;
                            case "CANCELLED":
                                setStyle("-fx-background-color: #F8D7DA; -fx-text-fill: #721C24;");
                                break;
                            default:
                                setStyle("");
                        }
                    }
                }
            });
            
            orderTable.getColumns().addAll(idCol, customerCol, itemCol, qtyCol, priceCol, totalCol, statusCol);
            
            // Bottom: Status bar
            statusLabel = new Label("Ready");
            statusLabel.setStyle("-fx-padding: 5px;");
            
            root.setTop(topBox);
            root.setCenter(orderTable);
            root.setBottom(statusLabel);
            
            Scene scene = new Scene(root, 900, 600);
            primaryStage.setScene(scene);
            primaryStage.show();
            
            // Load initial orders
            loadOrders();
            
        } catch (Exception e) {
            showError("Connection Error", "Could not connect to server: " + e.getMessage());
            Platform.exit();
        }
    }

    private void loadOrders() {
        try {
            Message resp = api.send(new Message("LIST_ORDERS", "{}"));
            if ("OK".equals(resp.action)) {
                List<Order> orders = DataManager.gson().fromJson(resp.payload, ORDER_LIST_TYPE);
                orderList.setAll(orders);
                statusLabel.setText("Loaded " + orders.size() + " orders");
            } else {
                showError("Error", "Failed to load orders: " + resp.payload);
            }
        } catch (Exception e) {
            showError("Error", "Failed to load orders: " + e.getMessage());
        }
    }

    private void showPlaceOrderDialog() {
        Dialog<Order> dialog = new Dialog<>();
        dialog.setTitle("Place New Order");
        dialog.setHeaderText("Enter order details");
        
        ButtonType placeButtonType = new ButtonType("Place Order", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(placeButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField customerField = new TextField();
        customerField.setPromptText("Customer Name");
        
        TextField itemField = new TextField();
        itemField.setPromptText("Item (e.g., Latte)");
        
        TextField qtyField = new TextField("1");
        qtyField.setPromptText("Quantity");
        
        TextField priceField = new TextField();
        priceField.setPromptText("Price Each (e.g., 3.50)");
        
        grid.add(new Label("Customer:"), 0, 0);
        grid.add(customerField, 1, 0);
        grid.add(new Label("Item:"), 0, 1);
        grid.add(itemField, 1, 1);
        grid.add(new Label("Quantity:"), 0, 2);
        grid.add(qtyField, 1, 2);
        grid.add(new Label("Price Each:"), 0, 3);
        grid.add(priceField, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        
        Platform.runLater(customerField::requestFocus);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == placeButtonType) {
                try {
                    String customer = customerField.getText().trim();
                    String item = itemField.getText().trim();
                    int qty = Integer.parseInt(qtyField.getText().trim());
                    double price = Double.parseDouble(priceField.getText().trim());
                    
                    if (customer.isEmpty() || item.isEmpty()) {
                        showError("Validation Error", "Customer and Item cannot be empty");
                        return null;
                    }
                    
                    JsonObject json = new JsonObject();
                    json.addProperty("customerName", customer);
                    json.addProperty("item", item);
                    json.addProperty("quantity", qty);
                    json.addProperty("priceEach", price);
                    
                    Message resp = api.send(new Message("PLACE_ORDER", json.toString()));
                    if ("CREATED".equals(resp.action)) {
                        Order order = DataManager.gson().fromJson(resp.payload, Order.class);
                        statusLabel.setText("✓ Order #" + order.id + " created successfully");
                        loadOrders();
                        return order;
                    } else {
                        showError("Error", "Failed to create order: " + resp.payload);
                    }
                } catch (NumberFormatException e) {
                    showError("Validation Error", "Quantity and Price must be valid numbers");
                } catch (Exception e) {
                    showError("Error", "Failed to place order: " + e.getMessage());
                }
            }
            return null;
        });
        
        dialog.showAndWait();
    }

    private void showUpdateStatusDialog() {
        Order selectedOrder = orderTable.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showError("Selection Error", "Please select an order to update");
            return;
        }
        
        ChoiceDialog<String> dialog = new ChoiceDialog<>("IN_PROGRESS", 
            "PENDING", "IN_PROGRESS", "SERVED", "CANCELLED");
        dialog.setTitle("Update Order Status");
        dialog.setHeaderText("Order #" + selectedOrder.id + " - " + selectedOrder.item);
        dialog.setContentText("New status:");
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(status -> {
            try {
                JsonObject json = new JsonObject();
                json.addProperty("id", selectedOrder.id);
                json.addProperty("status", status);
                
                Message resp = api.send(new Message("UPDATE_STATUS", json.toString()));
                if ("OK".equals(resp.action)) {
                    statusLabel.setText("✓ Order #" + selectedOrder.id + " updated to " + status);
                    loadOrders();
                } else {
                    showError("Error", "Failed to update status: " + resp.payload);
                }
            } catch (Exception e) {
                showError("Error", "Failed to update status: " + e.getMessage());
            }
        });
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