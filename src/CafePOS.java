import com.google.gson.JsonObject;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CafePOS extends Application {
    private ClientApi api;
    private VBox cartBox;
    private Label subtotalLabel;
    private Label taxLabel;
    private Label totalLabel;
    private TextField customerNameField;
    
    private List<CartItem> cartItems = new ArrayList<>();
    private static final double TAX_RATE = 0.10; // 10% tax
    
    // Menu items database
    private static final Map<String, List<MenuItem>> MENU = new HashMap<>();
    
    static {
        // Coffee Menu
        List<MenuItem> coffee = new ArrayList<>();
        coffee.add(new MenuItem("Espresso", 2.50, "☕", "#8B4513"));
        coffee.add(new MenuItem("Latte", 3.50, "☕", "#D2691E"));
        coffee.add(new MenuItem("Cappuccino", 3.50, "☕", "#CD853F"));
        coffee.add(new MenuItem("Americano", 3.00, "☕", "#A0522D"));
        coffee.add(new MenuItem("Mocha", 4.00, "☕", "#8B4513"));
        coffee.add(new MenuItem("Flat White", 3.80, "☕", "#DEB887"));
        MENU.put("Coffee", coffee);
        
        // Food Menu
        List<MenuItem> food = new ArrayList<>();
        food.add(new MenuItem("Croissant", 3.00, "🥐", "#FFD700"));
        food.add(new MenuItem("Sandwich", 6.50, "🥪", "#90EE90"));
        food.add(new MenuItem("Bagel", 4.00, "🥯", "#F4A460"));
        food.add(new MenuItem("Panini", 7.00, "🥖", "#DAA520"));
        food.add(new MenuItem("Salad", 8.00, "🥗", "#98FB98"));
        food.add(new MenuItem("Wrap", 6.00, "🌯", "#F0E68C"));
        MENU.put("Food", food);
        
        // Desserts Menu
        List<MenuItem> desserts = new ArrayList<>();
        desserts.add(new MenuItem("Brownie", 3.50, "🍫", "#8B4513"));
        desserts.add(new MenuItem("Muffin", 3.00, "🧁", "#FFB6C1"));
        desserts.add(new MenuItem("Cookie", 2.50, "🍪", "#D2691E"));
        desserts.add(new MenuItem("Cake Slice", 4.50, "🍰", "#FFC0CB"));
        desserts.add(new MenuItem("Donut", 2.80, "🍩", "#FF69B4"));
        desserts.add(new MenuItem("Cheesecake", 5.00, "🎂", "#FFDAB9"));
        MENU.put("Desserts", desserts);
        
        // Drinks Menu
        List<MenuItem> drinks = new ArrayList<>();
        drinks.add(new MenuItem("Orange Juice", 3.50, "🍊", "#FFA500"));
        drinks.add(new MenuItem("Smoothie", 5.00, "🥤", "#FF69B4"));
        drinks.add(new MenuItem("Iced Tea", 3.00, "🧋", "#DAA520"));
        drinks.add(new MenuItem("Hot Chocolate", 3.80, "☕", "#8B4513"));
        drinks.add(new MenuItem("Milkshake", 4.50, "🥛", "#FFB6C1"));
        drinks.add(new MenuItem("Water", 1.50, "💧", "#87CEEB"));
        MENU.put("Drinks", drinks);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            api = new ClientApi("127.0.0.1", 5050);
            
            primaryStage.setTitle("☕ Royal Cafe - Point of Sale");
            
            // Main container
            BorderPane root = new BorderPane();
            root.setStyle("-fx-background-color: #f5f5f5;");
            
            // Top: Header
            VBox header = createHeader();
            root.setTop(header);
            
            // Center: Menu + Cart
            HBox mainContent = new HBox(10);
            mainContent.setPadding(new Insets(10));
            
            // Left: Menu
            VBox menuSection = createMenuSection();
            HBox.setHgrow(menuSection, Priority.ALWAYS);
            
            // Right: Cart
            VBox cartSection = createCartSection();
            cartSection.setPrefWidth(350);
            
            mainContent.getChildren().addAll(menuSection, cartSection);
            root.setCenter(mainContent);
            
            Scene scene = new Scene(root, 1400, 800);
            primaryStage.setScene(scene);
            primaryStage.show();
            
        } catch (Exception e) {
            showError("Connection Error", "Could not connect to server: " + e.getMessage());
            Platform.exit();
        }
    }

    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: linear-gradient(to bottom, #6f4e37, #8b6f47);");
        
        Label title = new Label("☕ ROYAL CAFE");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        title.setTextFill(Color.WHITE);
        
        Label subtitle = new Label("Point of Sale System");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        subtitle.setTextFill(Color.web("#f0e68c"));
        
        // Date and time
        Label datetime = new Label(LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy | HH:mm")));
        datetime.setFont(Font.font("Arial", 12));
        datetime.setTextFill(Color.WHITE);
        
        header.getChildren().addAll(title, subtitle, datetime);
        return header;
    }

    private VBox createMenuSection() {
        VBox menuSection = new VBox(15);
        menuSection.setPadding(new Insets(10));
        
        Label menuTitle = new Label("📋 MENU");
        menuTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        // Category tabs
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        for (Map.Entry<String, List<MenuItem>> category : MENU.entrySet()) {
            Tab tab = new Tab(category.getKey());
            tab.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            
            ScrollPane scroll = new ScrollPane(createMenuGrid(category.getValue()));
            scroll.setFitToWidth(true);
            scroll.setStyle("-fx-background: white;");
            
            tab.setContent(scroll);
            tabPane.getTabs().add(tab);
        }
        
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        menuSection.getChildren().addAll(menuTitle, tabPane);
        
        return menuSection;
    }

    private GridPane createMenuGrid(List<MenuItem> items) {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        
        int col = 0;
        int row = 0;
        
        for (MenuItem item : items) {
            VBox itemCard = createMenuItemCard(item);
            grid.add(itemCard, col, row);
            
            col++;
            if (col > 2) { // 3 items per row
                col = 0;
                row++;
            }
        }
        
        return grid;
    }

    private VBox createMenuItemCard(MenuItem item) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setPrefSize(200, 180);
        card.setStyle(String.format(
            "-fx-background-color: white; " +
            "-fx-border-color: #ddd; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 10; " +
            "-fx-background-radius: 10; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);"
        ));
        
        // Icon/Emoji
        Label icon = new Label(item.icon);
        icon.setFont(Font.font(48));
        
        // Item name
        Label name = new Label(item.name);
        name.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        name.setWrapText(true);
        name.setAlignment(Pos.CENTER);
        
        // Price
        Label price = new Label(String.format("£%.2f", item.price));
        price.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        price.setTextFill(Color.web("#2e7d32"));
        
        // Add button
        Button addBtn = new Button("+ ADD");
        addBtn.setStyle(String.format(
            "-fx-background-color: %s; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 14px; " +
            "-fx-padding: 10 30; " +
            "-fx-background-radius: 20; " +
            "-fx-cursor: hand;",
            item.color
        ));
        addBtn.setOnAction(e -> addToCart(item));
        
        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle(card.getStyle() + 
            "-fx-scale-x: 1.05; -fx-scale-y: 1.05; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3);"));
        card.setOnMouseExited(e -> card.setStyle(card.getStyle().replace(
            "-fx-scale-x: 1.05; -fx-scale-y: 1.05;", "")));
        
        card.getChildren().addAll(icon, name, price, addBtn);
        return card;
    }

    private VBox createCartSection() {
        VBox cartSection = new VBox(15);
        cartSection.setPadding(new Insets(10));
        cartSection.setStyle("-fx-background-color: white; -fx-border-color: #ddd; " +
                             "-fx-border-width: 2; -fx-border-radius: 5; " +
                             "-fx-background-radius: 5;");
        
        // Cart header
        HBox cartHeader = new HBox(10);
        cartHeader.setAlignment(Pos.CENTER_LEFT);
        
        Label cartTitle = new Label("🛒 ORDER");
        cartTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button clearBtn = new Button("Clear");
        clearBtn.setStyle("-fx-background-color: #ff5252; -fx-text-fill: white; " +
                         "-fx-font-weight: bold; -fx-cursor: hand;");
        clearBtn.setOnAction(e -> clearCart());
        
        cartHeader.getChildren().addAll(cartTitle, spacer, clearBtn);
        
        // Customer name
        HBox nameBox = new HBox(10);
        nameBox.setAlignment(Pos.CENTER_LEFT);
        Label nameLabel = new Label("Customer:");
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        customerNameField = new TextField();
        customerNameField.setPromptText("Enter name...");
        HBox.setHgrow(customerNameField, Priority.ALWAYS);
        nameBox.getChildren().addAll(nameLabel, customerNameField);
        
        // Cart items
        Label itemsLabel = new Label("Items:");
        itemsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        cartBox = new VBox(10);
        ScrollPane cartScroll = new ScrollPane(cartBox);
        cartScroll.setFitToWidth(true);
        cartScroll.setPrefHeight(300);
        cartScroll.setStyle("-fx-background: transparent;");
        VBox.setVgrow(cartScroll, Priority.ALWAYS);
        
        // Totals
        VBox totalsBox = new VBox(10);
        totalsBox.setPadding(new Insets(15, 0, 0, 0));
        totalsBox.setStyle("-fx-border-color: #ddd; -fx-border-width: 2 0 0 0;");
        
        subtotalLabel = new Label("Subtotal: £0.00");
        subtotalLabel.setFont(Font.font("Arial", 14));
        
        taxLabel = new Label("Tax (10%): £0.00");
        taxLabel.setFont(Font.font("Arial", 14));
        
        totalLabel = new Label("TOTAL: £0.00");
        totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        totalLabel.setTextFill(Color.web("#2e7d32"));
        
        totalsBox.getChildren().addAll(subtotalLabel, taxLabel, totalLabel);
        
        // Action buttons
        VBox actionButtons = new VBox(10);
        
        Button placeOrderBtn = new Button("PLACE ORDER");
        placeOrderBtn.setMaxWidth(Double.MAX_VALUE);
        placeOrderBtn.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white; " +
                               "-fx-font-weight: bold; -fx-font-size: 16px; " +
                               "-fx-padding: 15; -fx-cursor: hand;");
        placeOrderBtn.setOnAction(e -> placeOrder());
        
        Button cancelBtn = new Button("Cancel Order");
        cancelBtn.setMaxWidth(Double.MAX_VALUE);
        cancelBtn.setStyle("-fx-background-color: #757575; -fx-text-fill: white; " +
                          "-fx-font-weight: bold; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> clearCart());
        
        actionButtons.getChildren().addAll(placeOrderBtn, cancelBtn);
        
        cartSection.getChildren().addAll(
            cartHeader, nameBox, itemsLabel, cartScroll, totalsBox, actionButtons
        );
        
        return cartSection;
    }

    private void addToCart(MenuItem item) {
        // Check if item already in cart
        for (CartItem cartItem : cartItems) {
            if (cartItem.menuItem.name.equals(item.name)) {
                cartItem.quantity++;
                updateCartDisplay();
                return;
            }
        }
        
        // Add new item
        cartItems.add(new CartItem(item, 1));
        updateCartDisplay();
    }

    private void updateCartDisplay() {
        cartBox.getChildren().clear();
        
        if (cartItems.isEmpty()) {
            Label empty = new Label("Cart is empty");
            empty.setFont(Font.font("Arial", 14));
            empty.setTextFill(Color.GRAY);
            cartBox.getChildren().add(empty);
        } else {
            for (CartItem item : cartItems) {
                cartBox.getChildren().add(createCartItemRow(item));
            }
        }
        
        updateTotals();
    }

    private HBox createCartItemRow(CartItem cartItem) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10));
        row.setStyle("-fx-background-color: #f9f9f9; -fx-background-radius: 5;");
        
        // Item info
        VBox info = new VBox(5);
        Label name = new Label(cartItem.menuItem.name);
        name.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        Label price = new Label(String.format("£%.2f", cartItem.menuItem.price));
        price.setFont(Font.font("Arial", 11));
        price.setTextFill(Color.GRAY);
        info.getChildren().addAll(name, price);
        HBox.setHgrow(info, Priority.ALWAYS);
        
        // Quantity controls
        HBox qtyBox = new HBox(5);
        qtyBox.setAlignment(Pos.CENTER);
        
        Button minusBtn = new Button("-");
        minusBtn.setStyle("-fx-background-color: #ff5252; -fx-text-fill: white; " +
                         "-fx-font-weight: bold; -fx-min-width: 30; -fx-cursor: hand;");
        minusBtn.setOnAction(e -> {
            if (cartItem.quantity > 1) {
                cartItem.quantity--;
            } else {
                cartItems.remove(cartItem);
            }
            updateCartDisplay();
        });
        
        Label qty = new Label(String.valueOf(cartItem.quantity));
        qty.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        qty.setMinWidth(30);
        qty.setAlignment(Pos.CENTER);
        
        Button plusBtn = new Button("+");
        plusBtn.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white; " +
                        "-fx-font-weight: bold; -fx-min-width: 30; -fx-cursor: hand;");
        plusBtn.setOnAction(e -> {
            cartItem.quantity++;
            updateCartDisplay();
        });
        
        qtyBox.getChildren().addAll(minusBtn, qty, plusBtn);
        
        // Total
        Label itemTotal = new Label(String.format("£%.2f", 
            cartItem.menuItem.price * cartItem.quantity));
        itemTotal.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        itemTotal.setMinWidth(60);
        itemTotal.setAlignment(Pos.CENTER_RIGHT);
        
        row.getChildren().addAll(info, qtyBox, itemTotal);
        return row;
    }

    private void updateTotals() {
        double subtotal = 0;
        for (CartItem item : cartItems) {
            subtotal += item.menuItem.price * item.quantity;
        }
        
        double tax = subtotal * TAX_RATE;
        double total = subtotal + tax;
        
        subtotalLabel.setText(String.format("Subtotal: £%.2f", subtotal));
        taxLabel.setText(String.format("Tax (10%%): £%.2f", tax));
        totalLabel.setText(String.format("TOTAL: £%.2f", total));
    }

    private void placeOrder() {
        if (cartItems.isEmpty()) {
            showError("Empty Cart", "Please add items to your order.");
            return;
        }
        
        String customerName = customerNameField.getText().trim();
        if (customerName.isEmpty()) {
            showError("Customer Name Required", "Please enter customer name.");
            customerNameField.requestFocus();
            return;
        }
        
        try {
            // Send each item as a separate order to the server
            for (CartItem item : cartItems) {
                JsonObject json = new JsonObject();
                json.addProperty("customerName", customerName);
                json.addProperty("item", item.menuItem.name);
                json.addProperty("quantity", item.quantity);
                json.addProperty("priceEach", item.menuItem.price);
                
                Message resp = api.send(new Message("PLACE_ORDER", json.toString()));
                if (!"CREATED".equals(resp.action)) {
                    showError("Error", "Failed to place order: " + resp.payload);
                    return;
                }
            }
            
            // Show success
            double total = 0;
            for (CartItem item : cartItems) {
                total += item.menuItem.price * item.quantity;
            }
            total = total * (1 + TAX_RATE);
            
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Order Placed");
            success.setHeaderText("✅ Order placed successfully!");
            success.setContentText(String.format(
                "Customer: %s\nItems: %d\nTotal: £%.2f\n\nThank you!",
                customerName, cartItems.size(), total
            ));
            success.showAndWait();
            
            clearCart();
            
        } catch (Exception e) {
            showError("Error", "Failed to place order: " + e.getMessage());
        }
    }

    private void clearCart() {
        cartItems.clear();
        customerNameField.clear();
        updateCartDisplay();
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

    // Helper classes
    static class MenuItem {
        String name;
        double price;
        String icon;
        String color;
        
        MenuItem(String name, double price, String icon, String color) {
            this.name = name;
            this.price = price;
            this.icon = icon;
            this.color = color;
        }
    }

    static class CartItem {
        MenuItem menuItem;
        int quantity;
        
        CartItem(MenuItem menuItem, int quantity) {
            this.menuItem = menuItem;
            this.quantity = quantity;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
